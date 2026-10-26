package net.fryc.imbleeding.network.payloads;

import net.fryc.imbleeding.network.ModPackets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SynchronizeConfigPayload(boolean combatRollCompat) implements CustomPacketPayload {

    public static final Type<SynchronizeConfigPayload> ID = new Type<>(ModPackets.SYNCHRONIZE_CONFIG);
    public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizeConfigPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SynchronizeConfigPayload::combatRollCompat,
            SynchronizeConfigPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}