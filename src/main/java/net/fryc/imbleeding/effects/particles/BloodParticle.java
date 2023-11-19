package net.fryc.imbleeding.effects.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class BloodParticle extends SpriteBillboardParticle {


    protected BloodParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else if(this.world.getBlockState(BlockPos.ofFloored(this.x, this.y, this.z)).isReplaceable()){
            this.velocityY -= 0.04 * (double)this.gravityStrength;
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            if (this.ascending && this.y == this.prevPosY) {
                this.velocityX *= 1.1;
                this.velocityZ *= 1.1;
            }

            this.velocityX *= (double)this.velocityMultiplier;
            this.velocityY *= (double)this.velocityMultiplier;
            this.velocityZ *= (double)this.velocityMultiplier;
            if (this.onGround) {
                this.velocityX *= 0.699999988079071;
                this.velocityZ *= 0.699999988079071;
            }

        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType>{
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            BloodParticle bloodParticle = new BloodParticle(clientWorld, d, e, f, g, h, i);
            bloodParticle.setSprite(this.spriteProvider);
            bloodParticle.velocityX = 0;
            bloodParticle.velocityY = 0;
            bloodParticle.velocityZ = 0;
            bloodParticle.gravityStrength = 1.5F;
            bloodParticle.maxAge = 300;
            bloodParticle.setColor(1.0f, 0.1f, 0.1f);
            return bloodParticle;
        }
    }



}
