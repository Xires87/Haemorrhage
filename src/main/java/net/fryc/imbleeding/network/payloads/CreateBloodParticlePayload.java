package net.fryc.imbleeding.network.payloads;

import net.fryc.imbleeding.network.ModPackets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CreateBloodParticlePayload(double x, double y, double z, double vec3d) implements CustomPacketPayload {

    public static final Type<CreateBloodParticlePayload> ID = new Type<>(ModPackets.CREATE_BLOOD_PARTICLE);
    public static final StreamCodec<RegistryFriendlyByteBuf, CreateBloodParticlePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, CreateBloodParticlePayload::x,
            ByteBufCodecs.DOUBLE, CreateBloodParticlePayload::y,
            ByteBufCodecs.DOUBLE, CreateBloodParticlePayload::z,
            ByteBufCodecs.DOUBLE, CreateBloodParticlePayload::vec3d,
            CreateBloodParticlePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}