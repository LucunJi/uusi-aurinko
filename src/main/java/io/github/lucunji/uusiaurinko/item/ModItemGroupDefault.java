package io.github.lucunji.uusiaurinko.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroupDefault extends ItemGroup {

    public ModItemGroupDefault(String label) {
        super(label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.SUN_STONE.get());
    }
}