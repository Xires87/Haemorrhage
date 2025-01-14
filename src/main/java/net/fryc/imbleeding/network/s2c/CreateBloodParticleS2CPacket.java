package net.fryc.imbleeding.network.s2c;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.effects.particles.ModParticles;
import net.fryc.imbleeding.network.payloads.CreateBloodParticlePayload;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

import java.util.concurrent.ThreadLocalRandom;

public class CreateBloodParticleS2CPacket {

    public static void receive(CreateBloodParticlePayload payload, ClientPlayNetworking.Context context){
        ClientPlayerEntity player = context.player();
        if(player != null){
            if(player.getWorld().isClient()){
                if(ImBleeding.config.enableBloodParticlesClientSided){
                    ClientWorld world = ((ClientWorld) player.getWorld());
                    double x = payload.x() + (ThreadLocalRandom.current().nextFloat()/2) - 0.25f;
                    double y = payload.y() + ThreadLocalRandom.current().nextFloat() + 0.1f;
                    double z = payload.z() + (ThreadLocalRandom.current().nextFloat()/2) - 0.25f;
                    double vec3d = payload.vec3d();
                    world.addParticle(ModParticles.BLOOD_PARTICLE, x, y, z, 0, vec3d, 0);
                }
            }

        }
    }
}
