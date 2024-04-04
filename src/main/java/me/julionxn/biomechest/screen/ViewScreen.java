package me.julionxn.biomechest.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.julionxn.biomechest.BiomeChest;
import me.julionxn.biomechest.networking.AllPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ViewScreen extends Screen {

    private static final Identifier CHEST_GUI = new Identifier(BiomeChest.ID, "chest.png");
    private static final Identifier VIEW_BG = new Identifier(BiomeChest.ID, "viewbg.png");
    private int biomesIndex = 0;
    private int optionsIndex = 0;
    private Entry activeEntry;
    private String activeOption;
    private List<ItemStack> roll;
    private final List<Entry> entries = new ArrayList<>();
    private final int itemsPerPage = 8;

    public ViewScreen(List<List<String>> entries) {
        super(Text.of("ViewScreen"));
        if (entries.isEmpty() && client != null){
            client.setScreen(null);
            return;
        }
        for (List<String> entry : entries) {
            String biomeKey = entry.get(0);
            entry.remove(0);
            this.entries.add(new Entry(biomeKey, entry));
        }
        activeEntry = this.entries.get(0);
    }

    @Override
    protected void init() {
        if (client == null) return;
        Window window = client.getWindow();
        int centerX = window.getScaledWidth() / 2;
        int centerY = window.getScaledHeight() / 2;
        addBiomeButtons(centerX, centerY);
        addOptionsButtons(centerX, centerY);
    }

    private void addBiomeButtons(int centerX, int centerY){
        centerY -= 120;
        centerX -= 170;
        int offset = 0;
        for (int i = biomesIndex; i - biomesIndex < Math.min(itemsPerPage, entries.size()); i++) {
            Entry entry = entries.get(i);
            int finalI = i;
            ButtonWidget btn = ButtonWidget.builder(Text.of(entry.biomeKey), button -> {
                        activeEntry = entries.get(finalI);
                        optionsIndex = 0;
                        clearAndInit();
                    }).dimensions(centerX, centerY + offset, 150, 20).build();
            addDrawableChild(btn);
            offset += 20;
        }
        ButtonWidget upList = ButtonWidget.builder(Text.of("↑"), button -> changeBiomeIndex(-1))
                .dimensions(centerX + 150, centerY, 20, 20).build();
        ButtonWidget downList = ButtonWidget.builder(Text.of("↓"), button -> changeBiomeIndex(1))
                .dimensions(centerX + 150, centerY + 20, 20, 20).build();
        addDrawableChild(upList);
        addDrawableChild(downList);
    }

    private void changeBiomeIndex(int in) {
        if (in == 0) return;
        if (biomesIndex + in < 0) return;
        if (biomesIndex + itemsPerPage + in > entries.size()) return;
        biomesIndex += in;
        clearAndInit();
    }

    private void addOptionsButtons(int centerX, int centerY){
        centerY -= 120;
        List<String> options = activeEntry.options();
        int offset = 0;
        for (int i = optionsIndex; i - optionsIndex < Math.min(itemsPerPage, options.size()); i++) {
            String option = options.get(i);
            ButtonWidget btn = ButtonWidget.builder(Text.of(option), button -> {
                activeOption = option;
                requestRoll();
            }).dimensions(centerX, centerY + offset, 150, 20).build();
            addDrawableChild(btn);
            offset += 20;
        }
        ButtonWidget upList = ButtonWidget.builder(Text.of("↑"), button -> changeOptionIndex(-1))
                .dimensions(centerX + 150, centerY, 20, 20).build();
        ButtonWidget downList = ButtonWidget.builder(Text.of("↓"), button -> changeOptionIndex(1))
                .dimensions(centerX + 150, centerY + 20, 20, 20).build();
        addDrawableChild(upList);
        addDrawableChild(downList);
    }

    private void changeOptionIndex(int in) {
        if (in == 0) return;
        if (optionsIndex + in < 0) return;
        List<String> options = entries.get(biomesIndex).options();
        if (optionsIndex + itemsPerPage + in > options.size()) return;
        optionsIndex += in;
        clearAndInit();
    }

    private void requestRoll(){
        ClientPlayNetworking.send(AllPackets.C2S_REQUEST_ROLL, PacketByteBufs.create().writeString(activeOption));
    }

    public void responseRoll(List<ItemStack> roll){
        this.roll = roll;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (client == null) return;
        Window window = client.getWindow();
        int x = window.getScaledWidth() / 2;
        int y = window.getScaledHeight() / 2;
        RenderSystem.setShaderTexture(0, VIEW_BG);
        RenderSystem.enableBlend();
        drawTexture(matrices, x - 175, y - 125, 0, 0, 350, 170, 350, 170);
        RenderSystem.disableBlend();
        super.render(matrices, mouseX, mouseY, delta);
        if (roll == null) return;
        renderRoll(matrices, x , y);
    }

    private void renderRoll(MatrixStack matrices, int x, int y){
        x -= 84;
        y += 50;
        RenderSystem.setShaderTexture(0, CHEST_GUI);
        drawTexture(matrices, x, y, 0, 0, 168, 60, 168, 60);
        for (int i = 0; i < roll.size(); i++) {
            ItemStack stack = roll.get(i);
            int drawX = x + 4 + (i % 9) * 18;
            int drawY = y + 4 + (i / 9) * 18;
            this.itemRenderer.renderInGuiWithOverrides(matrices, stack, drawX, drawY);
            this.itemRenderer.renderGuiItemOverlay(matrices, this.textRenderer, stack,
                    drawX, drawY, String.valueOf(stack.getCount()));
        }
    }

    private record Entry(String biomeKey, List<String> options) {
    }

}
