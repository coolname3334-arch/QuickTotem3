package com.quicktotem.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {

    /** Current mouse X position in screen coordinates (updated every render tick) */
    @Accessor("mouseX")
    double getMouseX();

    /** Current mouse Y position in screen coordinates (updated every render tick) */
    @Accessor("mouseY")
    double getMouseY();

    /** Left edge of the GUI panel in screen pixels */
    @Accessor("leftPos")
    int getLeftPos();

    /** Top edge of the GUI panel in screen pixels */
    @Accessor("topPos")
    int getTopPos();
}
