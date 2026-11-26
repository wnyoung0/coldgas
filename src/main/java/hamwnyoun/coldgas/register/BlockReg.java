package hamwnyoun.coldgas.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static hamwnyoun.coldgas.ColdGas.MODID;

public class BlockReg {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    public static DeferredBlock<Block> generateGalaxies(String name, MapColor mapColor, float destroyTime, float explosionResistance, int light){
        return BLOCKS.register(
                name,
                registryName -> new TransparentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                        .setId(ResourceKey.create(Registries.BLOCK, registryName))
                        .mapColor(mapColor)
                        .sound(SoundType.NYLIUM)
                        .instrument(NoteBlockInstrument.SNARE)
                        .requiresCorrectToolForDrops()
                        .strength(destroyTime,explosionResistance)
                        .noOcclusion()
                        .lightLevel((p_220871_) -> {
                            return light;
                        })

                ));
    }
    public static DeferredBlock<Block> generateGalaxies(String name, MapColor mapColor){
        return generateGalaxies(name, mapColor,5.0F,1200F,15);
    }


    public static final DeferredBlock<Block> VERY_DENSE_CLUSTER =
            generateGalaxies("very_dense_cluster",MapColor.QUARTZ);

    public static final DeferredBlock<Block> QUITE_DENSE_CLUSTER =
            generateGalaxies("quite_dense_cluster",MapColor.COLOR_MAGENTA);

    public static final DeferredBlock<Block> MILD_DENSE_CLUSTER =
            generateGalaxies("mild_dense_cluster",MapColor.QUARTZ);

    public static final DeferredBlock<Block> BARE_DENSE_CLUSTER =
            generateGalaxies("bare_dense_cluster",MapColor.COLOR_MAGENTA,5.0F,1200F,8);




}
