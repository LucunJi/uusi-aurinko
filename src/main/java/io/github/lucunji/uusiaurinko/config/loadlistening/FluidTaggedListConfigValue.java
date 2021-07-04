package io.github.lucunji.uusiaurinko.config.loadlistening;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class FluidTaggedListConfigValue extends TaggedListConfigValue<Fluid> {
    private static final Logger LOGGER = LogManager.getLogger(FluidTaggedListConfigValue.class);

    public FluidTaggedListConfigValue(ForgeConfigSpec.ConfigValue<List<? extends String>> configValue, LoadListeningConfigManagerAbstract manager) {
        super(configValue, manager);
    }

    @Override
    protected Fluid string2SingletonValue(String raw) {
        ResourceLocation resourceLocation = new ResourceLocation(raw);
        IForgeRegistry<Fluid> fluids = ForgeRegistries.FLUIDS;
        ResourceLocation defaultKey = fluids.getDefaultKey();
        Fluid fluid = fluids.getValue(resourceLocation);
        if (fluid == null ||
                !resourceLocation.equals(defaultKey) && fluid == fluids.getValue(defaultKey)) {
            LOGGER.error("Could not interpret the fluid '" + resourceLocation + "'");
            return null;
        }
        return fluid;
    }

    @Override
    public boolean contains(Fluid val) {
        return getSingletons().contains(val) ||
                getTags().stream()
                        .map(FluidTags.getCollection()::getTagByID)
                        .anyMatch(tag -> tag.contains(val));
    }

    public boolean contains(FluidState val) {
        return contains(val.getFluid());
    }
}
