package io.github.lucunji.uusiaurinko.entity;

import com.google.common.collect.Sets;
import io.github.lucunji.uusiaurinko.network.ModDataSerializers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Set;

public class NewSunEntity extends Entity {
    private static final DataParameter<SunState> SUN_STATE = EntityDataManager.createKey(NewSunEntity.class, ModDataSerializers.SUN_STATE);
    private static final DataParameter<Boolean> HAS_WATER_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_FIRE_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_EARTH_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_LIGHTNING_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_POOP_STONE = EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BOOLEAN);
    private static final Set<DataParameter<?>> SIZE_PARAMETERS = Sets.newHashSet(
            SUN_STATE, HAS_WATER_STONE, HAS_FIRE_STONE, HAS_EARTH_STONE, HAS_LIGHTNING_STONE, HAS_POOP_STONE
    );
    private static final int SIZE_INCREMENT_PER_STONE = 1;

    public NewSunEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
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
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putString("SunState", this.getSunState().name());
        compound.putBoolean("Water", this.getHasWaterStone());
        compound.putBoolean("Fire", this.getHasFireStone());
        compound.putBoolean("Earth", this.getHasEarthStone());
        compound.putBoolean("Lightning", this.getHasLightningStone());
        compound.putBoolean("Poop", this.getHasPoopStone());
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
