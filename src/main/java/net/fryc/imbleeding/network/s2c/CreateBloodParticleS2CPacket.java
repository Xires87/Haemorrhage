package net.fryc.imbleeding.network.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.effects.particles.ModParticles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

public class CreateBloodParticleS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        ClientPlayerEntity player = client.player;
        if(player != null){
            World world = player.getWorld();
            if(world.isClient()){
                if(ImBleeding.config.enableBloodParticlesClientSided){
                    double x = buf.readDouble() + (ThreadLocalRandom.current().nextFloat()/2) - 0.25f;
                    double y = buf.readDouble() + ThreadLocalRandom.current().nextFloat() + 0.1f;
                    double z = buf.readDouble() + (ThreadLocalRandom.current().nextFloat()/2) - 0.25f;
                    double vec3d = buf.readDouble();
                    world.addParticle(ModParticles.BLOOD_PARTICLE, x, y, z, 0, vec3d, 0);
                }
            }
        }
    }
}
