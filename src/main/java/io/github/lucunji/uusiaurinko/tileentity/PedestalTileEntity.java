package io.github.lucunji.uusiaurinko.tileentity;

import io.github.lucunji.uusiaurinko.network.NetworkManager;
import io.github.lucunji.uusiaurinko.network.ServerPedestalBlockSync;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class PedestalTileEntity extends LockableLootTileEntity {
    private ItemStack content = ItemStack.EMPTY;;

    public PedestalTileEntity() {
        super(ModTileEntityTypes.ITEM_PEDESTAL.get());
    }

    public ItemStack getContent() {
        return content;
    }

    public void setContent(ItemStack content) {
        this.content = content;
        sync();
    }

    public void sync() {
        String threadGroupName = Thread.currentThread().getThreadGroup().getName();
        if ("SERVER".equals(threadGroupName)) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach((player) -> {
                if (player.getPosition().distanceSq(pos) < 32 * 32) {
                    NetworkManager.serverSendToPlayer(new ServerPedestalBlockSync(getPos(), serializeNBT()), player);
                }
            });
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        content = ItemStack.read(nbt.getCompound("item"));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT result = super.write(compound);

        CompoundNBT item = new CompoundNBT();
        this.content.write(item);
        result.put("item", item);

        return result;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return NonNullList.create();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return null;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }
}
