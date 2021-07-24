package io.github.lucunji.uusiaurinko.item;

import io.github.lucunji.uusiaurinko.entity.ModEntityTypes;
import io.github.lucunji.uusiaurinko.entity.ThrownTabletEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ItemEmeraldTablet extends ItemBase {
    public ItemEmeraldTablet(Properties properties) {
        super(properties);
    }

    /**
     * Returns true if this item has an enchantment glint. By default, this returns <code>stack.isItemEnchanted()</code>,
     * but other items can override it (for instance, written books always return true).
     * <p>
     * Note that if you override this method, you generally want to also call the super version (on {@link net.minecraft.item.Item}) to get
     * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
     */
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote && playerIn.getHeldItem(handIn).getItem() == this) {
            playerIn.getHeldItem(handIn).shrink(1);
            playerIn.getCooldownTracker().setCooldown(this, 30);
            // TODO: add sound
            ThrownTabletEntity tabletEntity = new ThrownTabletEntity(ModEntityTypes.EMERALD_TABLET.get(), worldIn);
            tabletEntity.setShooter(playerIn);
            tabletEntity.setPosition(playerIn.getPosX(), playerIn.getPosYEye(), playerIn.getPosZ());
            Vector3d lookVec = playerIn.getLookVec();
            tabletEntity.shoot(lookVec.getX(), lookVec.getY(), lookVec.getZ(), 0.8F, 0);
            worldIn.addEntity(tabletEntity);
        }
        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }
}
