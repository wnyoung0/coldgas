package hamwnyoun.coldgas.client.events;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import hamwnyoun.coldgas.ColdGas;

@Mod(value = ColdGas.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = ColdGas.MODID, value = Dist.CLIENT)
public class ColdGasClient {
    public ColdGasClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        ColdGas.LOGGER.info("ColdGas client setup complete for {}", Minecraft.getInstance().getUser().getName());
    }
}
