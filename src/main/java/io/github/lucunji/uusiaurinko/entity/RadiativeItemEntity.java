package io.github.lucunji.uusiaurinko.entity;

import io.github.lucunji.uusiaurinko.item.radiative.ItemRadiative;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class RadiativeItemEntity extends ItemEntity {
    public RadiativeItemEntity(EntityType<? extends RadiativeItemEntity> p_i50217_1_, World world) {
        super(p_i50217_1_, world);
    }

    public RadiativeItemEntity(World worldIn, double x, double y, double z) {
        this(ModEntityTypes.RADIATIVE_ITEM.get(), worldIn);
        this.setPosition(x, y, z);
        this.rotationYaw = this.rand.nextFloat() * 360.0F;
        this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
    }

    @SuppressWarnings("ConstantConditions")
    public RadiativeItemEntity(World worldIn, double x, double y, double z, ItemStack stack) {
        this(worldIn, x, y, z);
        this.setItem(stack);
        this.lifespan = (stack.getItem() == null ? 6000 : stack.getEntityLifespan(worldIn));
    }

    public RadiativeItemEntity(ItemEntity itemEntity) {
        this(itemEntity.world, itemEntity.getPosX(), itemEntity.getPosY(), itemEntity.getPosZ(), itemEntity.getItem());
        this.setMotion(itemEntity.getMotion());
        this.setOwnerId(itemEntity.getOwnerId());
        this.setThrowerId(itemEntity.getThrowerId());
    }

    @Override
    public boolean isImmuneToExplosions() {
        return ((ItemRadiative) getItem().getItem()).isImmuneToExplosions();
    }

    /**
     * The vanilla render distance for {@link ItemEntity} is too short.
     */
    @Override
    public boolean isInRangeToRenderDist(double distanceSq) {
        double d = 96 * getRenderDistanceWeight();
        return distanceSq < d * d;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    /**
     * This function is used for particle generation.
     * Result from super method has to be wrapped to prevent {@link ClassCastException}.
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemEntity func_234273_t_() {
        return new RadiativeItemEntity(super.func_234273_t_());
    }
}
