package net.fryc.imbleeding;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fryc.imbleeding.effects.particles.BloodParticle;
import net.fryc.imbleeding.effects.particles.BloodParticleLand;
import net.fryc.imbleeding.effects.particles.ModParticles;
import net.fryc.imbleeding.items.ModItems;
import net.fryc.imbleeding.items.custom.BalmItem;
import net.fryc.imbleeding.network.ModPackets;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.PotionContents;

public class ImBleedingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemProperties.registerGeneric(ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, "balm"), (stack, world, entity, seed) -> {
            return entity != null && entity.isUsingItem() && entity.getUseItem().getItem() instanceof BalmItem ? 1.0F : 0.0F;
        });

        ParticleFactoryRegistry.getInstance().register(ModParticles.BLOOD_PARTICLE, BloodParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.BLOOD_PARTICLE_LAND, BloodParticleLand.Factory::new);
        ModPackets.registerS2CPackets();

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 1 ? stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor() : -1, ModItems.SOAKED_BANDAGE);
    }
}
