package hamwnyoun.coldgas.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import org.jspecify.annotations.Nullable;
import hamwnyoun.coldgas.worldgen.OpenSimplex;

import static hamwnyoun.coldgas.worldgen.BlockForDensity.AIR;
import static hamwnyoun.coldgas.worldgen.BlockForDensity.SPAWN;

/** A continuous cosmic web built from two crossing Minecraft gradient-noise fields. */
public final class ImprovedCosmicWebChunkGenerator extends ChunkGenerator {
    public static final MapCodec<ImprovedCosmicWebChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource)
            ).apply(builder, ImprovedCosmicWebChunkGenerator::new));

    private static final int GRID_STEP = 8;
    private static final double NOISE_SCALE = 0.015;
    private static final double WIDTH = 0.46;
    private static final int[] GAUSSIAN_WEIGHTS = {1, 2, 1};
    private static final float WEAK_NOISE_STRENGTH = 0.02F;
    private static final int SPAWN_Y = 30;
    private static final int SPAWN_RADIUS = 3;

    private static final OpenSimplex NOISE_A = new OpenSimplex(1023L);
    private static final OpenSimplex NOISE_B = new OpenSimplex(1217L);
    private static final PerlinNoise DETAIL_NOISE = new PerlinNoise(RandomSource.create(44L));

    public ImprovedCosmicWebChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> buildTerrain(
            ChunkAccess chunk,
            Blender blender,
            RandomState randomState,
            StructureManager structureManager,
            BiomeManager biomeManager,
            @Nullable WorldGenRegion carverBiomeRegion,
            Set<Holder<Biome>> possibleBiomes) {
        Heightmap oceanFloor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap worldSurface = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int minY = chunk.getMinY();
        int maxY = chunk.getMaxY();
        int startX = chunk.getPos().getMinBlockX();
        int startZ = chunk.getPos().getMinBlockZ();
        ChunkDensityCache densityCache = new ChunkDensityCache(
                startX, startX + 15, minY, maxY - 1, startZ, startZ + 15);

        for (int localX = 0; localX < 16; localX++) {
            int worldX = startX + localX;
            for (int localZ = 0; localZ < 16; localZ++) {
                int worldZ = startZ + localZ;
                for (int y = minY; y < maxY; y++) {
                    BlockState state = blockAt(worldX, y, worldZ, densityCache);
                    if (state != AIR) {
                        chunk.setBlockState(pos.set(localX, y, localZ), state);
                        oceanFloor.update(localX, y, localZ, state);
                        worldSurface.update(localX, y, localZ, state);
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);
    }

    private static BlockState blockAt(int x, int y, int z, ChunkDensityCache densityCache) {
        int dx = x;
        int dy = y + SPAWN_Y;
        int dz = z;
        if (dx * dx + dy * dy + dz * dz <= SPAWN_RADIUS * SPAWN_RADIUS) {
            return SPAWN;
        }

        float density = densityCache.sampleDensity(x, y, z);
        float detail = densityCache.sampleDetail(x, y, z);
        float modulatedDensity = clamp(density + detail * WEAK_NOISE_STRENGTH, 0.0F, 1.0F);

        return CosmicDensityPalette.blockForDensity(density, x, y, z, detail);
    }

    /** Filters an eight-block lattice with a small separable-equivalent 3D Gaussian kernel. */
    private static final class ChunkDensityCache {
        private final int gridMinX;
        private final int gridMinY;
        private final int gridMinZ;
        private final float[][][] density;
        private final float[][][] detail;

        private ChunkDensityCache(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
            this.gridMinX = Math.floorDiv(minX, GRID_STEP);
            this.gridMinY = Math.floorDiv(minY, GRID_STEP);
            this.gridMinZ = Math.floorDiv(minZ, GRID_STEP);
            int gridMaxX = Math.floorDiv(maxX, GRID_STEP) + 1;
            int gridMaxY = Math.floorDiv(maxY, GRID_STEP) + 1;
            int gridMaxZ = Math.floorDiv(maxZ, GRID_STEP) + 1;

            int rawMinX = gridMinX - 1;
            int rawMinY = gridMinY - 1;
            int rawMinZ = gridMinZ - 1;
            int rawSizeX = gridMaxX - gridMinX + 3;
            int rawSizeY = gridMaxY - gridMinY + 3;
            int rawSizeZ = gridMaxZ - gridMinZ + 3;
            float[][][] rawDensity = new float[rawSizeX][rawSizeY][rawSizeZ];
            float[][][] rawDetail = new float[rawSizeX][rawSizeY][rawSizeZ];

            for (int ix = 0; ix < rawSizeX; ix++) {
                double worldX = (rawMinX + ix) * (double) GRID_STEP;
                for (int iy = 0; iy < rawSizeY; iy++) {
                    double worldY = (rawMinY + iy) * (double) GRID_STEP;
                    for (int iz = 0; iz < rawSizeZ; iz++) {
                        double worldZ = (rawMinZ + iz) * (double) GRID_STEP;
                        double scaledX = worldX * NOISE_SCALE;
                        double scaledY = worldY * NOISE_SCALE;
                        double scaledZ = worldZ * NOISE_SCALE;



                        double a = NOISE_A.eval(scaledX, scaledY, scaledZ);
                        double b = NOISE_B.eval(scaledX, scaledY, scaledZ);
                        double d2 = a * a + b * b;
                        rawDensity[ix][iy][iz] = (float) Math.exp(-d2 / (WIDTH * WIDTH));



                        rawDetail[ix][iy][iz] = DETAIL_NOISE.get(
                                worldX * 0.11, worldY * 0.11, worldZ * 0.11);
                    }
                }
            }
            int sizeX = gridMaxX - gridMinX + 1;
            int sizeY = gridMaxY - gridMinY + 1;
            int sizeZ = gridMaxZ - gridMinZ + 1;
            this.density = new float[sizeX][sizeY][sizeZ];
            this.detail = new float[sizeX][sizeY][sizeZ];
            for (int ix = 0; ix < sizeX; ix++) {
                for (int iy = 0; iy < sizeY; iy++) {
                    for (int iz = 0; iz < sizeZ; iz++) {
                        float weighted = 0.0F;
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                for (int dz = -1; dz <= 1; dz++) {
                                    int weight = GAUSSIAN_WEIGHTS[dx + 1]
                                            * GAUSSIAN_WEIGHTS[dy + 1]
                                            * GAUSSIAN_WEIGHTS[dz + 1];
                                    weighted += rawDensity[ix + dx + 1][iy + dy + 1][iz + dz + 1] * weight;
                                }
                            }
                        }
                        this.density[ix][iy][iz] = weighted / 64.0F;
                        this.detail[ix][iy][iz] = rawDetail[ix + 1][iy + 1][iz + 1];
                    }
                }
            }
        }

        private float sampleDensity(int x, int y, int z) {
            return sampleGrid(this.density, x, y, z);
        }

        private float sampleDetail(int x, int y, int z) {
            return sampleGrid(this.detail, x, y, z);
        }

        private float sampleGrid(float[][][] values, int x, int y, int z) {
            int gridX = Math.floorDiv(x, GRID_STEP) - this.gridMinX;
            int gridY = Math.floorDiv(y, GRID_STEP) - this.gridMinY;
            int gridZ = Math.floorDiv(z, GRID_STEP) - this.gridMinZ;
            float fx = Math.floorMod(x, GRID_STEP) / (float) GRID_STEP;
            float fy = Math.floorMod(y, GRID_STEP) / (float) GRID_STEP;
            float fz = Math.floorMod(z, GRID_STEP) / (float) GRID_STEP;

            float c000 = values[gridX][gridY][gridZ];
            float c100 = values[gridX + 1][gridY][gridZ];
            float c010 = values[gridX][gridY + 1][gridZ];
            float c110 = values[gridX + 1][gridY + 1][gridZ];
            float c001 = values[gridX][gridY][gridZ + 1];
            float c101 = values[gridX + 1][gridY][gridZ + 1];
            float c011 = values[gridX][gridY + 1][gridZ + 1];
            float c111 = values[gridX + 1][gridY + 1][gridZ + 1];
            float low = lerp(lerp(c000, c100, fx), lerp(c010, c110, fx), fy);
            float high = lerp(lerp(c001, c101, fx), lerp(c011, c111, fx), fy);
            return lerp(low, high, fz);
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float lerp(float start, float end, float alpha) {
        return start + alpha * (end - start);
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState) {
        int minY = heightAccessor.getMinY();
        int maxY = heightAccessor.getMaxY();
        ChunkDensityCache cache = new ChunkDensityCache(x, x, minY, maxY - 1, z, z);
        for (int y = maxY - 1; y >= minY; y--) {
            if (blockAt(x, y, z, cache) != AIR) {
                return y + 1;
            }
        }
        return minY;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor heightAccessor, RandomState randomState) {
        int minY = heightAccessor.getMinY();
        ChunkDensityCache cache = new ChunkDensityCache(x, x, minY, heightAccessor.getMaxY() - 1, z, z);
        BlockState[] states = new BlockState[heightAccessor.getHeight()];
        for (int index = 0; index < states.length; index++) {
            states[index] = blockAt(x, minY + index, z, cache);
        }
        return new NoiseColumn(minY, states);
    }

    @Override
    public void addDebugScreenInfo(List<String> result, RandomState randomState, BlockPos feetPos, SamplerContext samplerContext) {
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion worldGenRegion) {
    }

    @Override
    public int getGenDepth() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return -63;
    }

    @Override
    public int getMinY() {
        return -64;
    }
}
