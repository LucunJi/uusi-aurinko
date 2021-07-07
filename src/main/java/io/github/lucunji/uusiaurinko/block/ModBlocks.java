package io.github.lucunji.uusiaurinko.block;

import io.github.lucunji.uusiaurinko.fluid.ModFluids;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

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


    @Itemize
    public static final RegistryObject<Block> TEMPLE_STONE = BLOCKS.register("temple_stone",
            () -> new Block(AbstractBlock.Properties
                    .create(Material.ROCK, MaterialColor.SAND)
                    .setRequiresTool().hardnessAndResistance(50F, 1200F)
            ));
    @Itemize
    public static final RegistryObject<SlabBlock> TEMPLE_STONE_SLAB = BLOCKS.register("temple_stone_slab",
            () -> new SlabBlock(AbstractBlock.Properties.from(TEMPLE_STONE.get())));
    @Itemize
    public static final RegistryObject<StairsBlock> TEMPLE_STONE_STAIRS = BLOCKS.register("temple_stone_stairs",
            () -> new StairsBlock(
                    () -> TEMPLE_STONE_SLAB.get().getDefaultState(),
                    AbstractBlock.Properties.from(TEMPLE_STONE.get())
            ));

    @Itemize
    public static final RegistryObject<Block> POLISHED_TEMPLE_STONE = BLOCKS.register("polished_temple_stone",
            () -> new Block(AbstractBlock.Properties.from(TEMPLE_STONE.get())));
    @Itemize
    public static final RegistryObject<Block> POLISHED_TEMPLE_STONE_SLAB = BLOCKS.register("polished_temple_stone_slab",
            () -> new SlabBlock(AbstractBlock.Properties.from(POLISHED_TEMPLE_STONE.get())));
    @Itemize
    public static final RegistryObject<StairsBlock> POLISHED_TEMPLE_STONE_STAIRS = BLOCKS.register("polished_temple_stone_stairs",
            () -> new StairsBlock(
                    () -> POLISHED_TEMPLE_STONE.get().getDefaultState(),
                    AbstractBlock.Properties.from(POLISHED_TEMPLE_STONE.get())
            ));

    @Itemize
    public static final RegistryObject<Block> TEMPLE_STONE_BRICKS = BLOCKS.register("temple_stone_bricks",
            () -> new Block(AbstractBlock.Properties.from(TEMPLE_STONE.get())));
    @Itemize
    public static final RegistryObject<SlabBlock> TEMPLE_STONE_BRICKS_SLAB = BLOCKS.register("temple_stone_bricks_slab",
            () -> new SlabBlock(AbstractBlock.Properties.from(TEMPLE_STONE_BRICKS.get())));
    @Itemize
    public static final RegistryObject<StairsBlock> TEMPLE_STONE_BRICKS_STAIRS = BLOCKS.register("temple_stone_bricks_stairs",
            () -> new StairsBlock(
                    () -> TEMPLE_STONE_BRICKS.get().getDefaultState(),
                    AbstractBlock.Properties.from(TEMPLE_STONE_BRICKS.get())
            ));

    @Itemize
    public static final RegistryObject<Block> TEMPLE_BRICKS = BLOCKS.register("temple_bricks",
            () -> new Block(AbstractBlock.Properties.from(TEMPLE_STONE.get())));
    @Itemize
    public static final RegistryObject<SlabBlock> TEMPLE_BRICKS_SLAB = BLOCKS.register("temple_bricks_slab",
            () -> new SlabBlock(AbstractBlock.Properties.from(TEMPLE_BRICKS.get())));
    @Itemize
    public static final RegistryObject<StairsBlock> TEMPLE_BRICKS_STAIRS = BLOCKS.register("temple_bricks_stairs",
            () -> new StairsBlock(
                    () -> TEMPLE_STONE_BRICKS.get().getDefaultState(),
                    AbstractBlock.Properties.from(TEMPLE_BRICKS.get())
            ));

    @Itemize
    public static final RegistryObject<Block> SHATTERED_TEMPLE_BRICKS = BLOCKS.register("shattered_temple_bricks",
            () -> new Block(AbstractBlock.Properties.from(TEMPLE_STONE.get())));
    @Itemize
    public static final RegistryObject<SlabBlock> SHATTERED_TEMPLE_BRICKS_SLAB = BLOCKS.register("shattered_temple_bricks_slab",
            () -> new SlabBlock(AbstractBlock.Properties.from(SHATTERED_TEMPLE_BRICKS.get())));
    @Itemize
    public static final RegistryObject<StairsBlock> SHATTERED_TEMPLE_BRICKS_STAIRS = BLOCKS.register("shattered_temple_bricks_stairs",
            () -> new StairsBlock(
                    () -> SHATTERED_TEMPLE_BRICKS.get().getDefaultState(),
                    AbstractBlock.Properties.from(SHATTERED_TEMPLE_BRICKS.get())
            ));

    @Itemize
    public static final RegistryObject<Block> RUNE_TEMPLE_BRICKS = BLOCKS.register("rune_temple_bricks",
            () -> new Block(AbstractBlock.Properties.from(TEMPLE_STONE.get())));

    @Itemize
    public static final RegistryObject<Block> CHISELED_TEMPLE_BRICKS = BLOCKS.register("chiseled_temple_bricks",
            () -> new Block(AbstractBlock.Properties.from(TEMPLE_STONE.get())));

    @Itemize(maxStackSize = 16)
    public static final RegistryObject<PedestalBlock> ITEM_PEDESTAL = BLOCKS.register("item_pedestal",
            () -> new PedestalBlock(AbstractBlock.Properties.from(TEMPLE_STONE.get())));

    /*==================== Fluids ====================*/
    public static final RegistryObject<FlowingFluidBlock> EXCREMENT = BLOCKS.register("excrement",
            () -> new ExcrementFluidBlock(ModFluids.EXCREMENT,
                    AbstractBlock.Properties
                            .create(Material.WATER)
                            .hardnessAndResistance(100)
                            .noDrops()));


    public static Stream<Pair<RegistryObject<Block>, Itemize>> itemizedBlocks() throws RuntimeException {
        return Arrays.stream(ModBlocks.class.getDeclaredFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> field.isAnnotationPresent(Itemize.class))
                .map(field -> {
                    final RegistryObject<Block> blockRegistryObject;
                    try {
                        //noinspection unchecked
                        blockRegistryObject = (RegistryObject<Block>) field.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    Itemize annotation = field.getAnnotation(Itemize.class);
                    return Pair.of(blockRegistryObject, annotation);
                });
    }
}
