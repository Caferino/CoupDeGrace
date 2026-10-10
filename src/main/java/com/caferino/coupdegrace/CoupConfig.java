package com.caferino.coupdegrace;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

public class CoupConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_EFFECT;
    public static final ForgeConfigSpec.ConfigValue<Integer> EFFECT_DURATION;
    public static final ForgeConfigSpec.DoubleValue EFFECT_PEAK_ALPHA;
    public static final ForgeConfigSpec.DoubleValue EFFECT_FOV;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EFFECT_WHITELIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EFFECT_BLACKLIST;

    static {
        BUILDER.push("Coup de Grâce Settings");
            BUILDER.push("General");
                ENABLE_EFFECT = BUILDER.comment(" Enable the kill effect", " Default: true").define("enableEffect", true);
                EFFECT_DURATION = BUILDER.comment(" Effect's duration in ticks (20 ticks = 1 second)", " Default: 14").define("effectDuration", 14);
                EFFECT_PEAK_ALPHA = BUILDER.comment(" Effect's peak opacity", " Default: 0.2").defineInRange("peakAlpha", 0.2, 0.0, 1.0);
                EFFECT_FOV = BUILDER.comment(" Camera zoom intensity multiplier",
                        " • 1.0 = No zoom (disabled)",
                        " • 0.96 = Subtle zoom (4% zoom-in)",
                        " • 0.50 = Heavy zoom (50% zoom-in)",
                        " Default: 0.96").defineInRange("effectFov", 0.96, 0.0, 1.0);
            BUILDER.pop();

            BUILDER.push("Filtering");
                EFFECT_WHITELIST = BUILDER.comment(
                        " Living entities that trigger the effect",
                        " • If empty, all living entities (vanilla & modded) will trigger the effect",
                        " • If populated, ONLY listed entities will trigger it [ID: namespace:name]",
                        " Default: empty []"
                ).defineList("whitelist", Lists.newArrayList(), (obj) -> obj instanceof String);

                EFFECT_BLACKLIST = BUILDER.comment(
                        " Living entities that bypass the adrenaline effect [ID: namespace:name]", " Default: empty []"
                ).defineList("blacklist", Lists.newArrayList(), (obj) -> obj instanceof String);
            BUILDER.pop();
        BUILDER.pop();

        COMMON_CONFIG = BUILDER.build();
    }
}
