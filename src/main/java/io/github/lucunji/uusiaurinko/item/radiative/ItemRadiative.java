package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.entity.EntityTypes;
import io.github.lucunji.uusiaurinko.entity.ThrownRockEntity;
import io.github.lucunji.uusiaurinko.item.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
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

    public abstract void radiationInWorld(ItemStack stack, ThrownRockEntity rockEntity);

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

    @SuppressWarnings("ConstantConditions")
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote()) {
            ThrownRockEntity entity = EntityTypes.THROWN_ROCK.create(worldIn);
            ItemStack itemStack = playerIn.getHeldItem(handIn);
            Vector3d userPosVec = playerIn.getPositionVec();
            entity.setPosition(userPosVec.x, playerIn.getPosYEye() - 0.1, userPosVec.z);
            entity.setItem(itemStack.copy());
            entity.setOwnerUUID(playerIn.getUniqueID());
            if (!playerIn.abilities.isCreativeMode) {
                itemStack.shrink(1);
            }
            entity.setDirectionAndMovement(playerIn, playerIn.rotationPitch, playerIn.rotationYaw,
                    0.0F, 1.5F, 1.0F);
            worldIn.addEntity(entity);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public Entity createEntity(World world, Entity oldEntity, ItemStack itemStack) {
        ThrownRockEntity entity = EntityTypes.THROWN_ROCK.create(world);
        Vector3d posVec = oldEntity.getPositionVec();
        entity.setPosition(posVec.x, posVec.y, posVec.z);
        entity.setMotion(oldEntity.getMotion());
        entity.setItem(itemStack);
        if (oldEntity instanceof ItemEntity) entity.setOwnerUUID(((ItemEntity) oldEntity).getOwnerId());
        return entity;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }
}
