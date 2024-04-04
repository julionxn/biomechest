package me.julionxn.biomechest.networking;

import me.julionxn.biomechest.BiomeChest;
import me.julionxn.biomechest.networking.packets.C2S_RequestRoll;
import me.julionxn.biomechest.networking.packets.S2C_OpenViewScreen;
import me.julionxn.biomechest.networking.packets.S2C_ResponseRoll;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class AllPackets {

    //Client->Server
    public static final Identifier C2S_REQUEST_ROLL = of("request_roll");

    //Server->Client
    public static final Identifier S2C_OPEN_VIEW_SCREEN = of("open_view_screen");
    public static final Identifier S2C_RESPONSE_ROLL = of("response_roll");

    public static void registerS2CPackets() {
        s2c(S2C_OPEN_VIEW_SCREEN, S2C_OpenViewScreen::onClient);
        s2c(S2C_RESPONSE_ROLL, S2C_ResponseRoll::onClient);
    }

    public static void registerC2SPackets() {
        c2s(C2S_REQUEST_ROLL, C2S_RequestRoll::onServer);
    }


    private static Identifier of(String packetName) {
        return new Identifier(BiomeChest.ID, packetName);
    }

    private static void c2s(Identifier identifier, ServerPlayNetworking.PlayChannelHandler handler) {
        ServerPlayNetworking.registerGlobalReceiver(identifier, handler);
    }

    private static void s2c(Identifier identifier, ClientPlayNetworking.PlayChannelHandler handler) {
        ClientPlayNetworking.registerGlobalReceiver(identifier, handler);
    }

}
