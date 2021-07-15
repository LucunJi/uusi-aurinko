package io.github.lucunji.uusiaurinko.entity;

import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.item.ModItems;
import io.github.lucunji.uusiaurinko.util.ModDamageSource;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
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
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class NewSunEntity extends Entity {
    /* A scratch for setting sun's entity data:
    /data merge entity @e[type=uusi-aurinko:new_sun, limit=1] {}
    /data get entity @e[type=uusi-aurinko:new_sun, limit=1]
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * bits:
     * 0: water stone;
     * 1: fire stone;
     * 2: earth stone;
     * 3: lightning stone;
     * 4: poop stone;
     * 5-6: sun state;
     * 7: always 0.
     */
    private static final DataParameter<Byte> SYNC_DATA = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BYTE);
    private static final DataParameter<BlockPos> REST_POSITION = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BLOCK_POS);

    private static final int SIZE_INCREMENT_PER_STONE = 1;

    private int killCount = 0;

    public NewSunEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        float size = this.getActualSize();
        return new EntitySize(size, size, false);
    }

    @Override
    public void recalculateSize() {
        double x = this.getPosX();
        double y = this.getPosY();
        double z = this.getPosZ();

        super.recalculateSize(); // has the side-effect of adding an offset in position when it expands

        this.setPosition(x, y, z);
        AxisAlignedBB box = this.getBoundingBox(this.getPose());
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (SYNC_DATA.equals(key)) {
            this.recalculateSize();
        }
    }

    /* ------------------------------ Major Logic ------------------------------ */

    @Override
    public void tick() {
        super.tick();
        // TODO: test this. Issue: https://github.com/LucunJi/uusi-aurinko/issues/17

        List<Entity> affectedEntities = this.getAffectedEntities();

        doEntityBothSides(affectedEntities);

        // move towards rest position
        if (this.getSunState() != SunState.NEW_BORN) {
            Vector3d vec = Vector3d.copy(this.getRestPosition()).subtract(this.getPositionCenter());
            if (vec.lengthSquared() > 1) {
                this.move(MoverType.SELF, vec.normalize().scale(0.1));
            }
        }

        if (!world.isRemote) {
            long startTime = System.nanoTime();
            doEntityServerOnly(affectedEntities);

            doBlockServerOnly();

            updateRestPosition(((ServerWorld) this.world));

            SunState newState = this.recalculateState();
            if (getSunState() != newState) {
                this.setSunState(newState);
            }
            LOGGER.debug((System.nanoTime() - startTime) / 1000_000D);
        }
    }

    private List<Entity> getAffectedEntities() {
        double radius = this.getAffectEntityRadius();
        return world.getEntitiesInAABBexcluding(this, new AxisAlignedBB(
                        getPosX() + radius,
                        getPosYCenter() + radius,
                        getPosZ() + radius,
                        getPosX() - radius,
                        getPosYCenter() - radius,
                        getPosZ() - radius
                ), entity -> entity.getDistanceSq(getPositionCenter()) <= radius * radius
                        && entity.isAlive()
                        && !entity.isSpectator()
                        && (!(entity instanceof PlayerEntity) || ((PlayerEntity) entity).isCreative())
        );
    }

    private void doEntityBothSides(List<Entity> entities) {
        float attractSpeedBase = 0.06F;
        for (Entity entity : entities) {
            // attract entities
            if (ServerConfigs.INSTANCE.NEW_SUN_ATTRACTION_IMMUNE_ENTITY_TYPES.contains(entity)) continue;

            double distance = MathHelper.sqrt(entity.getDistanceSq(this.getPositionCenter()));
            if (distance == 0) continue;
            // TODO: the calculation is problematic
            Vector3d toSun = this.getPositionCenter().subtract(entity.getPositionVec()).normalize();
            double rawSpeed = attractSpeedBase * (this.getAffectEntityRadius() / distance);
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
            if (entity.getDistanceSq(this.getPositionCenter()) < MathHelper.squareFloat(this.getFusionRadius())) {
                if (this.getSunState() == SunState.GROWING
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

            if (this.getSunState() == SunState.NEW_BORN
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
        int amount = ServerConfigs.INSTANCE.NEW_SUN_DESTROY_RATE.get();
        if (amount == 0) return;
        float radius = this.getMeltBlockRadius();
        for (BlockPos pos : this.getAffectedBlocks(radius, amount)) {
            if (pos.distanceSq(this.getPositionCenter(), true) > this.getVaporizeBlockRadius()
            && Math.random() < 0.3) {
                world.setBlockState(pos, Blocks.FIRE.getDefaultState());
            } else {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3, 512);
            }
        }
    }

    private List<BlockPos> getAffectedBlocks(float radius, int amount) {
        // filter out empty sections
        Vector3d positionCenter = this.getPositionCenter();
        int xMin = (int) (positionCenter.getX() - radius) >> 4;
        int yMin = (int) (positionCenter.getY() - radius) >> 4;
        int zMin = (int) (positionCenter.getZ() - radius) >> 4;
        int xMax = (int) (positionCenter.getX() + radius) >> 4;
        int yMax = (int) (positionCenter.getY() + radius) >> 4;
        int zMax = (int) (positionCenter.getZ() + radius) >> 4;
        LinkedList<SectionPos> sectionPosList = new LinkedList<>();
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    if (this.world.getChunk(x, z).isEmptyBetween(y << 4, (y << 4) | 0b1111)) continue;
                    sectionPosList.add(SectionPos.of(x, y, z));
                }
            }
        }

        List<BlockPos> results = new ArrayList<>();
        if (sectionPosList.isEmpty()) return results;

        // randomly pick blocks in non-empty sections
        float radiusSq = radius * radius;
        Random random = new Random();
        int trial;
        for (trial = 0; trial < 1024 && results.size() < amount; trial++) {
            SectionPos sectionPos = sectionPosList.get(random.nextInt(sectionPosList.size()));
            int x = sectionPos.getWorldStartX() | random.nextInt(16);
            int y = sectionPos.getWorldStartY() | random.nextInt(16);
            int z = sectionPos.getWorldStartZ() | random.nextInt(16);
            BlockPos p = new BlockPos(x, y, z);

            if (p.distanceSq(getPositionCenter(), true) > radiusSq
                    || this.world.isAirBlock(p)) continue;
            BlockState blockState = world.getBlockState(p);
            if (blockState.matchesBlock(Blocks.FIRE)
                    || ServerConfigs.INSTANCE.NEW_SUN_DESTROY_BLACKLIST.contains(world.getBlockState(p))) continue;

            p = this.world.rayTraceBlocks(new RayTraceContext(
                    this.getPositionCenter(),
                    Vector3d.copy(p).add(0.5, 0.5, 0.5),
                    RayTraceContext.BlockMode.COLLIDER,
                    RayTraceContext.FluidMode.NONE, // ignore fluids
                    null)
            ).getPos();
            results.add(p);
        }
        return results;
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

    private SunState recalculateState() {
        if (killCount >= 100) {
            if (getHasFireStone() && getHasEarthStone() && getHasWaterStone() && getHasLightningStone()) {
                if (getHasPoopStone()) {
                    return SunState.FULL_BLACK;
                } else {
                    return SunState.FULL_YELLOW;
                }
            } else {
                return SunState.GROWING;
            }
        } else {
            return SunState.NEW_BORN;
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
        this.setHasWaterStone(compound.getBoolean("Water"));
        this.setHasFireStone(compound.getBoolean("Fire"));
        this.setHasEarthStone(compound.getBoolean("Earth"));
        this.setHasLightningStone(compound.getBoolean("Lightning"));
        this.setHasPoopStone(compound.getBoolean("Poop"));
        this.killCount = compound.getInt("KillCount");
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
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

    private Vector3d getPositionCenter() {
        return this.getPositionVec().add(0, this.getActualSize() / 2D, 0);
    }

    private BlockPos getBlockPosCenter() {
        return new BlockPos(this.getPositionVec().add(0, this.getActualSize() / 2D, 0));
    }

    private double getPosYCenter() {
        return this.getPosY() + this.getActualSize() / 2D;
    }

    private float getAffectEntityRadius() {
        return this.getActualSize() * 1.3F;
    }

    private float getFireRadius() {
        return this.getAffectEntityRadius();
    }

    private float getFusionRadius() {
        return this.getActualSize() * 0.5F;
    }

    private float getMeltBlockRadius() {
        return this.getAffectEntityRadius();
    }

    private float getVaporizeBlockRadius() {
        return this.getFusionRadius();
    }

    public boolean getHasWaterStone() {
        return (this.dataManager.get(SYNC_DATA) & 0b1) != 0;
    }

    public void setHasWaterStone(boolean newVal) {
        byte data = this.dataManager.get(SYNC_DATA);
        data = (byte) (newVal ? data | 0b1 : data & 0b111_1110);
        this.dataManager.set(SYNC_DATA, data);
    }

    public boolean getHasFireStone() {
        return (this.dataManager.get(SYNC_DATA) & 0b10) != 0;
    }

    public void setHasFireStone(boolean newVal) {
        byte data = this.dataManager.get(SYNC_DATA);
        data = (byte) (newVal ? data | 0b10 : data & 0b111_1101);
        this.dataManager.set(SYNC_DATA, data);
    }

    public boolean getHasEarthStone() {
        return (this.dataManager.get(SYNC_DATA) & 0b100) != 0;
    }

    public void setHasEarthStone(boolean newVal) {
        byte data = this.dataManager.get(SYNC_DATA);
        data = (byte) (newVal ? data | 0b100 : data & 0b111_1011);
        this.dataManager.set(SYNC_DATA, data);
    }

    public boolean getHasLightningStone() {
        return (this.dataManager.get(SYNC_DATA) & 0b1000) != 0;
    }

    public void setHasLightningStone(boolean newVal) {
        byte data = this.dataManager.get(SYNC_DATA);
        data = (byte) (newVal ? data | 0b1000 : data & 0b111_0111);
        this.dataManager.set(SYNC_DATA, data);
    }

    public boolean getHasPoopStone() {
        return (this.dataManager.get(SYNC_DATA) & 0b1_0000) != 0;
    }

    public void setHasPoopStone(boolean newVal) {
        byte data = this.dataManager.get(SYNC_DATA);
        data = (byte) (newVal ? data | 0b1_0000 : data & 0b110_1111);
        this.dataManager.set(SYNC_DATA, data);
    }

    public SunState getSunState() {
        return SunState.values()[this.dataManager.get(SYNC_DATA) >>> 5];
    }

    public void setSunState(SunState sunState) {
        byte data = this.dataManager.get(SYNC_DATA);
        data = (byte) (data & 0b001_1111 | sunState.ordinal() << 5);
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
