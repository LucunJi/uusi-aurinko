package io.github.lucunji.uusiaurinko.entity;

import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.item.ModItems;
import io.github.lucunji.uusiaurinko.network.ModDataSerializers;
import io.github.lucunji.uusiaurinko.util.ModDamageSource;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;
import java.util.stream.Collectors;

public class NewSunEntity extends Entity {
    private static final DataParameter<SunState> SUN_STATE = EntityDataManager.createKey(NewSunEntity.class, ModDataSerializers.SUN_STATE);
    private static final DataParameter<Boolean> HAS_WATER_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_FIRE_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_EARTH_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_LIGHTNING_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_POOP_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);

    private static final int SIZE_INCREMENT_PER_STONE = 1;

    private static final Item[] STONES = new Item[]{
            ModItems.FIRE_STONE.get(),
            ModItems.WATER_STONE.get(),
            ModItems.EARTH_STONE.get(),
            ModItems.LIGHTNING_STONE.get(),
            ModItems.POOP_STONE.get()
    };

    private int killCount = 0;

    public NewSunEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        entityPart(getAttractRange());

        if (!world.isRemote) {
            blockPart((int) getAttractRange());

            if (getDataManager().get(SUN_STATE) != SunState.NEW_BORN) {
                Vector3d add = new Vector3d(0, 200, 0).subtract(getPositionVec()).normalize().scale(0.05);
                Vector3d a = getPositionVec().add(add);
                setPosition(a.x, a.y, a.z);
            }
        }
    }

    void entityPart(float range) {
        List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(
                getPosX() + range,
                getPosY() + range,
                getPosZ() + range,
                getPosX() - range,
                getPosY() - range,
                getPosZ() - range
        ), (entity) -> entity.getDistanceSq(getPositionVec()) <= range * range);

        attractEntity(entityList, 0.1F);
        hitEntity(entityList);
    }

    void hitEntity(List<Entity> entityList) {
        if (world.getGameTime() % 10 != 0) return;

        entityList.forEach(entity -> {
            entity.setFire(1);
            entity.attackEntityFrom(ModDamageSource.SUN_HEAT, 4);

            if (getDataManager().get(SUN_STATE) == SunState.GROWING && entity instanceof ItemEntity) {
                Item item = ((ItemEntity) entity).getItem().getItem();

                if (item == STONES[0]) {
                    getDataManager().set(HAS_FIRE_STONE, true);
                }
                else if (item == STONES[1]) {
                    getDataManager().set(HAS_WATER_STONE, true);
                }
                else if (item == STONES[2]) {
                    getDataManager().set(HAS_EARTH_STONE, true);
                }
                else if (item == STONES[3]) {
                    getDataManager().set(HAS_LIGHTNING_STONE, true);
                }
                else if (item == STONES[4]) {
                    getDataManager().set(HAS_POOP_STONE, true);
                }

                if (getHasFireStone() && getHasEarthStone() && getHasWaterStone() && getHasLightningStone()) {
                    if (getHasPoopStone()) {
                        getDataManager().set(SUN_STATE, SunState.FULL_BLACK);
                    }
                    else {
                        getDataManager().set(SUN_STATE, SunState.FULL_YELLOW);
                    }
                }
            }
        });

        entityList.stream().filter(entity ->
                        entity instanceof LivingEntity &&
                        entity.getDistanceSq(this) < (getAttractRangeSq() / 4F)
        ).forEach(entity -> {
            entity.attackEntityFrom(ModDamageSource.SUN_NUCLEAR, 6);

            if (getDataManager().get(SUN_STATE) == SunState.NEW_BORN && ((LivingEntity) entity).getHealth() <= 0 && killCount <= 100) {
                killCount++;

                if (killCount == 100) {
                    getDataManager().set(SUN_STATE, SunState.GROWING);
                }
            }
        });
    }

    void attractEntity(List<Entity> entityList, float attractSpeedBase) {
        entityList.forEach(entity -> {
            Vector3d toSun = getVectorToTarget(this, entity);
            Vector3d toEntity = getVectorToTarget(entity, this);
            Vector3d c1 = toSun.subtract(toEntity);
            Vector3d d1 = c1.scale(1 / c1.length());
            float realSpeed = Math.min(attractSpeedBase * (getAttractRange() / entity.getDistance(this)), attractSpeedBase * 2);
            Vector3d e1 = d1.scale(realSpeed);
            entity.setMotion(entity.getMotion().add(e1));
        });
    }

    private Vector3d getVectorToTarget(Entity a, Entity b) {
        return a.getPositionVec().subtract(b.getPositionVec()).normalize();
    }

    void blockPart(int range) {
        findCanDestroyBlocks(range).forEach(pos -> {
            world.destroyBlock(pos, false);
            world.setBlockState(pos, Fluids.FLOWING_LAVA.getDefaultState().getBlockState().with(FlowingFluidBlock.LEVEL, 2));
        });
    }

    List<BlockPos> findCanDestroyBlocks(int searchRange) {
        return BlockPos.getAllInBox(new AxisAlignedBB(
                        getPositionVec().subtract(searchRange, searchRange, searchRange),
                        getPositionVec().add(searchRange, searchRange, searchRange)))
                .filter((pos) -> {
                    BlockState blockState = world.getBlockState(pos);
                    return !blockState.isAir() &&
                            blockState.getBlock() != Blocks.BEDROCK &&
                            blockState.getFluidState().getFluid() != Fluids.LAVA &&
                            blockState.getFluidState().getFluid() != Fluids.FLOWING_LAVA &&
                            pos.distanceSq(getPosition()) <= searchRange * searchRange &&
                            !ServerConfigs.INSTANCE.NEW_SUN_DESTROY_BLACKLIST.contains(world.getBlockState(pos));
                }
        ).map(BlockPos::toImmutable).collect(Collectors.toList());
    }

    float getAttractRange() {
        return (getActualSize() * 1.6F) / 2F;
    }

    float getAttractRangeSq() {
        return getAttractRange() * getAttractRange();
    }

    /*
    /data merge entity @e[type=uusi-aurinko:new_sun, limit=1] {SunState:"GROWING"}
     */

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

    /**
     * Register {@link net.minecraft.network.datasync.EntityDataManager} here.
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
                this.setSunState(SunState.NEW_BORN);
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
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
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
