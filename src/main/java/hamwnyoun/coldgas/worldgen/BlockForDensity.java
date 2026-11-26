package hamwnyoun.coldgas.worldgen;

import hamwnyoun.coldgas.register.BlockReg;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockForDensity {


    public static final BlockState LOW_DENSITY_FILAMENT =
            BlockReg.BARE_DENSE_CLUSTER.get().defaultBlockState();

    public static final BlockState FILAMENT =
            BlockReg.MILD_DENSE_CLUSTER.get().defaultBlockState();

    public static final BlockState DENSE_FILAMENT =
            BlockReg.QUITE_DENSE_CLUSTER.get().defaultBlockState();

    public static final BlockState NODE =
            BlockReg.VERY_DENSE_CLUSTER.get().defaultBlockState();

    public static final BlockState DENSE_NODE =
            Blocks.GLOWSTONE.defaultBlockState();



    public static final BlockState AIR =
            Blocks.AIR.defaultBlockState();

    public static final BlockState VOID_GALAXY =
            LOW_DENSITY_FILAMENT;//Blocks.STAINED_GLASS.white().defaultBlockState();

    public static final BlockState SPAWN =
            Blocks.STAINED_GLASS.magenta().defaultBlockState();

}
