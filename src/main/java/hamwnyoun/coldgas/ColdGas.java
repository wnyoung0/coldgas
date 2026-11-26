package hamwnyoun.coldgas;

import com.mojang.logging.LogUtils;
import hamwnyoun.coldgas.register.BlockReg;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

import static hamwnyoun.coldgas.register.ChunkGeneratorReg.CHUNK_GENERATORS;

@Mod(ColdGas.MODID)
public class ColdGas {
    public static final String MODID = "coldgas";
    public static final Logger LOGGER = LogUtils.getLogger();



    public ColdGas(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        //registers
        CHUNK_GENERATORS.register(modEventBus);

        BlockReg.BLOCKS.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.LOCAL, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("ColdGas noise-based cosmic web dimension registered");
    }
}
