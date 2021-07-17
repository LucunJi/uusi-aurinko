package io.github.lucunji.uusiaurinko.advancements.criterion;

import com.google.gson.JsonObject;
import io.github.lucunji.uusiaurinko.entity.NewSunEntity;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

/**
 * The code currently is exactly the same as {@link net.minecraft.advancements.criterion.SummonedEntityTrigger}.
 * It takes a different registry name to distinguish from vanilla's event minecraft:summoned_entity.
 */
public class NewSunEntityTrigger extends AbstractCriterionTrigger<NewSunEntityTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(MODID, "new_sun_entity");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public NewSunEntityTrigger.Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate playerPredicate, ConditionArrayParser conditionsParser) {
        EntityPredicate.AndPredicate entityPredicate = EntityPredicate.AndPredicate.deserializeJSONObject(json, "entity", conditionsParser);
        return new NewSunEntityTrigger.Instance(playerPredicate, entityPredicate);
    }

    public void trigger(ServerPlayerEntity player, NewSunEntity entity) {
        LootContext lootcontext = EntityPredicate.getLootContext(player, entity);
        this.triggerListeners(player, (instance) -> instance.test(lootcontext));
    }

    public static class Instance extends CriterionInstance {
        private final EntityPredicate.AndPredicate entity;

        public Instance(EntityPredicate.AndPredicate player, EntityPredicate.AndPredicate entity) {
            super(NewSunEntityTrigger.ID, player);
            this.entity = entity;
        }

        public static NewSunEntityTrigger.Instance sunUpdate(EntityPredicate.Builder entityBuilder) {
            return new NewSunEntityTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND,
                    EntityPredicate.AndPredicate.createAndFromEntityCondition(entityBuilder.build()));
        }

        public boolean test(LootContext lootContext) {
            return this.entity.testContext(lootContext);
        }

        @Override
        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonobject = super.serialize(conditions);
            jsonobject.add("entity", this.entity.serializeConditions(conditions));
            return jsonobject;
        }
    }
}
