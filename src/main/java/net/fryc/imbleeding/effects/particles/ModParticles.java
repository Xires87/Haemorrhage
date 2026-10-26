package net.fryc.imbleeding.effects.particles;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fryc.imbleeding.ImBleeding;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModParticles {

    public static SimpleParticleType BLOOD_PARTICLE;
    public static SimpleParticleType BLOOD_PARTICLE_LAND;
    private static final SimpleParticleType blood_particle = FabricParticleTypes.simple();
    private static final SimpleParticleType blood_particle_land = FabricParticleTypes.simple();


    public static void registerModParticles(){
        if(BLOOD_PARTICLE == null){
            BLOOD_PARTICLE = (SimpleParticleType) registerParticleType(ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, "blood"), blood_particle);
            BLOOD_PARTICLE_LAND = (SimpleParticleType) registerParticleType(ResourceLocation.fromNamespaceAndPath(ImBleeding.MOD_ID, "blood_land"), blood_particle_land);
        }
    }

    private static ParticleType<? extends ParticleOptions> registerParticleType(ResourceLocation identifier, ParticleType<? extends ParticleOptions> particleType){
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, identifier, particleType);
    }
}
