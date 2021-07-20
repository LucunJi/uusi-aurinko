package io.github.lucunji.uusiaurinko.events;

import io.github.lucunji.uusiaurinko.item.ModItems;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class VillagerTradesEventListener {
    /* Scratch for summoning a new villager
    /summon villager ~ ~ ~ {VillagerData: {level: 5, profession: "minecraft:cleric", type: "minecraft:desert"}}
     */
    @SubscribeEvent
    public static void onCollectingVillagerTrades(VillagerTradesEvent villagerTradesEvent) {
        if (villagerTradesEvent.getType() == VillagerProfession.CLERIC) {
            Int2ObjectMap<List<VillagerTrades.ITrade>> trades = villagerTradesEvent.getTrades();
            trades.get(5).add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 32),
                    new ItemStack(Items.EMERALD_BLOCK, 6),
                    new ItemStack(ModItems.EMERALD_TABLET.get(), 1),
                    3,
                    50,
                    0.02F
            ));
        }
    }
}
