package io.github.lucunji.uusiaurinko.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
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
                    .of(Material.STONE, MaterialColor.NETHER)
                    .luminance(state -> 10).ticksRandomly().strength(0.3F)
                    .emissiveLighting((a, b, c) -> true) // prevent weird rendering when the block has a light level less than 15
                    .allowsSpawning((state, reader, pos, entity) -> entity.isFireImmune())));
}
