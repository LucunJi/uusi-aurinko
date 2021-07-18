package io.github.lucunji.uusiaurinko.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.UUID;

public class ThrownTabletEntity extends Entity {
    private UUID owner;

    public ThrownTabletEntity(EntityType<?> entityEntityType, World world) {
        super(entityEntityType, world);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void registerData() {
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        if (compound.hasUniqueId("Owner")) {
            this.owner = compound.getUniqueId("Owner");
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        if (this.owner != null) {
            compound.putUniqueId("Owner", this.owner);
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
