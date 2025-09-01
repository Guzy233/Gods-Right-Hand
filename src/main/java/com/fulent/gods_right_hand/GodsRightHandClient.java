package com.fulent.gods_right_hand;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import top.theillusivec4.curios.api.CuriosApi;

@Mod(value = GodsRightHand.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = GodsRightHand.MODID, value = Dist.CLIENT)
public class GodsRightHandClient {
    public static final String CATEGORY = "key.category.gods_right_hand.general";

    public static InventoryOverlay overlay;

    public static boolean shouldActive = false;

    public static Minecraft minecraft;

    public static int restoredIndex = -1;

    public static boolean isCuriosInstalled = false;

    // 定义你的按键绑定
    public static final KeyMapping OPEN_TOOL_GUI = new KeyMapping(
            "key.gods_right_hand.open_tool_gui",
            GLFW.GLFW_KEY_V,
            CATEGORY // 按键分类
    );

    public static final KeyMapping OPEN_CONFIGURATION = new KeyMapping(
            "key.gods_right_hand.open_configuration",
            GLFW.GLFW_KEY_C,
            CATEGORY // 按键分类
    );

    public GodsRightHandClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        minecraft = Minecraft.getInstance();
        isCuriosInstalled = ModList.get().isLoaded("curios");
    }

    @SubscribeEvent
    public static void onScreenRender(RenderGuiEvent.Post event) {
        if (!shouldActive && minecraft.player.getInventory().getSelected().isEmpty()) {
            if (minecraft.player.getInventory().selected > 8)
                restoredIndex = -1;
            while (minecraft.player.getInventory().selected > 8) {
                minecraft.player.getInventory().selected -= 9;
            }
        }

        GuiGraphics graphics = event.getGuiGraphics();

        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        int x = screenWidth / 2 + 91;
        int y = screenHeight - 23;

        if (minecraft.player.getInventory().selected < 9 && restoredIndex != -1) {
            graphics.blit(InventoryOverlay.RightHandtexture, x, y, 0, 0, 29, 24, 29, 24);

            graphics.renderItem(minecraft.player.getInventory().getItem(restoredIndex), x + 10, y + 4);
            graphics.renderItemDecorations(minecraft.font, minecraft.player.getInventory().getItem(restoredIndex),
                    x + 10,
                    y + 4);
        } else if (minecraft.player.getInventory().selected >= 9) {
            graphics.blit(InventoryOverlay.SelectedSlotTexture, x + 6, y, 0, 0, 24, 24,
                    24, 24);
            graphics.renderItem(minecraft.player.getInventory().getSelected(), x + 10, y + 4);
            graphics.renderItemDecorations(minecraft.font, minecraft.player.getInventory().getSelected(), x + 10,
                    y + 4);
        }

    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_TOOL_GUI);
        event.register(OPEN_CONFIGURATION);
    }

    public static boolean hasGodsRightHand() {
        var player = minecraft.player;
        if (player == null)
            return false;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == GodsRightHand.GODS_RIGHT_HAND.get()) {
                return true;
            }
        }

        if (isCuriosInstalled) {
            return CuriosApi.getCuriosInventory(player)
                    .flatMap(inv -> inv.findFirstCurio(GodsRightHand.GODS_RIGHT_HAND.get()))
                    .isPresent();
        }
        return false;
    }

    @SubscribeEvent
    public static void onKeyEvent(InputEvent.Key event) {
        if (event.getKey() != OPEN_TOOL_GUI.getKey().getValue())
            return;
        KeyHandler(event.getAction());
    }

    @SubscribeEvent
    public static void onKeyEvent(InputEvent.MouseButton.Post event) {
        if (Config.DO_CAPTURE_PICK_ITEM_BEHAVIOUR.get() == false)
            return;
        if (event.getButton() != minecraft.options.keyPickItem.getKey().getValue())
            return;
        KeyHandler(event.getAction());
    }

    public static void KeyHandler(int action){
        if (!hasGodsRightHand())
            return;

        var minecraft = Minecraft.getInstance();
        if (action == InputConstants.PRESS && minecraft.screen == null) {
            shouldActive = true;
        }

        if (action == InputConstants.RELEASE && minecraft.screen == overlay) {
            overlay.onClose();
        }
    }

    public static void ActiveOverlay()
    {
        shouldActive = false;
        overlay = new InventoryOverlay(Minecraft.getInstance().screen,
                minecraft.player.getInventory().selected);
        minecraft.setScreen(overlay);

        if (!Config.SHOW_MOUSE.get()) {
            var xpos = (double) (minecraft.getWindow().getScreenWidth() / 2);
            var ypos = (double) (minecraft.getWindow().getScreenHeight() / 2);

            InputConstants.grabOrReleaseMouse(minecraft.getWindow().getWindow(),
                    212995, xpos, ypos);
        }
        InventoryOverlay.setMouseToSelectedSlot();
    }

    @SubscribeEvent
    public static void onTick(Post event) {
        if (shouldActive && minecraft.screen == null) {
            ActiveOverlay();
        }
    }
}
