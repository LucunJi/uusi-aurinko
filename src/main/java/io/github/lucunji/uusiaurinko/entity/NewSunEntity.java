package io.github.lucunji.uusiaurinko.entity;

import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.item.ModItems;
import io.github.lucunji.uusiaurinko.util.MathUtil;
import io.github.lucunji.uusiaurinko.util.ModDamageSource;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NewSunEntity extends Entity {
    /* A scratch for setting sun's entity data:
    /data merge entity @e[type=uusi-aurinko:new_sun, limit=1] {SunState:"GROWING"}
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * bits:
     * 0: water stone;
     * 1: fire stone;
     * 2: earth stone;
     * 3: lightning stone;
     * 4: poop stone;
     * 5-7: always 0.
     */
    private static final DataParameter<Byte> SYNC_DATA = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BYTE);
    private static final DataParameter<BlockPos> REST_POSITION = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BLOCK_POS);

    private static final int SIZE_INCREMENT_PER_STONE = 1;

    private SunState sunState;
    private int killCount;

    public NewSunEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.sunState = SunState.NEW_BORN;
        this.killCount = 0;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    /* ------------------------------ Major Logic ------------------------------ */

    @Override
    public void tick() {
        super.tick();
        // TODO: test this. Issue: https://github.com/LucunJi/uusi-aurinko/issues/17

        List<Entity> affectedEntities = this.getAffectedEntities();

        doEntityBothSides(affectedEntities);

        if (!world.isRemote) {
            doEntityServerOnly(affectedEntities);
            doBlockServerOnly();
            updateRestPosition(((ServerWorld) this.world));
        }

        // move towards rest position
        if (this.sunState != SunState.NEW_BORN) {
            Vector3d vec = Vector3d.copy(this.getRestPosition()).subtract(getPositionVec());
            if (vec.lengthSquared() > 9) {
                vec = vec.normalize().scale(0.1);
                this.move(MoverType.SELF, vec);
            }
        }

        this.recalculateState();
    }

    private List<Entity> getAffectedEntities() {
        double range = this.getAffectEntityRange();
        return world.getEntitiesInAABBexcluding(this, new AxisAlignedBB(
                        getPosX() + range,
                        getPosY() + range,
                        getPosZ() + range,
                        getPosX() - range,
                        getPosY() - range,
                        getPosZ() - range
                ), entity -> entity.getDistanceSq(getPositionVec()) <= range * range
                        && entity.isAlive()
                        && !entity.isSpectator()
                        && (!(entity instanceof PlayerEntity) || ((PlayerEntity) entity).isCreative())
        );
    }

    private void doEntityBothSides(List<Entity> entities) {
        float attractSpeedBase = 0.06F;
        for (Entity entity : entities) {
            if (ServerConfigs.INSTANCE.NEW_SUN_ATTRACTION_IMMUNE_ENTITY_TYPES.contains(entity)) continue;

            double distance = entity.getDistance(this);
            if (distance == 0) continue;
            // TODO: the calculation is problematic
            Vector3d toSun = MathUtil.getVectorToTargetNormalized(this, entity);
            double rawSpeed = attractSpeedBase * (this.getAffectEntityRange() / distance);
            double attractSpeedMax = attractSpeedBase;
            double realSpeed = Math.min(rawSpeed, attractSpeedMax);
//            LOGGER.info(String.format("raw: %f, max: %f, final: %f", rawSpeed, attractSpeedMax, realSpeed));
            Vector3d attraction = toSun.scale(realSpeed);
            entity.setMotion(entity.getMotion().add(attraction));
        }
    }

    private void doEntityServerOnly(List<Entity> entities) {
        float blazeAmount = ServerConfigs.INSTANCE.NEW_SUN_BLAZE_DAMAGE_AMOUNT.get().floatValue();
        float fusionAmount = ServerConfigs.INSTANCE.NEW_SUN_FUSION_DAMAGE_AMOUNT.get().floatValue();

        for (Entity entity : entities) {
            if (entity.getDistanceSq(this) < MathHelper.squareFloat(getFusionRange())) {
                if (this.sunState == SunState.GROWING
                        && entity instanceof ItemEntity
                        && tryConsumeMagicStoneEntity((ItemEntity) entity)) {
                    entity.remove();
                    continue;
                }

                // remove falling blocks
                if (entity instanceof FallingBlockEntity) {
                    FallingBlockEntity fbe = ((FallingBlockEntity) entity);
                    if (fbe.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                        fbe.entityDropItem(fbe.getBlockState().getBlock());
                    }
                    fbe.remove();
                    continue;
                }

                if (fusionAmount > 0)
                    entity.attackEntityFrom(ModDamageSource.SUN_FUSION, fusionAmount);

            }

            if (blazeAmount > 0) {
                entity.setFire(10);
                entity.attackEntityFrom(ModDamageSource.SUN_BLAZE, blazeAmount);
            }

            if (this.sunState == SunState.NEW_BORN
                    && entity instanceof LivingEntity && ((LivingEntity) entity).getShouldBeDead()
                    && killCount < 100) {
                killCount++;
            }
        }
    }

    private boolean tryConsumeMagicStoneEntity(ItemEntity itemEntity) {
        Item item = itemEntity.getItem().getItem();
        if (item == ModItems.FIRE_STONE.get()) {
            this.setHasFireStone(true);
            return true;
        } else if (item == ModItems.WATER_STONE.get()) {
            this.setHasWaterStone(true);
            return true;
        } else if (item == ModItems.EARTH_STONE.get()) {
            this.setHasEarthStone(true);
            return true;
        } else if (item == ModItems.LIGHTNING_STONE.get()) {
            this.setHasLightningStone(true);
            return true;
        } else if (item == ModItems.POOP_STONE.get()) {
            this.setHasPoopStone(true);
            return true;
        } else {
            return false;
        }
    }

    private void doBlockServerOnly() {
        float range = this.getMeltBlockRange();
        for (BlockPos pos : this.getAffectedBlocks(range)) {
            if (pos.distanceSq(this.getPositionVec(), true) > this.getVaporizeBlockRange()) {
                world.setBlockState(pos, Blocks.FIRE.getDefaultState());
            } else {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3, 512);
            }
        }
    }

    private List<BlockPos> getAffectedBlocks(float searchRange) {
        float searchRangeSq = searchRange * searchRange;
        List<BlockPos> list = BlockPos.getAllInBox(new AxisAlignedBB(
                getPositionVec().subtract(searchRange, searchRange, searchRange),
                getPositionVec().add(searchRange, searchRange, searchRange)))
                .filter(pos -> pos.distanceSq(getPositionVec(), true) <= searchRangeSq)
                .filter(pos -> !(this.world.isAirBlock(pos)))
                .filter(pos -> {
                            BlockState blockState = world.getBlockState(pos);
                            return !blockState.matchesBlock(Blocks.FIRE) &&
                                    !ServerConfigs.INSTANCE.NEW_SUN_DESTROY_BLACKLIST.contains(world.getBlockState(pos));
                        }
                )
                .filter(pos ->
                        pos.equals(
                                this.world.rayTraceBlocks(new RayTraceContext(
                                        this.getPositionVec(),
                                        Vector3d.copy(pos).add(0.5, 0.5, 0.5),
                                        RayTraceContext.BlockMode.COLLIDER,
                                        RayTraceContext.FluidMode.NONE, // ignore fluids
                                        null)
                                ).getPos())
                )
                .map(BlockPos::toImmutable)
                .collect(Collectors.toList());
        Collections.shuffle(list);
        if (list.size() > 10) list = list.subList(0, 10);
        return list;
    }

    private void updateRestPosition(ServerWorld world) {
        RegistryKey<World> dimensionRegistryKey = world.getDimensionKey();
        if (dimensionRegistryKey == World.OVERWORLD) {
            BlockPos spawnPoint = world.getSpawnPoint();
            this.setRestPosition(new BlockPos(spawnPoint.getX(), 200, spawnPoint.getZ()));
        } else if (dimensionRegistryKey == World.THE_NETHER) {
            BlockPos spawnPoint = world.getSpawnPoint();
            this.setRestPosition(new BlockPos(spawnPoint.getX() / 8, 100, spawnPoint.getZ() / 8));
        } else if (dimensionRegistryKey == World.THE_END) {
            this.setRestPosition(new BlockPos(0, 200, 0));
        }
    }

    private void recalculateState() {
        switch (this.sunState) {
            case NEW_BORN:
                if (killCount >= 100) {
                    this.sunState = SunState.GROWING;
                }
                break;
            case GROWING:
                if (getHasFireStone() && getHasEarthStone() && getHasWaterStone() && getHasLightningStone()) {
                    if (getHasPoopStone()) {
                        this.sunState = SunState.FULL_BLACK;
                    } else {
                        this.sunState = SunState.FULL_YELLOW;
                    }
                }
                break;
        }

    }

    /* ------------------------------ Data Sync & Storage ------------------------------ */

    /**
     * Register static instances of {@link net.minecraft.network.datasync.DataParameter} here.
     */
    @Override
    protected void registerData() {
        this.dataManager.register(SYNC_DATA, (byte) 0);
        this.dataManager.register(REST_POSITION, new BlockPos(0, 0, 0));
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        if (compound.contains("SunState")) {
            try {
                this.sunState = SunState.valueOf(compound.getString("SunState"));
            } catch (IllegalArgumentException e) {
                LOGGER.error(e);
            }
        }
        this.setHasWaterStone(compound.getBoolean("Water"));
        this.setHasFireStone(compound.getBoolean("Fire"));
        this.setHasEarthStone(compound.getBoolean("Earth"));
        this.setHasLightningStone(compound.getBoolean("Lightning"));
        this.setHasPoopStone(compound.getBoolean("Poop"));
        this.killCount = compound.getInt("KillCount");
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putString("SunState", this.sunState.name());
        compound.putBoolean("Water", this.getHasWaterStone());
        compound.putBoolean("Fire", this.getHasFireStone());
        compound.putBoolean("Earth", this.getHasEarthStone());
        compound.putBoolean("Lightning", this.getHasLightningStone());
        compound.putBoolean("Poop", this.getHasPoopStone());
        compound.putInt("KillCount", this.killCount);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    /* ------------------------------ Getters & Setters ------------------------------ */

    public float getActualSize() {
        SunState sunState = this.sunState;
        if (sunState == SunState.GROWING) {
            float baseSize = sunState.size;
            if (this.getHasWaterStone()) baseSize += SIZE_INCREMENT_PER_STONE;
            if (this.getHasFireStone()) baseSize += SIZE_INCREMENT_PER_STONE;
            if (this.getHasEarthStone()) baseSize += SIZE_INCREMENT_PER_STONE;
            if (this.getHasLightningStone()) baseSize += SIZE_INCREMENT_PER_STONE;
            if (this.getHasPoopStone()) baseSize += SIZE_INCREMENT_PER_STONE;
            return baseSize;
        } else {
            return sunState.size;
        }
    }

    private float getAffectEntityRange() {
        return this.getActualSize() * 1.3F;
    }

    private float getFireRange() {
        return this.getAffectEntityRange();
    }

    private float getFusionRange() {
        return this.getActualSize() * 0.4F;
    }

    private float getMeltBlockRange() {
        return this.getAffectEntityRange();
    }

    private float getVaporizeBlockRange() {
        return this.getFusionRange();
    }

    public boolean getHasWaterStone() {
        return (this.dataManager.get(SYNC_DATA) & (byte) 0b1) != (byte) 0;
    }

    public void setHasWaterStone(boolean newVal) {
        byte data = this.dataManager.get(SYNC_DATA);
        data = newVal ? (byte) (data | (byte) 0b1) : (byte) (data & (byte) 0b1_1110);
        this.dataManager.set(SYNC_DATA, data);
    }

    public boolean getHasFireStone() {
        return (this.dataManager.get(SYNC_DATA) & (byte) 0b10) != (byte) 0;
    }

    public void setHasFireStone(boolean newVal) {
        byte data = this.dataManager.get(SYNC_DATA);
        data = newVal ? (byte) (data | (byte) 0b10) : (byte) (data & (byte) 0b1_1101);
        this.dataManager.set(SYNC_DATA, data);
    }

    public boolean getHasEarthStone() {
        return (this.dataManager.get(SYNC_DATA) & (byte) 0b100) != (byte) 0;
    }

    public void setHasEarthStone(boolean newVal) {
        byte data = this.dataManager.get(SYNC_DATA);
        data = newVal ? (byte) (data | (byte) 0b100) : (byte) (data & (byte) 0b1_1011);
        this.dataManager.set(SYNC_DATA, data);
    }

    public boolean getHasLightningStone() {
        return (this.dataManager.get(SYNC_DATA) & (byte) 0b1000) != (byte) 0;
    }

    public void setHasLightningStone(boolean newVal) {
        byte data = this.dataManager.get(SYNC_DATA);
        data = newVal ? (byte) (data | (byte) 0b1000) : (byte) (data & (byte) 0b1_0111);
        this.dataManager.set(SYNC_DATA, data);
    }

    public boolean getHasPoopStone() {
        return (this.dataManager.get(SYNC_DATA) & (byte) 0b1_0000) != (byte) 0;
    }

    public void setHasPoopStone(boolean newVal) {
        byte data = this.dataManager.get(SYNC_DATA);
        data = newVal ? (byte) (data | (byte) 0b1_0000) : (byte) (data & (byte) 0b0_1111);
        this.dataManager.set(SYNC_DATA, data);
    }

    private void setRestPosition(BlockPos newVal) {
        this.dataManager.set(REST_POSITION, newVal);
    }

    private BlockPos getRestPosition() {
        return this.dataManager.get(REST_POSITION);
    }

    public enum SunState {
        NEW_BORN(8F),
        GROWING(16F),
        FULL_YELLOW(24F),
        FULL_BLACK(48F);

        public final float size;

        SunState(float size) {
            this.size = size;
        }
    }
}
