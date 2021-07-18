package io.github.lucunji.uusiaurinko.item;

import net.minecraft.item.ItemStack;

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
     *
     * @param stack
     */
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
