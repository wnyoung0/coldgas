package hamwnyoun.coldgas.datagen;

import hamwnyoun.coldgas.register.BlockReg;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static hamwnyoun.coldgas.ColdGas.MODID;

public class ModBlockTagProvider extends BlockTagsProvider {
    // Get parameters from one of the `GatherDataEvent`s.



    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MODID);
    }


    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {

        this.tag(BlockTags.WITHER_IMMUNE)
                .add(BlockReg.VERY_DENSE_CLUSTER.key());


        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(BlockReg.VERY_DENSE_CLUSTER.key());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(BlockReg.VERY_DENSE_CLUSTER.key());


    }

}
