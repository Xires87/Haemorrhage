package net.fryc.imbleeding.effects.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class BloodParticleLand extends SpriteBillboardParticle {

    protected BloodParticleLand(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    public void tick() {
        if (this.age++ >= this.maxAge || this.world.getBlockState(BlockPos.ofFloored(this.x, this.y-1, this.z)).isReplaceable()) {
            this.markDead();
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            BloodParticleLand bloodParticle = new BloodParticleLand(clientWorld, d, e, f, g, h, i);
            bloodParticle.setSprite(this.spriteProvider);
            bloodParticle.maxAge = 220;
            bloodParticle.setColor(1.0f, 0.075f, 0.075f);
            return bloodParticle;
        }
    }
}
