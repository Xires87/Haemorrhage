package net.fryc.imbleeding.mixin;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fryc.imbleeding.effects.ModEffects;
import net.fryc.imbleeding.network.payloads.CreateBloodParticlePayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin extends PlayerEntity {

    private Random myRandom = new Random();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void spawnBloodParticles(CallbackInfo info) {
        ServerPlayerEntity dys = ((ServerPlayerEntity)(Object)this);
        if(dys.hasStatusEffect(ModEffects.BLEED_EFFECT)){
            if(this.myRandom.nextInt(100) < 5 + dys.getActiveStatusEffects().get(ModEffects.BLEED_EFFECT).getAmplifier()*5){
                Vec3d vec3d = dys.getVelocity();
                for (ServerPlayerEntity pl : PlayerLookup.tracking(dys.getServerWorld(), dys.getChunkPos())) {
                    ServerPlayNetworking.send(pl, new CreateBloodParticlePayload(
                            dys.getX() + (this.myRandom.nextFloat()/2) - 0.25f,
                            dys.getY() + this.myRandom.nextFloat() + 0.1f,
                            dys.getZ() + (this.myRandom.nextFloat()/2) - 0.25f,
                            vec3d.getY()-0.05
                    ));
                }
            }
        }
    }
}
