package io.github.lucunji.uusiaurinko.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.UUID;

public class ThrownRockEntity extends Entity {
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(ItemEntity.class, DataSerializers.ITEMSTACK);

    public ThrownRockEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void registerData() {
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (ITEM.equals(key)) {
            this.getItem().setAttachedEntity(this);
        }
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {

    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public ItemStack getItem() {
        return this.getDataManager().get(ITEM);
    }

    public void setItem(ItemStack stack) {
        this.getDataManager().set(ITEM, stack);
    }
}
