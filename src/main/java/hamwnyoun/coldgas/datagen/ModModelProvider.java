package hamwnyoun.coldgas.datagen;

import hamwnyoun.coldgas.register.BlockReg;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;

import static hamwnyoun.coldgas.ColdGas.MODID;

public class ModModelProvider extends ModelProvider {

    public ModModelProvider(PackOutput output){
        super(output,MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators){
        //item
        //itemModelGenerators.generateFlatItem(ItemReg.AUSTENITE_INGOT.get(), ModelTemplates.FLAT_ITEM);


        //block
        //   blockModelGenerators.createTrivialCube(BlockReg.ANTI_CORROSION_OBSIDIAN.get());
        //   blockModelGenerators.createGlassBlocks(BlockReg.ANTI_CORROSION_OBSIDIAN_GLASS.get(),BlockReg.ANTI_CORROSION_OBSIDIAN_GLASS_PANE.get());
        //  blockModelGenerators.createGlassBlocks(BlockReg.OBSIDIAN_GLASS.get(),BlockReg.OBSIDIAN_GLASS_PANE.get());
        blockModelGenerators.createNonTemplateModelBlock(BlockReg.VERY_DENSE_CLUSTER.get());


    }


}
