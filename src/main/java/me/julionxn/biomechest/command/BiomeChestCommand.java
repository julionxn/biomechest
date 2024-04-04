package me.julionxn.biomechest.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.julionxn.biomechest.networking.AllPackets;
import me.julionxn.biomechest.persistent.Config;
import me.julionxn.biomechest.persistent.Data;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BiomeChestCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("biomechest").requires(source -> source.hasPermissionLevel(2))
                .then(
                        CommandManager.literal("reload").executes(BiomeChestCommand::reload)
                )
                .then(
                        CommandManager.literal("view").executes(BiomeChestCommand::view)
                )
        );

    }

    /**
     * Recarga la configuración.
     * @param ctx CommandContext
     * @return Si el comando se ejecutó exitosamente.
     */
    private static int reload(CommandContext<ServerCommandSource> ctx){
        ServerCommandSource source = ctx.getSource();
        if (!source.isExecutedByPlayer()) return -1;
        Config.getInstance().load();
        source.sendFeedback(Text.of("Loot tables reloaded."), true);
        return 1;
    }

    /**
     * Abre el menú de vista para el jugador.
     * @param ctx CommandContext
     * @return si el comando se ejecutó exitosamente.
     */
    private static int view(CommandContext<ServerCommandSource> ctx){
        ServerCommandSource source = ctx.getSource();
        if (!source.isExecutedByPlayer() || source.getPlayer() == null) return -1;
        //Serializar las entries
        PacketByteBuf buf = PacketByteBufs.create();
        Data data = Config.getInstance().getData();
        Set<String> biomes = data.getBiomes();
        buf.writeInt(biomes.size());
        for (String biome : biomes) {
            List<String> markedOptions = new ArrayList<>();
            markedOptions.add(biome);
            List<String> options = data.getOptionsOf(biome);
            if (options == null) continue;
            markedOptions.addAll(options);
            buf.writeCollection(markedOptions, PacketByteBuf::writeString);
        }
        ServerPlayNetworking.send(source.getPlayer(), AllPackets.S2C_OPEN_VIEW_SCREEN, buf);
        return 1;
    }

}
