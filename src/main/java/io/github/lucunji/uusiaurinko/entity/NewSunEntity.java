package io.github.lucunji.uusiaurinko.entity;

import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.item.ModItems;
import io.github.lucunji.uusiaurinko.item.radiative.ItemRadiative;
import io.github.lucunji.uusiaurinko.network.ModDataSerializers;
import io.github.lucunji.uusiaurinko.util.ModDamageSource;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class NewSunEntity extends Entity {
    /* A scratch for setting sun's entity data:
    /data merge entity @e[type=uusi-aurinko:new_sun, limit=1, sort=nearest] {}
    /data get entity @e[type=uusi-aurinko:new_sun, limit=1, sort=nearest]
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * bits:
     * 0: water stone;
     * 1: fire stone;
     * 2: earth stone;
     * 3: lightning stone;
     * 4: poop stone;
     * 5-6: sun state;
     * 7: rise up to rest position.
     */
    private static final DataParameter<Byte> SYNC_DATA =
            EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BYTE);
    private static final DataParameter<ConsumedMagicStone> LAST_CONSUMED_STONE =
            EntityDataManager.createKey(NewSunEntity.class, ModDataSerializers.CONSUMED_MAGIC_STONE);
    private static final DataParameter<BlockPos> REST_POSITION =
            EntityDataManager.createKey(NewSunEntity.class, DataSerializers.BLOCK_POS);

    private static final int SIZE_INCREMENT_PER_STONE = 1;

    private int killCount = 0;

    public NewSunEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.setRiseUp(ServerConfigs.INSTANCE.NEW_SUN_RISE_UP.get());
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    /**
     * The hitbox of sun entity is actually smaller than its rendering size
     */
    @Override
    public EntitySize getSize(Pose poseIn) {
        float size = this.getBoundingBoxSize() * 0.7F;
        return new EntitySize(size, size, false);
    }

    @Override
    public void recalculateSize() {
        double x = this.getPosX();
        double y = this.getPosY();
        double z = this.getPosZ();

        super.recalculateSize(); // has the side-effect of adding an offset in position when it expands

        this.setPosition(x, y, z);
        AxisAlignedBB box = this.getBoundingBox(this.getPose());
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (SYNC_DATA.equals(key)) {
            this.recalculateSize();
        }
    }

    /* ------------------------------ Major Logic ------------------------------ */

    @Override
    public void tick() {
        super.tick();

        List<Entity> affectedEntities = this.getAffectedEntities();

        doEntityBothSides(affectedEntities);

        // move towards rest position
        if (this.getSunState() != SunState.NEW_BORN && this.getRiseUp()) {
            Vector3d vec = Vector3d.copy(this.getRestPosition()).subtract(this.getPositionCenter());
            if (vec.lengthSquared() > 1) {
                this.move(MoverType.SELF, vec.normalize().scale(0.1));
            }
        }

        if (!world.isRemote) {
            doEntityServerOnly(affectedEntities);

            doBlockServerOnly();

            updateRestPosition(((ServerWorld) this.world));

            SunState newState = this.recalculateState();
            if (getSunState() != newState) {
                this.setSunState(newState);
            }
        }
    }

    private List<Entity> getAffectedEntities() {
        double radius = this.getAffectingEntityRadius();
        return world.getEntitiesInAABBexcluding(this, new AxisAlignedBB(
                        getPosX() + radius,
                        getPosYCenter() + radius,
                        getPosZ() + radius,
                        getPosX() - radius,
                        getPosYCenter() - radius,
                        getPosZ() - radius
                ), entity -> entity.getDistanceSq(getPositionCenter()) <= radius * radius
                        && entity.isAlive()
                        && !entity.isSpectator()
                        && (!(entity instanceof PlayerEntity) || ((PlayerEntity) entity).isCreative())
        );
    }

    /**
     * Entity operations that on both client side and server side. Currently it only
     * attracts entities.
     * <p>
     * The attraction strength does not follow the inverse-square formula,
     * anf it is actually calculated like this:
     * <p>
     * -r^4 * maxForce / affectEntityRadius^4 + maxForce
     */
    private void doEntityBothSides(List<Entity> entities) {
        for (Entity entity : entities) {
            if (ServerConfigs.INSTANCE.NEW_SUN_ATTRACTION_IMMUNE_ENTITY_TYPES.contains(entity)) continue;
            double rangeQd = this.getAffectingEntityRadius();
            rangeQd *= rangeQd;
            rangeQd *= rangeQd;
            double distanceQd = entity.getDistanceSq(this.getPositionCenter());
            distanceQd *= distanceQd;
            if (distanceQd == 0) continue;
            Vector3d toSun = this.getPositionCenter().subtract(entity.getPositionVec()).normalize();
            final double maxForce = this.getSunState() == SunState.FULL_BLACK ? 0.085 : 0.07;
            double scale = -distanceQd * maxForce / rangeQd + maxForce;
            entity.setMotion(entity.getMotion().add(toSun.scale(scale)));
        }
    }

    private void doEntityServerOnly(List<Entity> entities) {
        float blazeAmount = ServerConfigs.INSTANCE.NEW_SUN_BLAZE_DAMAGE_AMOUNT.get().floatValue();
        float fusionAmount = ServerConfigs.INSTANCE.NEW_SUN_FUSION_DAMAGE_AMOUNT.get().floatValue();

        for (Entity entity : entities) {
            if (entity.getDistanceSq(this.getPositionCenter()) < MathHelper.squareFloat(this.getEntityFusionDamageRadius())) {
                if (this.getSunState() == SunState.GROWING
                        && entity instanceof ItemEntity
                        && tryConsumeMagicStoneEntity((ItemEntity) entity)) {
                    entity.remove();
                    continue;
                }

                // remove falling blocks
                if (entity instanceof FallingBlockEntity) {
                    FallingBlockEntity fbe = ((FallingBlockEntity) entity);
                    if (fbe.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                        fbe.entityDropItem(fbe.getBlockState().getBlock());
                    }
                    fbe.remove();
                    continue;
                }

                if (fusionAmount > 0)
                    entity.attackEntityFrom(ModDamageSource.SUN_FUSION, fusionAmount);

            }

            if (blazeAmount > 0) {
                entity.setFire(10);
                if (entity instanceof LivingEntity) entity.attackEntityFrom(ModDamageSource.SUN_BLAZE, blazeAmount);
            }

            if (this.getSunState() == SunState.NEW_BORN
                    && entity instanceof LivingEntity && ((LivingEntity) entity).getShouldBeDead()
                    && killCount < 100) {
                killCount++;
            }
        }
    }

    private boolean tryConsumeMagicStoneEntity(ItemEntity itemEntity) {
        Item item = itemEntity.getItem().getItem();
        ConsumedMagicStone[] values = ConsumedMagicStone.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i] != ConsumedMagicStone.NONE && item == Objects.requireNonNull(values[i].item).get()) {
                this.setSyncDataBit(i, true);
                this.setLastConsumedStone(values[i]);
                return true;
            }
        }
        return false;
    }

    private void doBlockServerOnly() {
        int amount = ServerConfigs.INSTANCE.NEW_SUN_DESTROY_RATE.get();
        if (amount == 0) return;
        float radius = this.getMeltBlockRadius();
        for (BlockPos pos : this.getAffectedBlocks(radius, amount)) {
            if (pos.distanceSq(this.getPositionCenter(), true) > this.getVaporizeBlockRadius()
                    && Math.random() < 0.3) {
                world.setBlockState(pos, Blocks.FIRE.getDefaultState());
            } else {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3, 512);
            }
        }
    }

    /**
     * It randomly picks {@link ServerConfigs#NEW_SUN_DESTROY_RATE} blocks in range.
     * It avoids picking air blocks by filtering out empty 16x16x16 sections before picking,
     * thus achieves better performance and large block amount at the same time.
     */
    private List<BlockPos> getAffectedBlocks(float radius, int amount) {
        // filter out empty sections
        Vector3d positionCenter = this.getPositionCenter();
        int xMin = (int) (positionCenter.getX() - radius) >> 4;
        int yMin = (int) (positionCenter.getY() - radius) >> 4;
        int zMin = (int) (positionCenter.getZ() - radius) >> 4;
        int xMax = (int) (positionCenter.getX() + radius) >> 4;
        int yMax = (int) (positionCenter.getY() + radius) >> 4;
        int zMax = (int) (positionCenter.getZ() + radius) >> 4;
        LinkedList<SectionPos> sectionPosList = new LinkedList<>();
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    if (this.world.getChunk(x, z).isEmptyBetween(y << 4, (y << 4) | 0b1111)) continue;
                    sectionPosList.add(SectionPos.of(x, y, z));
                }
            }
        }

        List<BlockPos> results = new ArrayList<>();
        if (sectionPosList.isEmpty()) return results;

        // randomly pick blocks in non-empty sections
        float radiusSq = radius * radius;
        Random random = new Random();
        int trial;
        for (trial = 0; trial < 1024 && results.size() < amount; trial++) {
            SectionPos sectionPos = sectionPosList.get(random.nextInt(sectionPosList.size()));
            int x = sectionPos.getWorldStartX() | random.nextInt(16);
            int y = sectionPos.getWorldStartY() | random.nextInt(16);
            int z = sectionPos.getWorldStartZ() | random.nextInt(16);
            BlockPos p = new BlockPos(x, y, z);

            if (p.distanceSq(getPositionCenter(), true) > radiusSq
                    || this.world.isAirBlock(p)) continue;
            BlockState blockState = world.getBlockState(p);
            if (blockState.matchesBlock(Blocks.FIRE)
                    || ServerConfigs.INSTANCE.NEW_SUN_DESTROY_BLACKLIST.contains(world.getBlockState(p))) continue;

            p = this.world.rayTraceBlocks(new RayTraceContext(
                    this.getPositionCenter(),
                    Vector3d.copy(p).add(0.5, 0.5, 0.5),
                    RayTraceContext.BlockMode.COLLIDER,
                    RayTraceContext.FluidMode.SOURCE_ONLY, // ignore non-source fluid blocks
                    null)
            ).getPos();
            results.add(p);
        }
        return results;
    }

    private void updateRestPosition(ServerWorld world) {
        RegistryKey<World> dimensionRegistryKey = world.getDimensionKey();
        if (dimensionRegistryKey == World.OVERWORLD) {
            BlockPos spawnPoint = world.getSpawnPoint();
            this.setRestPosition(new BlockPos(spawnPoint.getX(), 200, spawnPoint.getZ()));
        } else if (dimensionRegistryKey == World.THE_NETHER) {
            BlockPos spawnPoint = world.getSpawnPoint();
            this.setRestPosition(new BlockPos(spawnPoint.getX() / 8, 100, spawnPoint.getZ() / 8));
        } else { // for end and other dimensions
            this.setRestPosition(new BlockPos(0, 200, 0));
        }
    }

    private SunState recalculateState() {
        if (killCount >= 100) {
            if (this.getHas4Stones()) {
                if (this.getHasPoopStone()) {
                    return SunState.FULL_BLACK;
                } else {
                    return SunState.FULL_YELLOW;
                }
            } else {
                return SunState.GROWING;
            }
        } else {
            return SunState.NEW_BORN;
        }
    }

    /* ------------------------------ Data Sync & Storage ------------------------------ */

    /**
     * Register static instances of {@link net.minecraft.network.datasync.DataParameter} here.
     */
    @Override
    protected void registerData() {
        this.dataManager.register(SYNC_DATA, (byte) 0);
        this.dataManager.register(REST_POSITION, new BlockPos(0, 0, 0));
        this.dataManager.register(LAST_CONSUMED_STONE, ConsumedMagicStone.NONE);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        try {
            this.setLastConsumedStone(ConsumedMagicStone.valueOf(compound.getString("LastStoneConsumed")));
        } catch (IllegalArgumentException ignored) {
        }

        CompoundNBT stoneConsumed = compound.getCompound("StoneConsumed");
        this.setHasWaterStone(stoneConsumed.getBoolean("Water"));
        this.setHasFireStone(stoneConsumed.getBoolean("Fire"));
        this.setHasEarthStone(stoneConsumed.getBoolean("Earth"));
        this.setHasLightningStone(stoneConsumed.getBoolean("Lightning"));
        this.setHasPoopStone(stoneConsumed.getBoolean("Poop"));

        this.killCount = compound.getInt("KillCount");
        if (compound.contains("RiseUp")) {
            this.setRiseUp(compound.getBoolean("RiseUp"));
        } else {
            this.setRiseUp(ServerConfigs.INSTANCE.NEW_SUN_RISE_UP.get());
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putString("LastStoneConsumed", this.getLastConsumedStone().name());

        CompoundNBT stoneConsumed = new CompoundNBT();
        stoneConsumed.putBoolean("Water", this.getHasWaterStone());
        stoneConsumed.putBoolean("Fire", this.getHasFireStone());
        stoneConsumed.putBoolean("Earth", this.getHasEarthStone());
        stoneConsumed.putBoolean("Lightning", this.getHasLightningStone());
        stoneConsumed.putBoolean("Poop", this.getHasPoopStone());
        compound.put("StoneConsumed", stoneConsumed);

        compound.putInt("KillCount", this.killCount);
        compound.putBoolean("RiseUp", this.getRiseUp());
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    /* ------------------------------ Getters & Setters ------------------------------ */

    public float getRenderingSize() {
        SunState sunState = this.getSunState();
        if (sunState == SunState.GROWING) {
            float baseSize = sunState.size;
            if (this.getHasWaterStone()) baseSize += SIZE_INCREMENT_PER_STONE;
            if (this.getHasFireStone()) baseSize += SIZE_INCREMENT_PER_STONE;
            if (this.getHasEarthStone()) baseSize += SIZE_INCREMENT_PER_STONE;
            if (this.getHasLightningStone()) baseSize += SIZE_INCREMENT_PER_STONE;
            if (this.getHasPoopStone()) baseSize += SIZE_INCREMENT_PER_STONE;
            return baseSize;
        } else {
            return sunState.size;
        }
    }

    public float getBoundingBoxSize() {
        return this.getRenderingSize() * 0.7F;
    }

    private Vector3d getPositionCenter() {
        return this.getPositionVec().add(0, this.getBoundingBoxSize() / 2D, 0);
    }

    private BlockPos getBlockPosCenter() {
        return new BlockPos(this.getPositionVec().add(0, this.getBoundingBoxSize() / 2D, 0));
    }

    private double getPosYCenter() {
        return this.getPosY() + this.getBoundingBoxSize() / 2D;
    }

    /**
     * Should be larger than {@link NewSunEntity#getMeltBlockRadius}
     */
    private float getAffectingEntityRadius() {
        return this.getRenderingSize() * 1.7F;
    }

    private float getEntityFireDamageRadius() {
        return this.getAffectingEntityRadius();
    }

    private float getEntityFusionDamageRadius() {
        return this.getRenderingSize() * 0.6F;
    }

    /**
     * Should be smaller than {@link NewSunEntity#getAffectingEntityRadius()}
     */
    private float getMeltBlockRadius() {
        return this.getRenderingSize() * 1.3F;
    }

    private float getVaporizeBlockRadius() {
        return this.getEntityFusionDamageRadius();
    }

    public boolean getHasWaterStone() {
        return this.getSyncDataBit(0);
    }

    public void setHasWaterStone(boolean newVal) {
        this.setSyncDataBit(0, newVal);
    }

    public boolean getHasFireStone() {
        return this.getSyncDataBit(1);
    }

    public void setHasFireStone(boolean newVal) {
        this.setSyncDataBit(1, newVal);
    }

    public boolean getHasEarthStone() {
        return this.getSyncDataBit(2);
    }

    public void setHasEarthStone(boolean newVal) {
        this.setSyncDataBit(2, newVal);
    }

    public boolean getHasLightningStone() {
        return this.getSyncDataBit(3);
    }

    public void setHasLightningStone(boolean newVal) {
        this.setSyncDataBit(3, newVal);
    }

    private boolean getHas4Stones() {
        return (this.dataManager.get(SYNC_DATA) & 0b1111 ^ 0b1111) == 0;
    }

    public boolean getHasPoopStone() {
        return this.getSyncDataBit(4);
    }

    public void setHasPoopStone(boolean newVal) {
        this.setSyncDataBit(4, newVal);
    }

    public SunState getSunState() {
        return SunState.values()[this.dataManager.get(SYNC_DATA) >> 5 & 0b11];
    }

    public void setSunState(SunState sunState) {
        byte data = this.dataManager.get(SYNC_DATA);
        this.dataManager.set(SYNC_DATA, (byte) (data & 0b1001_1111 | sunState.ordinal() << 5));
    }

    private boolean getRiseUp() {
        return this.getSyncDataBit(7);
    }

    private void setRiseUp(boolean val) {
        this.setSyncDataBit(7, val);
    }

    private boolean getSyncDataBit(int offset) {
        return (this.dataManager.get(SYNC_DATA) & 1 << offset) != 0;
    }

    private void setSyncDataBit(int offset, boolean set) {
        byte data = this.dataManager.get(SYNC_DATA);
        this.dataManager.set(SYNC_DATA, (byte) (set ? data | 1 << offset : data & ~(1 << offset)));
    }

    public ConsumedMagicStone getLastConsumedStone() {
        return this.dataManager.get(LAST_CONSUMED_STONE);
    }

    public void setLastConsumedStone(ConsumedMagicStone stone) {
        this.dataManager.set(LAST_CONSUMED_STONE, stone);
    }

    private void setRestPosition(BlockPos newVal) {
        this.dataManager.set(REST_POSITION, newVal);
    }

    private BlockPos getRestPosition() {
        return this.dataManager.get(REST_POSITION);
    }

    public enum SunState {
        NEW_BORN(8F, new ResourceLocation(MODID, "textures/entity/sun_yellow.png"), 20),
        GROWING(16F, new ResourceLocation(MODID, "textures/entity/sun_white.png"), 30),
        FULL_YELLOW(24F, new ResourceLocation(MODID, "textures/entity/sun_white.png"), 30),
        FULL_BLACK(48F, new ResourceLocation(MODID, "textures/entity/sun_black.png"), 40);

        public final float size;
        public final ResourceLocation texture;
        public final int haloIters;

        SunState(float size, ResourceLocation texture, int haloIters) {
            this.size = size;
            this.texture = texture;
            this.haloIters = haloIters;
        }
    }

    public enum ConsumedMagicStone {
        WATER(ModItems.WATER_STONE, new ResourceLocation(MODID, "textures/entity/sun_purple.png")),
        FIRE(ModItems.FIRE_STONE, new ResourceLocation(MODID, "textures/entity/sun_red.png")),
        EARTH(ModItems.EARTH_STONE, new ResourceLocation(MODID, "textures/entity/sun_green.png")),
        LIGHTNING(ModItems.LIGHTNING_STONE, new ResourceLocation(MODID, "textures/entity/sun_blue.png")),
        POOP(ModItems.POOP_STONE, new ResourceLocation(MODID, "textures/entity/sun_white.png")),
        NONE(null, new ResourceLocation(MODID, "textures/entity/sun_white.png"));

        @Nullable
        public final RegistryObject<? extends ItemRadiative> item;
        public final ResourceLocation texture;

        ConsumedMagicStone(RegistryObject<? extends ItemRadiative> item, ResourceLocation texture) {
            this.item = item;
            this.texture = texture;
        }
    }
}
