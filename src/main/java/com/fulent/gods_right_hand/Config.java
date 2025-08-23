package com.fulent.gods_right_hand;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@SuppressWarnings("unused")
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SHOW_MOUSE = BUILDER
            .define("showMouse", true);

    public static final ModConfigSpec.BooleanValue DO_CAPTURE_PICK_ITEM_BEHAVIOUR = BUILDER
            .define("capturePickItemBehaviour", true);

    public static final ModConfigSpec.BooleanValue KEEP_RIGHT_HAND = BUILDER
            .define("keepRightHand", true);

    public static final ModConfigSpec.BooleanValue DO_HIDE_KEY_TIP = BUILDER
            .define("hideKeyTip", false);

    public static final ModConfigSpec.DoubleValue SENSITIVITY = BUILDER
            .defineInRange("sensitivity", 1.5, 0.1, 10.0);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
