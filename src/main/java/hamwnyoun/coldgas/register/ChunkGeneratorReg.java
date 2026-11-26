package hamwnyoun.coldgas.register;

import com.mojang.serialization.MapCodec;
import hamwnyoun.coldgas.worldgen.ImprovedCosmicWebChunkGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static hamwnyoun.coldgas.ColdGas.MODID;

public class ChunkGeneratorReg {

    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, MODID);


    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<? extends ChunkGenerator>> COSMIC_WEB_GENERATOR =
            CHUNK_GENERATORS.register("cosmic_web", () -> ImprovedCosmicWebChunkGenerator.CODEC);

}
