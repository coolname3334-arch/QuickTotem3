package com.quicktotem.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void quicktotem_keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();

        // Check offhand key - in 26.1, KeyMapping#matches takes keyCode and scanCode
        if (!mc.options.keySwapOffhand.matches(keyCode, scanCode)) {
            return;
        }

        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this;
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        AbstractContainerMenu menu = screen.getMenu();
        List<Slot> slots = menu.slots;

        final int OFFHAND_SLOT = 45;
        if (OFFHAND_SLOT >= slots.size()) return;
        if (!slots.get(OFFHAND_SLOT).getItem().isEmpty()) return;

        double mouseX = accessor.getMouseX();
        double mouseY = accessor.getMouseY();
        int guiLeft = accessor.getLeftPos();
        int guiTop = accessor.getTopPos();

        Slot closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Slot slot : slots) {
            if (slot.index == OFFHAND_SLOT) continue;
            if (!slot.getItem().is(Items.TOTEM_OF_UNDYING)) continue;

            double cx = guiLeft + slot.x + 8;
            double cy = guiTop + slot.y + 8;
            double dist = Math.hypot(cx - mouseX, cy - mouseY);

            if (dist < closestDist) {
                closestDist = dist;
                closest = slot;
            }
        }

        if (closest == null) return;

        // Send swap packet directly — button 40 = offhand, type 2 = SWAP
        mc.getConnection().send(new ServerboundContainerClickPacket(
            menu.containerId,
            menu.getStateId(),
            closest.index,
            40,
            ClickType.SWAP,
            closest.getItem().copy(),
            new HashMap<>()
        ));

        cir.setReturnValue(true);
    }
}
