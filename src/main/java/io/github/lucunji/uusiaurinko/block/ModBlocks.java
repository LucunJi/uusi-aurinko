package io.github.lucunji.uusiaurinko.block;

import io.github.lucunji.uusiaurinko.fluid.ModFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final RegistryObject<SemisolidLavaBlock> SEMISOLID_LAVA = BLOCKS.register("semisolid_lava",
            () -> new SemisolidLavaBlock(AbstractBlock.Properties
                    .create(Material.ROCK, MaterialColor.NETHERRACK)
                    .setLightLevel(state -> 10)
                    .tickRandomly()
                    .hardnessAndResistance(0.3F)
                    .setEmmisiveRendering((a, b, c) -> true) // prevent weird rendering when the block has a light level less than 15
                    .setAllowsSpawn((state, reader, pos, entity) -> entity.isImmuneToFire())));
    public static final RegistryObject<TransmutingBlock> TRANSMUTING_BLOCK = BLOCKS.register("transmuting_block",
            () -> new TransmutingBlock(AbstractBlock.Properties
                    .create(Material.ROCK)
                    .zeroHardnessAndResistance()
                    .noDrops()
                    .notSolid()
                    .setOpaque(AbstractBlock.AbstractBlockState::isOpaqueCube)
                    .setAllowsSpawn((a, b, c, d) -> false)));

    /*==================== Fluids ====================*/
    public static final RegistryObject<FlowingFluidBlock> EXCREMENT = BLOCKS.register("excrement",
            () -> new FlowingFluidBlock(ModFluids.EXCREMENT,
                    AbstractBlock.Properties
                            .create(Material.WATER)
                            .doesNotBlockMovement()
                            .hardnessAndResistance(100)
                            .noDrops()){
                @SuppressWarnings("deprecation")
                @Override
                public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
                    if (worldIn.isRemote || !(entityIn instanceof PlayerEntity)) return;
                    ((PlayerEntity) entityIn).addPotionEffect(new EffectInstance(Effects.HUNGER, 400, 0));
                }
            });
}
