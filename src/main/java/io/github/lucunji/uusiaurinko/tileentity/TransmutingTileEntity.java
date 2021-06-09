package io.github.lucunji.uusiaurinko.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class TransmutingTileEntity extends TileEntity implements ITickableTileEntity {
    private BlockState blockStateFrom;
    private BlockState blockStateTo;
    private float progress;

    public TransmutingTileEntity() {
        super(ModTileEntityTypes.TRANSMUTING_BLOCK.get());
        this.blockStateFrom = Blocks.AIR.getDefaultState();
        this.blockStateTo = this.blockStateFrom;
        this.progress = 0;
    }

    public TransmutingTileEntity(BlockState from, BlockState to, float progress) {
        this();
        this.blockStateFrom = from;
        this.blockStateTo = to;
        this.progress = progress;
    }

    public TransmutingTileEntity(BlockState from, BlockState to) {
        this(from, to, 0);
    }

    @Override
    public void tick() {
        this.progress += 0.1f;
        if (progress >= 1) {
            //noinspection ConstantConditions
            if (!this.world.isRemote) {
                this.world.setBlockState(this.getPos(), blockStateTo);
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.put("From", NBTUtil.writeBlockState(this.blockStateFrom));
        compound.put("To", NBTUtil.writeBlockState(this.blockStateTo));
        compound.putFloat("Progress", progress);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.blockStateFrom = NBTUtil.readBlockState(nbt.getCompound("From"));
        this.blockStateTo = NBTUtil.readBlockState(nbt.getCompound("To"));
        this.progress = nbt.getFloat("Progress");
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("From", NBTUtil.writeBlockState(this.blockStateFrom));
        nbt.put("To", NBTUtil.writeBlockState(this.blockStateTo));
        nbt.putFloat("Progress", progress - 0.1f);
        return new SUpdateTileEntityPacket(getPos(), -1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getNbtCompound();
        this.blockStateFrom = NBTUtil.readBlockState(nbt.getCompound("From"));
        this.blockStateTo = NBTUtil.readBlockState(nbt.getCompound("To"));
        this.progress = nbt.getFloat("Progress");
    }

    public BlockState getBlockStateFrom() {
        return blockStateFrom;
    }

    public BlockState getBlockStateTo() {
        return blockStateTo;
    }

    public float getProgress() {
        return progress;
    }
}
