package io.github.lucunji.uusiaurinko.datagen.client;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public enum HardcodedLanguageEntries {
    ZH_CN("zh_cn",
            Pair.of("itemGroup.uusi-aurinko.defaults", "Uusi Aurinko"),
            Pair.of("tooltip.uusi-aurinko.shift_more", "§o更多信息：按下 <Shift>§r"),
            Pair.of("tooltip.uusi-aurinko.shift_less", "\n§7§o更少信息：松开 <Shift>§r"),

            Pair.of("item.uusi-aurinko.fire_stone.tooltip", "这块石头看似炽热，摸着只是暖意融融。\n似乎被握在手中时能提供火焰防护，但是留心你的易燃家具！"),
            Pair.of("item.uusi-aurinko.water_stone.tooltip", "这块石头坚硬却又水流汩汩。\n它散发的潮气能对怕火的生物造成伤害，熄灭身上的火焰并临时凝固岩浆。\n它甚至能让你在水下呼吸！"),
            Pair.of("item.uusi-aurinko.lightning_stone.tooltip", "这块石头让你感到自己充满了电力。\n握着它能让你免受电击。请不要在导体附近随意丢弃。"),
            Pair.of("item.uusi-aurinko.earth_stone.tooltip", "这块石头看起来亘古不衰。\n它周围的一切都快速化为泥土；稍稍挥动它大地便会崩裂。"),
            Pair.of("item.uusi-aurinko.poop_stone.tooltip", "这块神秘的螺旋形物体闻着恶臭，摸着温热。\n呕……"),
            Pair.of("item.uusi-aurinko.sun_seed.tooltip", "它发着光，充满希望。\n周围的尘土都因它的热量迅速崩裂。"),
            Pair.of("item.uusi-aurinko.sun_stone.tooltip", "太阳精华之结晶。\n周围的尘土在顷刻间被焚烧殆尽。"),
            Pair.of("item.uusi-aurinko.evil_eye.tooltip", "这个形似眼球的人工物恶毒地瞪着。\n放在额头上，它将助你看清一切。\n强烈的凝视也使你感到紧张疲惫……"),
            Pair.of("item.uusi-aurinko.moon.tooltip", "这个小巧的天体有着强大的重力。\n万物都被它吸引，尤其是其它天体。"),

            Pair.of("death.attack.uusi-aurinko.electricity", "%1$s感受到了贯穿身体的高压电流"),
            Pair.of("death.attack.uusi-aurinko.electricity.player", "%1$s在与%2$s的战斗中感受到了贯穿身体的高压电流"),

            Pair.of("config.uusi-aurinko.change_fluid_rendering", "流体方块的渲染效果改变了。按下 F3+T 使其生效。"),

            Pair.of("container.uusi-aurinko.item_pedestal", "物品底座")
    ),

    EN_US("en_us",
            Pair.of("itemGroup.uusi-aurinko.defaults", "Uusi Aurinko"),
            Pair.of("tooltip.uusi-aurinko.shift_more", "§oMore Info: Press <Shift>§r"),
            Pair.of("tooltip.uusi-aurinko.shift_less", "\n§7§oLess Info: Release <Shift>§r"),

            Pair.of("item.uusi-aurinko.fire_stone.tooltip", "This tiny rock looks most fiery, but when touched only a pleasant warmth can be felt.\nSeem to provide fire protection when held in hand, but be careful with your flammable furniture!"),
            Pair.of("item.uusi-aurinko.water_stone.tooltip", "This small rock is hard and solid, yet seems to be gushing with water.\nIt is wet enough to damage fire-sensitive creatures, extinguish fire on body and solidify lava temporary.\nIt even makes you breath under water!"),
            Pair.of("item.uusi-aurinko.lightning_stone.tooltip", "This small rock makes you feel very charged.\nHolding it can makes you survive from electric shock. Do not throw it away near conductors."),
            Pair.of("item.uusi-aurinko.earth_stone.tooltip", "It looks like it could stand the test of aeons.\nIt quickly turns everything around into soil; with only a slight swing it can make earth collapse."),
            Pair.of("item.uusi-aurinko.poop_stone.tooltip", "This mysterious spiral-shaped artefact smells horrible and feels warm to the touch.\nDisgusting..."),
            Pair.of("item.uusi-aurinko.sun_seed.tooltip", "It glows, full of promise.\nDust and dirt around burst into pieces quickly under its tremendous heat."),
            Pair.of("item.uusi-aurinko.sun_stone.tooltip", "The essence of the Sun, crystallized.\nDust and dirt around are burnt down within seconds."),
            Pair.of("item.uusi-aurinko.evil_eye.tooltip", "This eye-shaped artefact projects a malevolent glare.\nPut it on your forehead, it then reveals everything to you.\nExhausting to you is its intense gaze."),
            Pair.of("item.uusi-aurinko.moon.tooltip", "This tiny celestial object has a strong gravitational pull.\nIt attracts everything, especially other stars."),

            Pair.of("death.attack.uusi-aurinko.electricity", "%1$s experienced high voltage pass through the body"),
            Pair.of("death.attack.uusi-aurinko.electricity.player", "%1$s experienced high voltage pass through the body whilst fighting %2$s"),

            Pair.of("config.uusi-aurinko.change_fluid_rendering", "The rendering effect of fluid blocks is changed. Press F3+T to take effect."),

            Pair.of("container.uusi-aurinko.item_pedestal", "Item Pedestal")
    );

    final String locale;
    final Pair<String, String>[] entries;

    @SafeVarargs
    HardcodedLanguageEntries(String locale, Pair<String, String>... entries) {
        this.locale = locale;
        this.entries = entries;
    }
}
