package io.github.lucunji.uusiaurinko.tileentity;

import io.github.lucunji.uusiaurinko.block.PedestalBlock;
import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.item.radiative.ItemRadiative;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class PedestalTileEntity extends LockableLootTileEntity implements ITickableTileEntity {
    private final NonNullList<ItemStack> content = NonNullList.withSize(1, ItemStack.EMPTY);

    public PedestalTileEntity() {
        super(ModTileEntityTypes.ITEM_PEDESTAL.get());
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        content.set(0, ItemStack.read(nbt.getCompound("Item")));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT result = super.write(compound);
        result.put("Item", this.content.get(0).write(new CompoundNBT()));
        return result;
    }

    /**
     * When the item inside is fetched from outer class, use {@link LockableLootTileEntity#getStackInSlot} instead.
     * This method does not do anything related to loot-table.
     * <p>
     * FIXME: compatibility with loot-table is not ensured.
     */
    @Override
    protected NonNullList<ItemStack> getItems() {
        return content;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);
        update();
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack itemStack = super.removeStackFromSlot(index);
        update();
        return itemStack;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemStack = super.decrStackSize(index, count);
        update();
        return itemStack;
    }

    /**
     * @deprecated Only used by vanilla code to swap items between two chests in constructing
     * {@link net.minecraft.util.palette.UpgradeData.BlockFixers#CHEST}.
     */
    @SuppressWarnings("JavadocReference")
    @Deprecated
    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
    }

    /**
     * Sync data to client. The packet is processed in {@link PedestalTileEntity#onDataPacket} method.
     * To send this packet, use {@link net.minecraft.world.World#notifyBlockUpdate}
     *
     * @return The package from server to client with item data.
     */
    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getPos(), 0, this.content.get(0).write(new CompoundNBT()));
    }

    /**
     * Processes packet with item data from server sent in {@link PedestalTileEntity#getUpdatePacket} method.
     *
     * @param pkt The received packet with item info from server.
     */
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.content.set(0, ItemStack.read(pkt.getNbtCompound()));
    }

    /**
     * Same as {@link PedestalTileEntity#getUpdatePacket} except that it is used in the initial chunk loading
     * it or when many blocks updates at once. It's is packet processed in {@link PedestalTileEntity#handleUpdateTag}
     */
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT result = super.getUpdateTag();
        result.put("Item", this.content.get(0).write(new CompoundNBT()));
        return result;
    }

    /**
     * Processes the packet constructed in {@link PedestalTileEntity#getUpdateTag()} and sent from server to client.
     */
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        content.set(0, ItemStack.read(tag.getCompound("Item")));
    }

    /**
     * The vanilla translation keys for containers are in camel case, which is strange.
     */
    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container." + MODID + ".item_pedestal");
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    /**
     * No menu.
     */
    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return null;
    }

    @Override
    public void tick() {
        if (this.world != null && this.world.isRemote) {
            ItemStack itemStack = this.getStackInSlot(0);
            if (itemStack.getItem() instanceof ItemRadiative) {
                ItemEntity dummyItemEntity = new ItemEntity(this.world,
                        this.getPos().getX() + 0.5, this.getPos().getY() + 1, this.getPos().getZ() + 0.5,
                        itemStack);
                dummyItemEntity.setOnGround(true);
                ((ItemRadiative) itemStack.getItem()).makeParticles(dummyItemEntity);
            }
        }
    }

    private void update() {
        BlockPos pos = this.getPos();
        //noinspection ConstantConditions
        BlockState oldState = this.world.getBlockState(pos);
        BlockState newState = oldState.with(PedestalBlock.POWERED, !content.get(0).isEmpty())
                .with(PedestalBlock.SPECIAL_ITEM, ServerConfigs.INSTANCE.PEDESTAL_SPECIAL_ITEMS.contains(this.content.get(0)));
        // update block
        if (newState != oldState) {
            this.world.setBlockState(pos, newState);
            // it gives strong redstone power to the block below
            this.world.notifyNeighborsOfStateChange(pos.offset(Direction.DOWN), newState.getBlock());
        }
        // update tile entity, only flag 0b1000 matters
        // always send notify because the rendering effect of items changes when they stacks up
        this.world.notifyBlockUpdate(this.getPos(), oldState, newState, 0);
    }
}
