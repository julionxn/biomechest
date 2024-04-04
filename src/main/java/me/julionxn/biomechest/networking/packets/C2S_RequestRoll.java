package me.julionxn.biomechest.networking.packets;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.julionxn.biomechest.networking.AllPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class C2S_RequestRoll {
    public static void onServer(MinecraftServer server, ServerPlayerEntity player,
                                ServerPlayNetworkHandler serverPlayNetworkHandler,
                                PacketByteBuf buf, PacketSender sender) {

        String id = buf.readString();
        server.execute(() -> {
            Identifier lootId = Identifier.tryParse(id);
            if (lootId == null) return;
            LootTable lootTable = server.getLootManager().getTable(lootId);
            LootContext context = new LootContext.Builder(player.getWorld())
                    .parameter(LootContextParameters.ORIGIN, player.getPos())
                    .build(LootContextTypes.CHEST);
            ObjectArrayList<ItemStack> loot = lootTable.generateLoot(context);
            PacketByteBuf buf2 = PacketByteBufs.create();
            buf2.writeCollection(loot, PacketByteBuf::writeItemStack);
            ServerPlayNetworking.send(player, AllPackets.S2C_RESPONSE_ROLL, buf2);
        });
    }
}
