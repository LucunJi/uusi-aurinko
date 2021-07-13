package io.github.lucunji.uusiaurinko.entity;

import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.item.ModItems;
import io.github.lucunji.uusiaurinko.network.ModDataSerializers;
import io.github.lucunji.uusiaurinko.util.MathUtil;
import io.github.lucunji.uusiaurinko.util.ModDamageSource;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
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

    private static final DataParameter<SunState> SUN_STATE = EntityDataManager.createKey(NewSunEntity.class, ModDataSerializers.SUN_STATE);
    private static final DataParameter<Boolean> HAS_WATER_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_FIRE_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_EARTH_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_LIGHTNING_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_POOP_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);

    private static final int SIZE_INCREMENT_PER_STONE = 1;

    private int killCount = 0;

    public NewSunEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
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
        }

        if (this.getSunState() != SunState.NEW_BORN) {
            Vector3d vec = new Vector3d(0, 200, 0).subtract(getPositionVec());
            if (vec.lengthSquared() < 9) return;
            vec = vec.normalize().scale(0.1);
            Vector3d sum = getPositionVec().add(vec);
            setPosition(sum.x, sum.y, sum.z);
        }

        this.recalculateState();
    }

    private List<Entity> getAffectedEntities() {
        double range = this.getAffectEntityRange();
        return world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(
                        getPosX() + range,
                        getPosY() + range,
                        getPosZ() + range,
                        getPosX() - range,
                        getPosY() - range,
                        getPosZ() - range
                ), entity -> entity != this
                        && entity.getDistanceSq(getPositionVec()) <= range * range
                        && !entity.isSpectator()
                        && (!(entity instanceof PlayerEntity) || ((PlayerEntity) entity).isCreative())
                // TODO: add blacklist
        );
    }

    private void doEntityBothSides(List<Entity> entities) {
        attractEntity(entities, 0.06);
    }

    private void attractEntity(List<Entity> entityList, double attractSpeedBase) {
        for (Entity entity : entityList) {
            double distance = entity.getDistance(this);
            if (distance == 0) continue;
            // TODO: the calculation is problematic
            Vector3d toSun = MathUtil.getVectorToTargetNormalized(this, entity);
            double rawSpeed = attractSpeedBase * (this.getAffectEntityRange() / distance);
            double attractSpeedMax = attractSpeedBase;
            double realSpeed = Math.min(rawSpeed, attractSpeedMax);
//            LOGGER.info(String.format("raw: %f, max: %f, final: %f", rawSpeed, attractSpeedMax, realSpeed));
            Vector3d e1 = toSun.scale(realSpeed);
            entity.setMotion(entity.getMotion().add(e1));
        }
    }

    private void doEntityServerOnly(List<Entity> entities) {
        if ((world.getGameTime() & 0b111) == 0) damageEntity(entities);
    }

    private void damageEntity(List<Entity> entities) {
        for (Entity entity : entities) {
            if (!entity.isAlive()) continue;

            if (this.getSunState() == SunState.GROWING && entity instanceof ItemEntity) {
                Item item = ((ItemEntity) entity).getItem().getItem();
                boolean flag = true;
                if (item == ModItems.FIRE_STONE.get()) this.setHasFireStone(true);
                else if (item == ModItems.WATER_STONE.get()) this.setHasWaterStone(true);
                else if (item == ModItems.EARTH_STONE.get()) this.setHasEarthStone(true);
                else if (item == ModItems.LIGHTNING_STONE.get()) this.setHasLightningStone(true);
                else if (item == ModItems.POOP_STONE.get()) this.setHasPoopStone(true);
                else flag = false;

                if (flag) {
                    entity.remove();
                    continue;
                }
            }

            entity.setFire(1);
            entity.attackEntityFrom(ModDamageSource.SUN_HEAT, 4);

            if (entity.getDistanceSq(this) < MathHelper.squareFloat(getFusionRange())) {
                entity.attackEntityFrom(ModDamageSource.SUN_FUSION, 6);
                // remove falling blocks
                if (entity instanceof FallingBlockEntity) {
                    FallingBlockEntity fbe = ((FallingBlockEntity) entity);
                    if (fbe.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                        fbe.entityDropItem(fbe.getBlockState().getBlock());
                        fbe.remove();
                    }
                }
            }

            if (this.getSunState() == SunState.NEW_BORN
                    && entity instanceof LivingEntity && ((LivingEntity) entity).getShouldBeDead()
                    && killCount < 100) {
                killCount++;
            }
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

    private void recalculateState() {
        switch (this.getSunState()) {
            case NEW_BORN:
                if (killCount >= 100) {
                    this.setSunState(SunState.GROWING);
                }
                break;
            case GROWING:
                if (getHasFireStone() && getHasEarthStone() && getHasWaterStone() && getHasLightningStone()) {
                    if (getHasPoopStone()) {
                        this.setSunState(SunState.FULL_BLACK);
                    } else {
                        this.setSunState(SunState.FULL_YELLOW);
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
        this.dataManager.register(SUN_STATE, SunState.NEW_BORN);
        this.dataManager.register(HAS_WATER_STONE, false);
        this.dataManager.register(HAS_FIRE_STONE, false);
        this.dataManager.register(HAS_EARTH_STONE, false);
        this.dataManager.register(HAS_LIGHTNING_STONE, false);
        this.dataManager.register(HAS_POOP_STONE, false);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        if (compound.contains("SunState")) {
            try {
                this.setSunState(SunState.valueOf(compound.getString("SunState")));
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
        compound.putString("SunState", this.getSunState().name());
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
        SunState sunState = this.getSunState();
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

    public SunState getSunState() {
        return this.dataManager.get(SUN_STATE);
    }

    public void setSunState(SunState sunState) {
        this.dataManager.set(SUN_STATE, sunState);
    }

    public boolean getHasWaterStone() {
        return this.dataManager.get(HAS_WATER_STONE);
    }

    public void setHasWaterStone(boolean newVal) {
        this.dataManager.set(HAS_WATER_STONE, newVal);
    }

    public boolean getHasFireStone() {
        return this.dataManager.get(HAS_FIRE_STONE);
    }

    public void setHasFireStone(boolean newVal) {
        this.dataManager.set(HAS_FIRE_STONE, newVal);
    }

    public boolean getHasEarthStone() {
        return this.dataManager.get(HAS_EARTH_STONE);
    }

    public void setHasEarthStone(boolean newVal) {
        this.dataManager.set(HAS_EARTH_STONE, newVal);
    }

    public boolean getHasLightningStone() {
        return this.dataManager.get(HAS_LIGHTNING_STONE);
    }

    public void setHasLightningStone(boolean newVal) {
        this.dataManager.set(HAS_LIGHTNING_STONE, newVal);
    }

    public boolean getHasPoopStone() {
        return this.dataManager.get(HAS_POOP_STONE);
    }

    public void setHasPoopStone(boolean newVal) {
        this.dataManager.set(HAS_POOP_STONE, newVal);
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
