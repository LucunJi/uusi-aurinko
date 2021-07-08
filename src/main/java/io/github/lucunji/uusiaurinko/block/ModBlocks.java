package io.github.lucunji.uusiaurinko.block;

import io.github.lucunji.uusiaurinko.datagen.client.Localize;
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

    @Localize(locales = "zh_cn", translations = "半凝固岩浆")
    public static final RegistryObject<SemisolidLavaBlock> SEMISOLID_LAVA = BLOCKS.register("semisolid_lava",
            () -> new SemisolidLavaBlock(AbstractBlock.Properties
                    .create(Material.ROCK, MaterialColor.NETHERRACK)
                    .setLightLevel(state -> 10)
                    .tickRandomly()
                    .hardnessAndResistance(0.3F)
                    .setEmmisiveRendering((a, b, c) -> true) // prevent weird rendering when the block has a light level less than 15
                    .setAllowsSpawn((state, reader, pos, entity) -> entity.isImmuneToFire())));

    @Localize(locales = "zh_cn", translations = "转化方块")
    public static final RegistryObject<TransmutingBlock> TRANSMUTING_BLOCK = BLOCKS.register("transmuting_block",
            () -> new TransmutingBlock(AbstractBlock.Properties
                    .create(Material.ROCK)
                    .zeroHardnessAndResistance()
                    .noDrops()
                    .notSolid()
                    .setAllowsSpawn((a, b, c, d) -> false)));


    @Itemize
    @Localize(locales = "zh_cn", translations = "神殿石")
    public static final RegistryObject<Block> TEMPLE_STONE = BLOCKS.register("temple_stone",
            () -> new Block(AbstractBlock.Properties
                    .create(Material.ROCK, MaterialColor.SAND)
                    .setRequiresTool().hardnessAndResistance(50F, 1200F)
            ));
    @Itemize
    @Localize(locales = "zh_cn", translations = "神殿石台阶")
    public static final RegistryObject<SlabBlock> TEMPLE_STONE_SLAB = BLOCKS.register("temple_stone_slab",
            () -> new SlabBlock(AbstractBlock.Properties.from(TEMPLE_STONE.get())));
    @Itemize
    @Localize(locales = "zh_cn", translations = "神殿石楼梯")
    public static final RegistryObject<StairsBlock> TEMPLE_STONE_STAIRS = BLOCKS.register("temple_stone_stairs",
            () -> new StairsBlock(
                    () -> TEMPLE_STONE_SLAB.get().getDefaultState(),
                    AbstractBlock.Properties.from(TEMPLE_STONE.get())
            ));

    @Itemize
    @Localize(locales = "zh_cn", translations = "磨制神殿石砖")
    public static final RegistryObject<Block> POLISHED_TEMPLE_STONE = BLOCKS.register("polished_temple_stone",
            () -> new Block(AbstractBlock.Properties.from(TEMPLE_STONE.get())));
    @Itemize
    @Localize(locales = "zh_cn", translations = "磨制神殿石砖台阶")
    public static final RegistryObject<Block> POLISHED_TEMPLE_STONE_SLAB = BLOCKS.register("polished_temple_stone_slab",
            () -> new SlabBlock(AbstractBlock.Properties.from(POLISHED_TEMPLE_STONE.get())));
    @Itemize
    @Localize(locales = "zh_cn", translations = "磨制神殿石砖楼梯")
    public static final RegistryObject<StairsBlock> POLISHED_TEMPLE_STONE_STAIRS = BLOCKS.register("polished_temple_stone_stairs",
            () -> new StairsBlock(
                    () -> POLISHED_TEMPLE_STONE.get().getDefaultState(),
                    AbstractBlock.Properties.from(POLISHED_TEMPLE_STONE.get())
            ));

    @Itemize
    @Localize(locales = "zh_cn", translations = "神殿石砖")
    public static final RegistryObject<Block> TEMPLE_STONE_BRICKS = BLOCKS.register("temple_stone_bricks",
            () -> new Block(AbstractBlock.Properties.from(TEMPLE_STONE.get())));
    @Itemize
    @Localize(locales = "zh_cn", translations = "神殿石砖台阶")
    public static final RegistryObject<SlabBlock> TEMPLE_STONE_BRICK_SLAB = BLOCKS.register("temple_stone_brick_slab",
            () -> new SlabBlock(AbstractBlock.Properties.from(TEMPLE_STONE_BRICKS.get())));
    @Itemize
    @Localize(locales = "zh_cn", translations = "神殿石砖楼梯")
    public static final RegistryObject<StairsBlock> TEMPLE_STONE_BRICK_STAIRS = BLOCKS.register("temple_stone_brick_stairs",
            () -> new StairsBlock(
                    () -> TEMPLE_STONE_BRICKS.get().getDefaultState(),
                    AbstractBlock.Properties.from(TEMPLE_STONE_BRICKS.get())
            ));

    @Itemize
    @Localize(locales = "zh_cn", translations = "神殿砖")
    public static final RegistryObject<Block> TEMPLE_BRICKS = BLOCKS.register("temple_bricks",
            () -> new Block(AbstractBlock.Properties.from(TEMPLE_STONE.get())));
    @Itemize
    @Localize(locales = "zh_cn", translations = "神殿砖台阶")
    public static final RegistryObject<SlabBlock> TEMPLE_BRICK_SLAB = BLOCKS.register("temple_brick_slab",
            () -> new SlabBlock(AbstractBlock.Properties.from(TEMPLE_BRICKS.get())));
    @Itemize
    @Localize(locales = "zh_cn", translations = "神殿砖楼梯")
    public static final RegistryObject<StairsBlock> TEMPLE_BRICK_STAIRS = BLOCKS.register("temple_brick_stairs",
            () -> new StairsBlock(
                    () -> TEMPLE_STONE_BRICKS.get().getDefaultState(),
                    AbstractBlock.Properties.from(TEMPLE_BRICKS.get())
            ));

    @Itemize
    @Localize(locales = "zh_cn", translations = "碎裂神殿石")
    public static final RegistryObject<Block> SHATTERED_TEMPLE_BRICKS = BLOCKS.register("shattered_temple_bricks",
            () -> new Block(AbstractBlock.Properties.from(TEMPLE_STONE.get())));
    @Itemize
    @Localize(locales = "zh_cn", translations = "碎裂神殿石台阶")
    public static final RegistryObject<SlabBlock> SHATTERED_TEMPLE_BRICK_SLAB = BLOCKS.register("shattered_temple_brick_slab",
            () -> new SlabBlock(AbstractBlock.Properties.from(SHATTERED_TEMPLE_BRICKS.get())));
    @Itemize
    @Localize(locales = "zh_cn", translations = "碎裂神殿石楼梯")
    public static final RegistryObject<StairsBlock> SHATTERED_TEMPLE_BRICK_STAIRS = BLOCKS.register("shattered_temple_brick_stairs",
            () -> new StairsBlock(
                    () -> SHATTERED_TEMPLE_BRICKS.get().getDefaultState(),
                    AbstractBlock.Properties.from(SHATTERED_TEMPLE_BRICKS.get())
            ));

    @Itemize(parentModel = MODID + ":block/rune_temple_bricks_1")
    @Localize(locales = "zh_cn", translations = "符文神殿砖")
    public static final RegistryObject<Block> RUNE_TEMPLE_BRICKS = BLOCKS.register("rune_temple_bricks",
            () -> new Block(AbstractBlock.Properties.from(TEMPLE_STONE.get())));

    @Itemize(parentModel = MODID + ":block/chiseled_temple_bricks_1")
    @Localize(locales = "zh_cn", translations = "錾制神殿砖")
    public static final RegistryObject<Block> CHISELED_TEMPLE_BRICKS = BLOCKS.register("chiseled_temple_bricks",
            () -> new Block(AbstractBlock.Properties.from(TEMPLE_STONE.get())));

    @Itemize(maxStackSize = 16, parentModel = MODID + ":block/item_pedestal_off")
    @Localize(locales = "zh_cn", translations = "物品底座")
    public static final RegistryObject<PedestalBlock> ITEM_PEDESTAL = BLOCKS.register("item_pedestal",
            () -> new PedestalBlock(AbstractBlock.Properties.from(TEMPLE_STONE.get())));

    /*==================== Fluids ====================*/
    @Localize(locales = "zh_cn", translations = "排泄物")
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
