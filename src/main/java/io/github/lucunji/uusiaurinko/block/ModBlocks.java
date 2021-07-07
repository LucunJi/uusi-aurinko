package io.github.lucunji.uusiaurinko.block;

import io.github.lucunji.uusiaurinko.fluid.ModFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
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
                    .setAllowsSpawn((a, b, c, d) -> false)));

    public static final RegistryObject<Block> CHISELED_TEMPLE_BRICKS = BLOCKS.register("chiseled_temple_bricks",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> ITEM_BASE = BLOCKS.register("item_base",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));

    public static final RegistryObject<Block> TEMPLE_BRICKS = BLOCKS.register("temple_bricks",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> TEMPLE_BRICKS_SLAB = BLOCKS.register("temple_bricks_slab",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> TEMPLE_BRICKS_STAIRS = BLOCKS.register("temple_bricks_stairs",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> SHATTERED_TEMPLE_BRICKS = BLOCKS.register("shattered_temple_bricks",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> SHATTERED_TEMPLE_BRICKS_SLAB = BLOCKS.register("shattered_temple_bricks_slab",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> SHATTERED_TEMPLE_BRICKS_STAIRS = BLOCKS.register("shattered_temple_bricks_stairs",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> RUNE_TEMPLE_BRICKS = BLOCKS.register("rune_temple_bricks",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));

    public static final RegistryObject<Block> TEMPLE_STONE = BLOCKS.register("temple_stone",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> TEMPLE_STONE_BRICKS = BLOCKS.register("temple_stone_bricks",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> TEMPLE_STONE_BRICKS_SLAB = BLOCKS.register("temple_stone_bricks_slab",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> TEMPLE_STONE_BRICKS_STAIRS = BLOCKS.register("temple_stone_bricks_stairs",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> TEMPLE_STONE_SLAB = BLOCKS.register("temple_stone_slab",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> TEMPLE_STONE_STAIRS = BLOCKS.register("temple_stone_stairs",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> POLISHED_TEMPLE_STONE = BLOCKS.register("polished_temple_stone",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> POLISHED_TEMPLE_STONE_SLAB = BLOCKS.register("polished_temple_stone_slab",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));
    public static final RegistryObject<Block> POLISHED_TEMPLE_STONE_STAIRS = BLOCKS.register("polished_temple_stone_stairs",
            () -> new Block(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.SAND).setRequiresTool().hardnessAndResistance(50F, 1200F)));


    /*==================== Fluids ====================*/
    public static final RegistryObject<FlowingFluidBlock> EXCREMENT = BLOCKS.register("excrement",
            () -> new ExcrementFluidBlock(ModFluids.EXCREMENT,
                    AbstractBlock.Properties
                            .create(Material.WATER)
                            .hardnessAndResistance(100)
                            .noDrops()));
}
