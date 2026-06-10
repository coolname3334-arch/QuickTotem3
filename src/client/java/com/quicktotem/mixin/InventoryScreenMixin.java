package com.quicktotem.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
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
            ItemStack stack = slot.getItem();
            if (!stack.is(Items.TOTEM_OF_UNDYING)) continue;

            double cx = guiLeft + slot.x + 8;
            double cy = guiTop + slot.y + 8;
            double dist = Math.hypot(cx - mouseX, cy - mouseY);

            if (dist < closestDist) {
                closestDist = dist;
                closest = slot;
            }
        }

        if (closest == null) return;

        // SWAP click type = 2, button 40 = offhand
        mc.gameMode.handleInventoryMouseClick(
            menu.containerId,
            closest.index,
            40,
            net.minecraft.world.inventory.ClickType.valueOf("SWAP"),
            mc.player
        );

        cir.setReturnValue(true);
    }
}
