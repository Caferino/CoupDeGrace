package com.caferino.coupdegrace.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.caferino.coupdegrace.CoupConfig;
import com.caferino.coupdegrace.CoupDeGrace;
import com.caferino.coupdegrace.network.CoupEffectPacket;
import com.caferino.coupdegrace.network.CoupNetworkManager;
import java.util.List;

@Mod.EventBusSubscriber(modid = CoupDeGrace.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CoupEventSubscriber {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        // Master toggle check
        if (!CoupConfig.ENABLE_EFFECT.get()) return;

        LivingEntity victim = event.getEntity();
        DamageSource damageSource = event.getSource();

        // Verify that a killer exists and that it is a player
        if (damageSource != null && damageSource.getEntity() instanceof ServerPlayer serverPlayer) {

            // --- WHITELIST / BLACKLIST FILTERING ---
            ResourceLocation entityId = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(victim.getType());
            String entityKey = entityId != null ? entityId.toString() : "";

            List<? extends String> whitelist = CoupConfig.EFFECT_WHITELIST.get();
            List<? extends String> blacklist = CoupConfig.EFFECT_BLACKLIST.get();

            // If the whitelist is populated, the living entity MUST be in it to trigger the effect
            if (!whitelist.isEmpty() && !whitelist.contains(entityKey)) {
                return;
            }

            // If the living entity is in the blacklist, skip the effect entirely
            if (blacklist.contains(entityKey)) {
                return;
            }
            // ----------------------------------------

            // Trigger the visual effect packet to the player
            CoupNetworkManager.sendToPlayer(new CoupEffectPacket(
                            CoupConfig.EFFECT_DURATION.get(),
                            CoupConfig.EFFECT_PEAK_ALPHA.get().floatValue()),
                    serverPlayer);
        }
    }
}