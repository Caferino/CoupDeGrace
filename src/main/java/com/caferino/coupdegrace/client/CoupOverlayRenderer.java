package com.caferino.coupdegrace.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import com.caferino.coupdegrace.CoupConfig;
import com.caferino.coupdegrace.CoupDeGrace;

public class CoupOverlayRenderer {
    private static final ResourceLocation VIGNETTE_TEX = ResourceLocation.fromNamespaceAndPath(CoupDeGrace.MOD_ID, "textures/gui/kill_vignette.png");

    private static long fadeOutStartMs = 0L;
    private static long effectEndMs = 0L;

    private static float peakAlpha = 0.2f;
    private static float targetAlpha = 0.0f;
    private static float currentRenderAlpha = 0.0f;

    // Alpha change per millisecond
    private static float fadeInSpeed = 0.0f;
    private static float fadeOutSpeed = 0.0f;

    private static long lastFrameTime = 0L;

    public static void triggerEffect(int durationTicks, float peak) {
        long now = Util.getMillis();
        long durationMs = durationTicks * 50L;

        if (durationMs <= 0) return;

        peakAlpha = peak;
        targetAlpha = peak;

        fadeOutStartMs = now + (durationMs / 2);
        effectEndMs = now + durationMs;

        float fadeInTimeMs = durationMs * 0.35f;
        float fadeOutTimeMs = durationMs * 0.50f;

        fadeInSpeed = peak / fadeInTimeMs;
        fadeOutSpeed = peak / fadeOutTimeMs;
    }

    public static float getFovModifier() {
        if (peakAlpha <= 0.0f || currentRenderAlpha <= 0.0f) return 1.0f;

        // Converts current alpha (e.g. 0.1) into a 0.0 to 1.0 progress factor
        float factor = Math.min(currentRenderAlpha / peakAlpha, 1.0f);
        float targetZoom = CoupConfig.EFFECT_FOV.get().floatValue(); // Default 0.96

        return 1.0f - (factor * (1.0f - targetZoom));
    }

    public static final IGuiOverlay HUD_OVERLAY = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        long now = Util.getMillis();
        if (lastFrameTime == 0L) lastFrameTime = now;
        long deltaMs = now - lastFrameTime;
        lastFrameTime = now;

        // Decide what the alpha should be based on time
        if (now > effectEndMs) {
            targetAlpha = 0.0f;
        } else if (now > fadeOutStartMs) {
            targetAlpha = 0.0f;
        } else {
            targetAlpha = peakAlpha;
        }

        // Smoothly chase the targetAlpha
        if (currentRenderAlpha < targetAlpha) {
            currentRenderAlpha = Math.min(currentRenderAlpha + (fadeInSpeed * deltaMs), targetAlpha);
        } else if (currentRenderAlpha > targetAlpha) {
            currentRenderAlpha = Math.max(currentRenderAlpha - (fadeOutSpeed * deltaMs), targetAlpha);
        }

        // Draw it if visible
        if (currentRenderAlpha > 0.001f) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, currentRenderAlpha);
            guiGraphics.blit(VIGNETTE_TEX, 0, 0, -90, 0.0f, 0.0f, screenWidth, screenHeight, screenWidth, screenHeight);

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }
    };
}