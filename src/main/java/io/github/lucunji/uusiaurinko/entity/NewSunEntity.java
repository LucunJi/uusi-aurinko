package io.github.lucunji.uusiaurinko.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class NewSunEntity extends Entity {
    private SunState sunState;
    private boolean hasWaterStone, hasFireStone, hasEarthStone, hasLightningStone, hasPoopStone;
    private static final int SIZE_INCREMENT_PER_STONE = 1;

    public NewSunEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.sunState = SunState.NEW_BORN;
    }

    public int getActualSize() {
        if (this.sunState == SunState.GROWING) {
            int baseSize = this.sunState.size;
            if (hasWaterStone) baseSize += SIZE_INCREMENT_PER_STONE;
            if (hasFireStone) baseSize += SIZE_INCREMENT_PER_STONE;
            if (hasEarthStone) baseSize += SIZE_INCREMENT_PER_STONE;
            if (hasLightningStone) baseSize += SIZE_INCREMENT_PER_STONE;
            if (hasPoopStone) baseSize += SIZE_INCREMENT_PER_STONE;
            return baseSize;
        } else {
            return this.sunState.size;
        }
    }

    /**
     * Register {@link net.minecraft.network.datasync.EntityDataManager} here.
     */
    @Override
    protected void registerData() {

    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        super.deserializeNBT(compound);
        readAdditional(compound);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = super.serializeNBT();
        writeAdditional(compound);
        return compound;
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        readFromNBT(compound);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        writeToNBT(compound);
    }

    private void readFromNBT(CompoundNBT compound) {
        int state = compound.getInt("SunState");
        this.sunState = SunState.values()[state >= 0 && state < SunState.values().length ? state : 0];
        this.hasWaterStone = compound.getBoolean("Water");
        this.hasFireStone = compound.getBoolean("Fire");
        this.hasEarthStone = compound.getBoolean("Earth");
        this.hasLightningStone = compound.getBoolean("Lightning");
        this.hasPoopStone = compound.getBoolean("Poop");
    }

    private void writeToNBT(CompoundNBT compound) {
        compound.putInt("SunState", sunState.ordinal());
        compound.putBoolean("Water", hasWaterStone);
        compound.putBoolean("Fire", hasFireStone);
        compound.putBoolean("Earth", hasEarthStone);
        compound.putBoolean("Lightning", hasLightningStone);
        compound.putBoolean("Poop", hasPoopStone);
    }

    @Override
    public void handleStatusUpdate(byte id) {
        super.handleStatusUpdate(id);
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    private enum SunState {
        NEW_BORN(10),
        GROWING(20),
        FULL_YELLOW(30),
        FULL_BLACK(60);

        private final int size;

        SunState(int size) {
            this.size = size;
        }
    }
}
