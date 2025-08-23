package com.fulent.gods_right_hand.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;

import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;

@Mixin(Gui.class)
public class GuiMixin {

    @Redirect(method = "renderItemHotbar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1 // 指定第几个调用（从0开始计数）
            ))

    private void onFirstBlitSpriteCall(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height) {
        if(x<graphics.guiWidth()/2+82){
            graphics.blitSprite(sprite, x, y, width, height);
        }        
    }

}
