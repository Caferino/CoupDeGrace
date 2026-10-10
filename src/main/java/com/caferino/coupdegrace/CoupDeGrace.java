package com.caferino.coupdegrace;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import com.caferino.coupdegrace.client.CoupConfigScreen;
import com.caferino.coupdegrace.client.CoupOverlayRenderer;
import com.caferino.coupdegrace.network.CoupNetworkManager;

@Mod(CoupDeGrace.MOD_ID)
public class CoupDeGrace {
    public static final String MOD_ID = "coupdegrace";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CoupDeGrace() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CoupConfig.COMMON_CONFIG);

        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parentScreen) -> CoupConfigScreen.create(parentScreen)
                )
        );

        CoupNetworkManager.register();
        LOGGER.info("Coup de Grâce initialized successfully.");
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                // Preload the vignette texture so it doesn't stutter on the first kill
                Minecraft.getInstance().getTextureManager().bindForSetup(
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/kill_vignette.png")
                );
            });
        }

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("kill_vignette", CoupOverlayRenderer.HUD_OVERLAY);
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onComputeFov(ViewportEvent.ComputeFov event) {
            float modifier = CoupOverlayRenderer.getFovModifier();
            if (modifier < 1.0f) {
                event.setFOV(event.getFOV() * modifier);
            }
        }
    }
}