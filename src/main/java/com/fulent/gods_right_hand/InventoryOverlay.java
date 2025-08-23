package com.fulent.gods_right_hand;

import org.lwjgl.glfw.GLFW;

import com.ibm.icu.impl.Pair;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.minecraft.client.Minecraft;

public class InventoryOverlay extends Screen {

    public static int convertCoordToIndex(int x, int y) {
        var temp = x + y * 9;
        return (temp >= 27) ? temp - 27 : temp + 9;
    }

    public static Pair<Integer, Integer> convertIndexToCoord(int index) {
        index = (index < 9) ? index + 27 : index - 9;
        return Pair.of(index % 9, index / 9);
    }

    public static Pair<Integer, Integer> convertMouseToCoord(int mouseX, int mouseY) {
        var x = mouseX - xMin;
        var y = mouseY - yMin;
        return Pair.of(x / 20, y / 20);
    }

    public static void setMouseToSelectedSlot()

    {
        int guiScale = (int) Minecraft.getInstance().getWindow().getGuiScale();
        if (Config.SHOW_MOUSE.get()) {
            GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().getWindow(),
                    (double) (guiScale * (dx + xMin)),
                    (double) (guiScale * (dy + yMin)));
        }
    }

    public InventoryOverlay(Screen parent, int initialPosition) {
        super(Component.literal("Overlay"));

        var initialCoord = convertIndexToCoord(initialPosition);

        selectedX = initialCoord.first;
        selectedY = initialCoord.second;

        dx = 10 + (selectedX) * 20;
        dy = 10 + (selectedY) * 20;
    }

    public static final ResourceLocation InventoryOverlayTexture = ResourceLocation.fromNamespaceAndPath(
            GodsRightHand.MODID,
            "textures/gui/inventory_overlay.png");

    public static final ResourceLocation SelectedSlotTexture = ResourceLocation.fromNamespaceAndPath(
            GodsRightHand.MODID,
            "textures/gui/selected_slot.png");

    public static final ResourceLocation RightHandtexture = ResourceLocation.fromNamespaceAndPath(
            GodsRightHand.MODID,
            "textures/gui/right_hand.png");

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static int getExtraItemIndex() {
        return convertCoordToIndex(selectedX, selectedY);
    }

    public static int xMin, yMin;

    public static int dx = 182 / 2, dy = 82 / 2 - 10;

    public static int selectedX;
    public static int selectedY;

    public static float sensitivity = 2f;

    public static String indicator;

    @Override
    protected void init() {
        super.init();
        // 计算纹理左上角坐标
        xMin = width / 2 - 182 / 2;
        yMin = height / 2 - 82 / 2 + 10;

        indicator = String.format(Component.translatable("gods_right_hand.press_to_open_config").getString(),
                GodsRightHandClient.OPEN_CONFIGURATION.getKey().getDisplayName().getString());

    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {

        if (!Config.SHOW_MOUSE.get()) {
            selectedX = (dx + (int) (mouseX - width / 2)) / 20;
            selectedY = (dy + (int) (mouseY - height / 2)) / 20;

            selectedX = Math.clamp(selectedX, 0, 8);
            selectedY = Math.clamp(selectedY, 0, 3);

        } else {
            dx = (int) (mouseX - xMin);
            dy = (int) (mouseY - yMin);
            dx = Math.clamp(dx, 0, 182 - 3);
            dy = Math.clamp(dy, 0, 82 - 3);
            selectedX = dx / 20;
            selectedY = dy / 20;
        }

        Minecraft.getInstance().player.getInventory().selected = getExtraItemIndex();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GodsRightHandClient.OPEN_CONFIGURATION.getKey().getValue()) {

            var container = ModList.get().getModContainerById(GodsRightHand.MODID).get();

            Minecraft.getInstance().setScreen(null);
            Minecraft.getInstance().setScreen(new ConfigurationScreen(container, null));
            return false;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return false;
        }
        return true;
    }

    // 绘制叠加层
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 绘制纹理
        RenderSystem.enableBlend();
        graphics.blit(InventoryOverlayTexture, xMin, yMin, 0, 0, 182, 82, 182, 82);
        graphics.blit(SelectedSlotTexture, xMin + selectedX * 20 - 1,
                yMin + selectedY * 20 - 1,
                0, 0, 24, 24, 24, 24);
        RenderSystem.disableBlend();

        if (!Config.DO_HIDE_KEY_TIP.get())
            graphics.drawCenteredString(font, Component.literal(indicator), width / 2, height / 2 + 60, 0xFFFF00);

        var initX = xMin + 3;
        var initY = yMin + 3;

        var inventory = Minecraft.getInstance().player.getInventory();
        for (int i = 0; i < 9; i++) {
            var stack = inventory.getItem(i);
            if (stack.isEmpty())
                continue;
            graphics.renderItem(stack, initX + i % 9 * 20, initY + (i / 9 + 3) * 20);
            graphics.renderItemDecorations(font, stack, initX + i % 9 * 20, initY + (i / 9 + 3) * 20);

        }

        for (int i = 9; i < 36; i++) {
            var stack = inventory.getItem(i);
            if (stack.isEmpty())
                continue;
            graphics.renderItem(stack, initX + (i) % 9 * 20, initY + (i - 9) / 9 * 20);
            graphics.renderItemDecorations(font, stack, initX + (i) % 9 * 20, initY + (i - 9) / 9 * 20);

        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);

    }
}