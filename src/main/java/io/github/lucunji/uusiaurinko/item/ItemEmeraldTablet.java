package io.github.lucunji.uusiaurinko.item;

import io.github.lucunji.uusiaurinko.entity.ModEntityTypes;
import io.github.lucunji.uusiaurinko.entity.ThrownTabletEntity;
import io.github.lucunji.uusiaurinko.util.ClientUtil;
import io.github.lucunji.uusiaurinko.util.ModSoundEvents;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ItemEmeraldTablet extends ItemBase {
    public static final int TYPE_MAX = 2;

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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        int type = this.getTabletType(stack);
        if (type > -1) {
            addTranslationAsLines(tooltip, this.getTranslationKey() + ".tooltip_simple_" + type);
            if (ClientUtil.isShiftDown()) {
                addTranslationAsLines(tooltip, this.getTranslationKey() + ".tooltip_" + type);
                addTranslationAsLines(tooltip, "tooltip.uusi-aurinko.shift_less");
            } else {
                addTranslationAsLines(tooltip, "tooltip.uusi-aurinko.shift_more");
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote && this.getTabletType(stack) == -1) {
            this.setTabletType(stack, new Random().nextInt(TYPE_MAX + 1));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote && playerIn.getHeldItem(handIn).getItem() == this) {
            playerIn.getCooldownTracker().setCooldown(this, 30);
            worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(),
                    ModSoundEvents.ENTITY_EMERALD_TABLET_THROW.get(), SoundCategory.NEUTRAL,
                    0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

            ThrownTabletEntity tabletEntity = new ThrownTabletEntity(ModEntityTypes.EMERALD_TABLET.get(), worldIn);
            tabletEntity.setTabletType(this.getTabletType(playerIn.getHeldItem(handIn)));
            if (playerIn.isCreative()) tabletEntity.setDisappear(true);
            tabletEntity.setShooter(playerIn);
            tabletEntity.setPosition(playerIn.getPosX(), playerIn.getPosYEye(), playerIn.getPosZ());
            Vector3d lookVec = playerIn.getLookVec();
            tabletEntity.shoot(lookVec.getX(), lookVec.getY(), lookVec.getZ(), 0.8F, 0);
            worldIn.addEntity(tabletEntity);
            playerIn.addStat(Stats.ITEM_USED.get(this), 1);
            playerIn.getHeldItem(handIn).shrink(1); // put shrink in the last step, or it will get an empty item stack somewhere
        }
        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }

    public void setTabletType(ItemStack stack, int type) {
        stack.getOrCreateTag().putInt("TabletType", type);
    }

    @SuppressWarnings({"ConstantConditions", "BooleanMethodIsAlwaysInverted"})
    public int getTabletType(ItemStack itemStack) {
        if (itemStack.hasTag()) {
            int type = itemStack.getTag().getInt("TabletType");
            return type >= -1 && type <= TYPE_MAX ? type : -1;
        }
        return -1;
    }
}
