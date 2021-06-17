package io.github.lucunji.uusiaurinko.fluid;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import io.github.lucunji.uusiaurinko.item.ModItems;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MODID);

    private static ForgeFlowingFluid.Properties EXCREMENT_PROPERTIES;

    public static final RegistryObject<FlowingFluid> EXCREMENT = FLUIDS.register("excrement", () ->
            new ForgeFlowingFluid.Source(EXCREMENT_PROPERTIES) {
                @Override
                protected boolean canDisplace(FluidState state, IBlockReader world, BlockPos pos, Fluid fluidIn, Direction direction) {
                    return false;
                }
            });
    public static final RegistryObject<FlowingFluid> FLOWING_EXCREMENT = FLUIDS.register("flowing_excrement", () ->
            new ForgeFlowingFluid.Flowing(EXCREMENT_PROPERTIES) {
                @Override
                protected boolean canDisplace(FluidState state, IBlockReader world, BlockPos pos, Fluid fluidIn, Direction direction) {
                    return false;
                }
            });
    public static final ResourceLocation EXCREMENT_FLUID_TAG_LOCATION = new ResourceLocation(MODID, "excrement");

    static {
        EXCREMENT_PROPERTIES = new ForgeFlowingFluid.Properties(
                EXCREMENT,
                FLOWING_EXCREMENT,
                FluidAttributes.builder(
                        new ResourceLocation(MODID, "block/fluid_still"),
                        new ResourceLocation(MODID, "block/fluid_flow"))
                        .overlay(new ResourceLocation(MODID, "block/fluid_overlay"))
                        // ARGB; we use customized opaque overlay/fluid texture, so a better range of alpha value is available
                        // the vanilla strategy uses semi-opaque texture,
                        // and I think that's a bad idea which disallows any opacity beyond water
                        .color(0xF7281804)
                        .viscosity(3000).density(3000)
        ).block(ModBlocks.EXCREMENT).bucket(ModItems.EXCREMENT_BUCKET).levelDecreasePerBlock(2);
    }
}

