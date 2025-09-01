package com.fulent.gods_right_hand.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerPlayerConnection;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(ServerGamePacketListenerImpl.class)
abstract public class ServerMixin
        implements ServerPlayerConnection, TickablePacketListener, ServerGamePacketListener {
        @Redirect(method = "handleSetCarriedItem", at = @At(value = "INVOKE", target =
         "Lnet/minecraft/world/entity/player/Inventory;getSelectionSize()I"))
        private int getSelectionSize() {
            return 36;
        }
}
