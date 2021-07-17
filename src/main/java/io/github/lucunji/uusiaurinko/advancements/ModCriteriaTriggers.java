package io.github.lucunji.uusiaurinko.advancements;

import com.google.common.collect.Lists;
import io.github.lucunji.uusiaurinko.advancements.criterion.NewSunEntityTrigger;
import net.minecraft.advancements.ICriterionTrigger;

import java.util.List;

public class ModCriteriaTriggers {
    public static final List<ICriterionTrigger<?>> REGISTRY = Lists.newArrayList();

    public static final NewSunEntityTrigger SUMMONED_ENTITY = register(new NewSunEntityTrigger());

    public static <T extends ICriterionTrigger<?>> T register(T criterion) {
        REGISTRY.add(criterion);
        return criterion;
    }
}
