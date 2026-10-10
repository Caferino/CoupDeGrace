package com.caferino.coupdegrace.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.caferino.coupdegrace.CoupConfig;

public class CoupConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Coup de Grâce Settings"));

        ConfigCategory generalCategory = builder.getOrCreateCategory(Component.literal("General"));
        ConfigCategory filteringCategory = builder.getOrCreateCategory(Component.literal("Filtering"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // === GENERAL === //

        // 1. Checkbox for enabling/disabling the effect
        generalCategory.addEntry(entryBuilder.startBooleanToggle(
                        Component.literal("Enable Coup de Grâce"),
                        CoupConfig.ENABLE_EFFECT.get())
                .setDefaultValue(true)
                .setSaveConsumer(CoupConfig.ENABLE_EFFECT::set)
                .build());

        // 2. Slider for duration in ticks
        generalCategory.addEntry(entryBuilder.startIntSlider(
                        Component.literal("Effect Duration (Ticks)"),
                        CoupConfig.EFFECT_DURATION.get(), 5, 100)
                .setDefaultValue(14)
                .setSaveConsumer(CoupConfig.EFFECT_DURATION::set)
                .build());

        // 3. Slider for peak alpha transparency
        generalCategory.addEntry(entryBuilder.startIntSlider(
                        Component.literal("Peak Opacity (%)"),
                        (int) Math.round(CoupConfig.EFFECT_PEAK_ALPHA.get() * 100.0),
                        0,
                        100)
                .setDefaultValue(20)
                .setTextGetter(val -> Component.literal(val + "%"))
                .setSaveConsumer(val -> CoupConfig.EFFECT_PEAK_ALPHA.set(val / 100.0))
                .build());

        // 4. Integer slider displaying zoom percentage (0% to 50% zoom-in)
        // 0% zoom = 1.0 multiplier, 4% zoom = 0.96 multiplier, 50% zoom = 0.50 multiplier
        generalCategory.addEntry(entryBuilder.startIntSlider(
                        Component.literal("Zoom Strength (%)"),
                        (int) Math.round((1.0 - CoupConfig.EFFECT_FOV.get()) * 100.0),
                        0,
                        100)
                .setDefaultValue(4)
                .setTextGetter(val -> Component.literal(val + "%"))
                .setSaveConsumer(val -> CoupConfig.EFFECT_FOV.set(1.0 - (val / 100.0)))
                .build());

        // === FILTERING === //

        // 1. Whitelist
        filteringCategory.addEntry(entryBuilder.startStrList(
                        Component.literal("Living Entity Whitelist"),
                        new java.util.ArrayList<>(CoupConfig.EFFECT_WHITELIST.get()))
                .setDefaultValue(java.util.List.of())
                .setSaveConsumer(list -> CoupConfig.EFFECT_WHITELIST.set(new java.util.ArrayList<>(list)))
                .build());

        // 2. Blacklist
        filteringCategory.addEntry(entryBuilder.startStrList(
                        Component.literal("Living Entity Blacklist"),
                        new java.util.ArrayList<>(CoupConfig.EFFECT_BLACKLIST.get()))
                .setDefaultValue(java.util.List.of())
                .setSaveConsumer(list -> CoupConfig.EFFECT_BLACKLIST.set(new java.util.ArrayList<>(list)))
                .build());

        builder.setSavingRunnable(CoupConfig.COMMON_CONFIG::save);
        return builder.build();
    }
}