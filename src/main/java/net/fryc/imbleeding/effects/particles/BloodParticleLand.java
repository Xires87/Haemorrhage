package net.fryc.imbleeding.effects.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class BloodParticleLand extends TextureSheetParticle {

    protected BloodParticleLand(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void tick() {
        if (this.age++ >= this.lifetime || this.level.getBlockState(BlockPos.containing(this.x, this.y-1, this.z)).canBeReplaced()) {
            this.remove();
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            BloodParticleLand bloodParticle = new BloodParticleLand(clientWorld, d, e, f, g, h, i);
            bloodParticle.pickSprite(this.spriteProvider);
            bloodParticle.lifetime = 220;
            bloodParticle.setColor(1.0f, 0.075f, 0.075f);
            return bloodParticle;
        }
    }
}
