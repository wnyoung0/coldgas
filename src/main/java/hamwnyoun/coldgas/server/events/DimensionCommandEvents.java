package hamwnyoun.coldgas.server.events;

import java.util.Set;
import com.mojang.brigadier.Command;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import hamwnyoun.coldgas.ColdGas;

/** Server-side command registration and dimension travel. */
@EventBusSubscriber(modid = ColdGas.MODID)
public final class DimensionCommandEvents {
    private static final ResourceKey<Level> COSMIC_WEB_DIMENSION = ResourceKey.create(
            Registries.DIMENSION, Identifier.fromNamespaceAndPath(ColdGas.MODID, "2357"));

    private DimensionCommandEvents() {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("coldgas")
                .then(Commands.literal("dimension")
                        .executes(context -> teleport(context.getSource().getPlayerOrException()))));
    }

    private static int teleport(ServerPlayer player) {
        ServerLevel destination = player.level().getServer().getLevel(COSMIC_WEB_DIMENSION);
        if (destination == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "차원 coldgas:2357을 찾을 수 없습니다. 월드가 차원 리소스를 불러왔는지 확인하세요."));
            return 0;
        }

        BlockPos surface = destination.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, BlockPos.ZERO);
        boolean moved = player.teleportTo(destination, surface.getX() + 0.5, surface.getY() + 300.0,
                surface.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot(), true);
        return moved ? Command.SINGLE_SUCCESS : 0;
    }
}
