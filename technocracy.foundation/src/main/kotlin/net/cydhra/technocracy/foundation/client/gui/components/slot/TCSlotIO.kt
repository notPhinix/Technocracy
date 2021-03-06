package net.cydhra.technocracy.foundation.client.gui.components.slot

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Items
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

/**
 * A gui slot that is used to draw tile-entity inventory slots. For player inventory slots, see [TCSlotPlayer]
 */
class TCSlotIO(itemHandler: IItemHandler, index: Int, xPosition: Int, yPosition: Int, val gui: TCGui) :
        SlotItemHandler(itemHandler, index, xPosition, yPosition), ITCSlot {

    private var enabledOverride = true

    override fun update() {
    }

    override fun draw(x: Int, y: Int, mouseX: Int, mouseY: Int, partialTicks: Float) {
        Minecraft.getMinecraft().textureManager.bindTexture(TCGui.guiComponents)
        GlStateManager.color(1F, 1F, 1F, 1F)
        GuiContainer.drawModalRectWithCustomSizedTexture(xPos - 1 + x, yPos - 1 + y, 0F, 10F, 18, 18, 256F,
                256F)
    }

    override fun drawTooltip(mouseX: Int, mouseY: Int) {
        val stack = itemHandler.getStackInSlot(slotIndex)
        if (stack.item != Items.AIR)
            gui.renderHoveredItemToolTip(mouseX, mouseY)
    }

    override fun mouseClicked(x: Int, y: Int, mouseX: Int, mouseY: Int, mouseButton: Int) {}

    override fun isMouseOnComponent(mouseX: Int, mouseY: Int): Boolean {
        return mouseX > xPos && mouseX < xPos + 18 && mouseY > yPos && mouseY < yPos + 18
    }

    override fun setEnabled(enabled: Boolean) {
        this.enabledOverride = enabled
    }

    /**
     * This method overrides [net.minecraft.inventory.Slot.isEnabled] and allows that to be ignored by our own value.
     */
    override fun isEnabled(): Boolean {
        return super.isEnabled() && this.enabledOverride
    }
}
