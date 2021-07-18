package io.github.lucunji.uusiaurinko.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

import javax.annotation.Nullable;

public class SparkParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite sprite;

    /**
     * Code is borrowed from {@link WaterWakeParticle}.
     */
    protected SparkParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite sprite) {
        super(world, x, y, z, 0, 0, 0);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;

        this.particleGravity = 0;
        this.setSize(0.01F, 0.01F);
        this.maxAge = (int)(Math.random() * 5 + 1);

        this.sprite = sprite;
        this.selectSpriteWithAge(sprite);
        this.setColor(126 - 5 + (int)(Math.random() * 10), 249 - 5 + (int)(Math.random() * 10), 255 - (int)(Math.random() * 5));
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    protected int getBrightnessForRender(float partialTick) {
        return 0xF000F0;
    }

    /**
     * Rewrite the logic in the super class because this class is using {@link IAnimatedSprite},
     * rather than {@link TextureAtlasSprite}.
     * <p>
     * Code is borrowed from {@link WaterWakeParticle}.
     */
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
            float f = (float) i * 0.001F;
            this.setSize(f, f);
            this.setSprite(this.sprite.get(i & 3, 4)); // i & 3 == i % 4
        }
    }

    /**
     * Integer version of {@link Particle#setColor}, accept three 0-255 integers.
     */
    public void setColor(int particleRedIn, int particleGreenIn, int particleBlueIn) {
        super.setColor(particleRedIn / 255F, particleGreenIn / 255F, particleBlueIn / 255F);
    }

    /**
     * Override to make the method available in the current package.
     *
     * @param alpha a 0-1 decimal
     */
    @Override
    protected void setAlphaF(float alpha) {
        super.setAlphaF(alpha);
    }

    /**
     * Factory class for client to make new particles on receiving packets from server.
     */
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public Factory(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        @Nullable
        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SparkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
        }
    }
}
