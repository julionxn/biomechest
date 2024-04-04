package me.julionxn.biomechest.networking.packets;

import me.julionxn.biomechest.screen.ViewScreen;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;

public class S2C_OpenViewScreen {

    public static void onClient(MinecraftClient client, ClientPlayNetworkHandler handler,
                                PacketByteBuf buf, PacketSender sender) {
        //Obtener del paquete las entries para poder mostrar en el menú
        List<List<String>> entries = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            List<String> markedOptions = buf.readCollection(ArrayList::new, PacketByteBuf::readString);
            entries.add(markedOptions);
        }
        client.execute(() -> {
            MinecraftClient.getInstance().setScreen(new ViewScreen(entries));
        });
    }
}
