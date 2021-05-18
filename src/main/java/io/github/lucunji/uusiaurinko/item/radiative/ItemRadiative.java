package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.item.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Items which has special effects when held in hands or thrown.
 */
public abstract class ItemRadiative extends ItemBase {

    public ItemRadiative(Properties properties) {
        super(properties);
    }

    public abstract void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand);

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        boolean inMainHand = EquipmentSlotType.MAINHAND.getSlotIndex() == itemSlot;
        if (inMainHand && isSelected || EquipmentSlotType.OFFHAND.getSlotIndex() == itemSlot) {
            this.radiationInHand(stack, worldIn, entityIn, inMainHand);
        }
    }

    protected static Iterable<BlockPos> randomBlocksAround(BlockPos blockPos, int trials, int xRadius, int zRadius, int yMax, int yMin, Random random) {
        List<BlockPos> list = new ArrayList<>(trials);
        int xRange = xRadius * 2 + 1;
        int yRange = yMax - yMin + 1;
        int zRange = zRadius * 2 + 1;
        for (int i = 0; i < trials; ++i) {
            list.add(blockPos.add(random.nextInt(xRange) - xRadius,
                    random.nextInt(yRange) + yMin, random.nextInt(zRange) - zRadius));
        }
        return list;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return super.createEntity(world, location, itemstack);
    }
}
