package com.quicktotem.mixin;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {

    /**
     * Intercepts the offhand key (F by default) while the player inventory is open.
     *
     * If the offhand slot is empty and there are totems of undying in the inventory,
     * equips the totem closest to the mouse cursor into the offhand instead of
     * performing the normal vanilla swap.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void quicktotem_keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();

        // Only act if the "swap with offhand" key was pressed
        if (!mc.options.keySwapOffhand.matches(keyCode, scanCode)) {
            return;
        }

        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this;
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        AbstractContainerMenu menu = screen.getMenu();
        List<Slot> slots = menu.slots;

        // In the player InventoryMenu, the offhand slot is at container slot index 45
        final int OFFHAND_CONTAINER_SLOT_INDEX = 45;
        if (OFFHAND_CONTAINER_SLOT_INDEX >= slots.size()) {
            return;
        }

        Slot offhandSlot = slots.get(OFFHAND_CONTAINER_SLOT_INDEX);

        // Only proceed if the offhand is currently empty
        if (!offhandSlot.getItem().isEmpty()) {
            return;
        }

        // Get mouse position from the accessor
        double mouseX = accessor.getMouseX();
        double mouseY = accessor.getMouseY();
        int guiLeft = accessor.getLeftPos();
        int guiTop = accessor.getTopPos();

        // Find the totem of undying slot closest to the mouse cursor
        Slot closestTotemSlot = null;
        double closestDist = Double.MAX_VALUE;

        for (Slot slot : slots) {
            if (slot.index == OFFHAND_CONTAINER_SLOT_INDEX) continue;

            ItemStack stack = slot.getItem();
            if (!stack.is(Items.TOTEM_OF_UNDYING)) continue;

            // Slot x/y are relative to the GUI panel; convert to screen space
            double slotCenterX = guiLeft + slot.x + 8;
            double slotCenterY = guiTop + slot.y + 8;
            double dist = Math.hypot(slotCenterX - mouseX, slotCenterY - mouseY);

            if (dist < closestDist) {
                closestDist = dist;
                closestTotemSlot = slot;
            }
        }

        if (closestTotemSlot == null) {
            // No totem found — let vanilla handle the keypress normally
            return;
        }

        // Send a SWAP click packet: ClickType.SWAP with button=40 means offhand
        // This is exactly what vanilla does when you press F over a slot
        mc.gameMode.handleInventoryMouseClick(
            menu.containerId,
            closestTotemSlot.index,
            40,              // 40 = offhand slot index in the player's inventory
            ClickType.SWAP,
            mc.player
        );

        cir.setReturnValue(true); // consume the key event so vanilla doesn't also run
    }
}
