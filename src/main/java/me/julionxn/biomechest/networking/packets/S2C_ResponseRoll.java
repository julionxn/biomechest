package me.julionxn.biomechest.networking.packets;

import me.julionxn.biomechest.screen.ViewScreen;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;

public class S2C_ResponseRoll {
    public static void onClient(MinecraftClient client, ClientPlayNetworkHandler handler,
                                PacketByteBuf buf, PacketSender sender) {
        List<ItemStack> roll = buf.readCollection(ArrayList::new, PacketByteBuf::readItemStack);
        client.execute(() -> {
            if (client.currentScreen instanceof ViewScreen screen){
                screen.responseRoll(roll);
            }
        });
    }
}
