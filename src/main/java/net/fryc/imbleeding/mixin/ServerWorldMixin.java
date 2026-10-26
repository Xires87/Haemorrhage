package net.fryc.imbleeding.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fryc.imbleeding.ImBleeding;
import net.fryc.imbleeding.network.payloads.SynchronizeConfigPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
abstract class ServerWorldMixin {

    @Inject(method = "addNewPlayer(Lnet/minecraft/server/level/ServerPlayer;)V", at = @At("TAIL"))
    private void sendConfigToClient(ServerPlayer player, CallbackInfo info) {
        ServerPlayNetworking.send(player, new SynchronizeConfigPayload(ImBleeding.config.enableCombatRollCompatibility));
    }
}
