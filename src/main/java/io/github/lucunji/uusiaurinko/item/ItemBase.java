package io.github.lucunji.uusiaurinko.item;

import io.github.lucunji.uusiaurinko.util.ClientUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemBase extends Item {
    public ItemBase(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
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
}
