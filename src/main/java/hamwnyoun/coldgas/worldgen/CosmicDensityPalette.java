package hamwnyoun.coldgas.worldgen;

import net.minecraft.world.level.block.state.BlockState;

import static hamwnyoun.coldgas.worldgen.BlockForDensity.*;

/** Maps the generated cosmic-web density field to its temporary block palette. */
public final class CosmicDensityPalette {
    // These cutoffs are calibrated against the smoothed ImprovedNoise field on an 8-block lattice.
    public static final float LOW_DENSITY_THRESHOLD = 0.62F;
    public static final float FILAMENT_THRESHOLD = 0.72F;
    public static final float DENSE_FILAMENT_THRESHOLD = 0.78F;
    public static final float NODE_THRESHOLD = 0.86F;
    public static final float DENSE_NODE_THRESHOLD = 0.90F;

    private static final float VOID_DENSITY_LIMIT = 0.43F;
    private static final int VOID_CELL_SIZE = 8;

    private CosmicDensityPalette() {
    }

    /** Selects a block at a position from its final, modulated density. */
    public static BlockState blockForDensity(float density, int x, int y, int z, float detail) {
        if (density >= DENSE_NODE_THRESHOLD) {
            return DENSE_NODE;
        }
        if (density >= NODE_THRESHOLD) {
            return NODE;
        }
        if (density >= DENSE_FILAMENT_THRESHOLD) {
            return DENSE_FILAMENT;
        }
        if (density >= FILAMENT_THRESHOLD) {
            return FILAMENT;
        }
        if (density >= LOW_DENSITY_THRESHOLD) {
            return LOW_DENSITY_FILAMENT;
        }
        return isRareVoidGalaxy(x, y, z, density, detail) ? VOID_GALAXY : AIR;
    }

    private static boolean isRareVoidGalaxy(int x, int y, int z, float density, float detail) {
        if (density > VOID_DENSITY_LIMIT) {
            return false;
        }

        int cellX = Math.floorDiv(x, VOID_CELL_SIZE);
        int cellY = Math.floorDiv(y, VOID_CELL_SIZE);
        int cellZ = Math.floorDiv(z, VOID_CELL_SIZE);
        long hash = mix64(0x243F6A8885A308D3L
                ^ (long) cellX * 0x9E3779B185EBCA87L
                ^ (long) cellY * 0xC2B2AE3D27D4EB4FL
                ^ (long) cellZ * 0x165667B19E3779F9L);
        if ((hash & 0x3FL) != 0L) {
            return false;
        }

        int centerX = cellX * VOID_CELL_SIZE + (int) ((hash >>> 8) & 31L);
        int centerY = cellY * VOID_CELL_SIZE + (int) ((hash >>> 16) & 31L);
        int centerZ = cellZ * VOID_CELL_SIZE + (int) ((hash >>> 24) & 31L);
        int dx = x - centerX;
        int dy = y - centerY;
        int dz = z - centerZ;
        float radius = 1.35F + Math.abs(detail) * 1.65F;
        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }

    private static long mix64(long value) {
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }
}
