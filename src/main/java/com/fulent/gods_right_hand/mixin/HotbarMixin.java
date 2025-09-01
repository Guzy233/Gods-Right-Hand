package com.fulent.gods_right_hand.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fulent.gods_right_hand.*;

import net.minecraft.core.NonNullList;

@Mixin(Inventory.class)
abstract public class HotbarMixin implements Container, Nameable {
    @Shadow
    public int selected;

    @Shadow
    public NonNullList<ItemStack> items;

    @Overwrite
    public ItemStack getSelected() {
        return items.get(selected);
    }

    @Overwrite
    public static boolean isHotbarSlot(int index) {
        return index >= 0 && index < 36;
    }

    @Inject(method = "swapPaint", at = @At("HEAD"), cancellable = true)
    public void swapPaint(double direction, CallbackInfo ci) {
        if (Config.KEEP_RIGHT_HAND.get() && GodsRightHandClient.restoredIndex != -1) {
            // 若保持右手为开，且存在提前保存的右手索引，恢复右手,同时拦截后续处理
            if ((selected == 8 && direction < 0) || selected == 0 && direction > 0) {
                selected = GodsRightHandClient.restoredIndex;
                GodsRightHandClient.restoredIndex = -1;

                ci.cancel();
                return;
            }
        }

        if (selected >= 9) {
            // 若保持右手为开，且当前槽位不为空，保存当前槽位索引
            if (Config.KEEP_RIGHT_HAND.get() && !items.get(selected).isEmpty())
                GodsRightHandClient.restoredIndex = selected;

            if (direction > 0) {
                selected = 9;
            } else {
                selected = -1;
            }
        }
    }

    @Inject(method = "pickSlot", at = @At("HEAD"), cancellable = true)
    public void pickSlot(int index, CallbackInfo ci) {
        if (Config.DO_CAPTURE_PICK_ITEM_BEHAVIOUR.get() &&
                GodsRightHandClient.hasGodsRightHand() &&
                !items.get(index).isEmpty()) {
            selected = index;
            ci.cancel();
        }
    }

}
