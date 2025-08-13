package net.fryc.imbleeding.network.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.effects.particles.ModParticles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;

public class CreateBloodParticleS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        ClientPlayerEntity player = client.player;
        if(player != null){
            if(player.getWorld().isClient()){
                if(ImBleeding.config.enableBloodParticlesClientSided){
                    ClientWorld world = (ClientWorld) player.getWorld();
                    double x = buf.readDouble();
                    double y = buf.readDouble();
                    double z = buf.readDouble();
                    double vec3d = buf.readDouble();
                    world.addParticle(ModParticles.BLOOD_PARTICLE, x, y, z, 0, vec3d, 0);
                }
            }
        }
    }
}
