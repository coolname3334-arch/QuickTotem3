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

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void quicktotem_keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();

        if (!mc.options.keySwapOffhand.matches(keyCode, scanCode)) {
            return;
        }

        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this;
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        AbstractContainerMenu menu = screen.getMenu();
        List<Slot> slots = menu.slots;

        final int OFFHAND_CONTAINER_SLOT_INDEX = 45;
        if (OFFHAND_CONTAINER_SLOT_INDEX >= slots.size()) {
            return;
        }

        Slot offhandSlot = slots.get(OFFHAND_CONTAINER_SLOT_INDEX);
        if (!offhandSlot.getItem().isEmpty()) {
            return;
        }

        double mouseX = accessor.getMouseX();
        double mouseY = accessor.getMouseY();
        int guiLeft = accessor.getLeftPos();
        int guiTop = accessor.getTopPos();

        Slot closestTotemSlot = null;
        double closestDist = Double.MAX_VALUE;

        for (Slot slot : slots) {
            if (slot.index == OFFHAND_CONTAINER_SLOT_INDEX) continue;
            ItemStack stack = slot.getItem();
            if (!stack.is(Items.TOTEM_OF_UNDYING)) continue;

            double slotCenterX = guiLeft + slot.x + 8;
            double slotCenterY = guiTop + slot.y + 8;
            double dist = Math.hypot(slotCenterX - mouseX, slotCenterY - mouseY);

            if (dist < closestDist) {
                closestDist = dist;
                closestTotemSlot = slot;
            }
        }

        if (closestTotemSlot == null) {
            return;
        }

        mc.gameMode.handleInventoryMouseClick(
            menu.containerId,
            closestTotemSlot.index,
            40,
            net.minecraft.world.inventory.ClickType.SWAP,
            mc.player
        );

        cir.setReturnValue(true);
    }
}
