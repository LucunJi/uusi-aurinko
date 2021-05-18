package io.github.lucunji.uusiaurinko.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemBase extends Item {
    @OnlyIn(Dist.CLIENT)
    private String tooltipKeyCache = null;

    @OnlyIn(Dist.CLIENT)
    private final long windowHandle = Minecraft.getInstance().getMainWindow().getHandle();

    public ItemBase(Properties properties) {
        super(properties);
    }

    /**
     * Gives the translation key of the current item's tooltip.
     * The key will be cached on the first call to improve performance.
     * @return key of tooltip of the current item.
     */
    @OnlyIn(Dist.CLIENT)
    private String getTooltipKey() {
        if (tooltipKeyCache == null) {
            tooltipKeyCache = this.getTranslationKey()+".tooltip";
        }
        return tooltipKeyCache;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (InputMappings.isKeyDown(windowHandle, 340) || InputMappings.isKeyDown(windowHandle, 344)) {
            tooltip.add(new TranslationTextComponent(this.getTooltipKey()));
            tooltip.add(new TranslationTextComponent("tooltip.uusi-aurinko.shift_less"));
        } else {
            tooltip.add(new TranslationTextComponent("tooltip.uusi-aurinko.shift_more"));
        }
    }
}
