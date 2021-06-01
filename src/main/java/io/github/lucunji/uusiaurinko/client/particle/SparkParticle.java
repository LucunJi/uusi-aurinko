package io.github.lucunji.uusiaurinko.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class SparkParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite sprite;

    protected SparkParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite sprite) {
        super(world, x, y, z, 0, 0, 0);
        this.sprite = sprite;
        this.motionX *= (double)0.3F;
        this.motionY = Math.random() * (double)0.2F + (double)0.1F;
        this.motionZ *= (double)0.3F;
        this.setSize(0.01F, 0.01F);
        this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
        this.selectSpriteWithAge(sprite);
        this.particleGravity = 0.0F;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.setColor(194, 225, 247);
        this.setAlphaF(0.6F);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        int i = 60 - this.maxAge;
        if (this.maxAge-- <= 0) {
            this.setExpired();
        } else {
            this.motionY -= this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.98F;
            this.motionY *= 0.98F;
            this.motionZ *= 0.98F;
            float f = (float)i * 0.001F;
            this.setSize(f, f);
            this.setSprite(this.sprite.get(i % 4, 4));
        }
    }

    /**
     * Integer version of {@code setColor()}, accept three 0-255 integers.
     */
    public void setColor(int particleRedIn, int particleGreenIn, int particleBlueIn) {
        super.setColor(particleRedIn/255F, particleGreenIn/255F, particleBlueIn/255F);
    }

    @Override
    protected void setAlphaF(float alpha) {
        super.setAlphaF(alpha);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public Factory(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        @Nullable
        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SparkParticle particle = new SparkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
            return particle;
        }
    }
}
