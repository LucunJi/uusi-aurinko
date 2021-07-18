package io.github.lucunji.uusiaurinko.item;

import io.github.lucunji.uusiaurinko.effects.ModEffects;
import io.github.lucunji.uusiaurinko.util.ClientUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEvilEye extends ArmorItem {
    public ItemEvilEye(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builderIn) {
        super(materialIn, slot, builderIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (ClientUtil.isShiftDown()) {
            addTranslationAsLines(tooltip, this.getTranslationKey() + ".tooltip");
            addTranslationAsLines(tooltip, "tooltip.uusi-aurinko.shift_less");
        } else {
            addTranslationAsLines(tooltip, "tooltip.uusi-aurinko.shift_more");
        }
    }

    /**
     * fix rendering error of newline symbols (\n) in some languages(such as Chinese) by split lines explicitly
     */
    private static void addTranslationAsLines(List<ITextComponent> tooltip, String translationKey) {
        String[] lines = new TranslationTextComponent(translationKey).getString().split("\n");
        for (String line : lines)
            tooltip.add(new StringTextComponent(line));
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entityIn;
            if (livingEntity.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == this)
                livingEntity.addPotionEffect(new EffectInstance(ModEffects.TRUE_VISION.get(),
                        2, 0, true, true, true));
        }
    }
}
