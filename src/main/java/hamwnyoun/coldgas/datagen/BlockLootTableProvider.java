package hamwnyoun.coldgas.datagen;

import hamwnyoun.coldgas.register.BlockReg;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class BlockLootTableProvider  extends BlockLootSubProvider {
    protected BlockLootTableProvider(LootTableSubProvider.Context output) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, output);
    }

    @Override
    protected void generate() {
        dropSelf(BlockReg.VERY_DENSE_CLUSTER.get());
       // dropSelf(BlockReg. .get());



    }


    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BlockReg.BLOCKS.getEntries()
                .stream()
                .map(Holder::value)
                .toList();
    }

}
