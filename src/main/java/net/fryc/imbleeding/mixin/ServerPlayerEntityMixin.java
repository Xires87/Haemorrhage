package net.fryc.imbleeding.mixin;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fryc.imbleeding.effects.ModEffects;
import net.fryc.imbleeding.network.ModPackets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Shadow
    public ServerWorld getServerWorld() {
        return (ServerWorld)this.getWorld();
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void spawnBloodParticles(CallbackInfo info) {
        if(!this.getWorld().isClient()){
            if(this.hasStatusEffect(ModEffects.BLEED_EFFECT)){
                if(ThreadLocalRandom.current().nextInt(100) < 5 + this.getActiveStatusEffects().get(ModEffects.BLEED_EFFECT).getAmplifier()*5){
                    Vec3d vec3d = this.getVelocity();
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeDouble(this.getX());
                    buf.writeDouble(this.getY());
                    buf.writeDouble(this.getZ());
                    buf.writeDouble(vec3d.getY()-0.05);
                    for (ServerPlayerEntity pl : PlayerLookup.tracking(this.getServerWorld(), this.getChunkPos())) {
                        ServerPlayNetworking.send(pl, ModPackets.CREATE_BLOOD_PARTICLE, buf);
                    }
                }
            }
        }
    }
}
