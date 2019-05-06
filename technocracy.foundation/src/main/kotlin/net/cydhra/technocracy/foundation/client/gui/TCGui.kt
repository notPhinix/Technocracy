package net.cydhra.technocracy.foundation.client.gui

import net.cydhra.technocracy.foundation.client.gui.components.TCSlot
import net.cydhra.technocracy.foundation.client.gui.tabs.TCTab
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

open class TCGui(val player: EntityPlayer, val guiWidth: Int = 176, val guiHeight: Int = 166, val container:
TCContainer)
    : GuiContainer(container) {

    data class Rectangle(val x: Int, val y: Int, val width: Int, val height: Int)

    companion object {
        val guiComponents: ResourceLocation = ResourceLocation("technocracy.foundation", "textures/gui/components.png")

        const val windowBodyColor = 0xFFC6C6C6.toInt()
        const val inactiveTabTint = 0xFF505050.toInt()

        val left = Rectangle(0, 4, 4, 1)
        val right = Rectangle(6, 3, 4, 1)
        val top = Rectangle(4, 0, 1, 4)
        val bottom = Rectangle(4, 6, 1, 4)
        val cornerTopLeft = Rectangle(0, 0, 4, 4)
        val cornerTopRight = Rectangle(7, 0, 3, 3)
        val cornerBottomLeft = Rectangle(0, 7, 3, 3)
        val cornerBottomRight = Rectangle(6, 6, 4, 4)
    }

    val tabs: ArrayList<TCTab> = ArrayList()

    private var tab: Int = 0

    init {
        this.xSize = guiWidth
        this.ySize = guiHeight
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        val x = (width - xSize) / 2
        val y = (height - ySize) / 2

        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toDouble(), y.toDouble(), 0.0)

        drawWindow(0.0, 0.0, xSize, ySize)

        if (tabs.isNotEmpty()) {
            drawTabs(partialTicks, mouseX, mouseY)
        }

        this.tabs[this.tab].draw(mouseX, mouseY, partialTicks)
        GlStateManager.popMatrix()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        this.tabs.indices.filterNot { it == this.tab }.forEach {
            val x = xSize + 3
            val y = it * 28 + 3
            val width = 25
            val height = 25

            if (this.isPointInRegion(x, y, width, height, mouseX, mouseY)) {
                this.tab = it

                this.tabs.withIndex()
                        .forEach { (index, tab) ->
                            tab.components
                                    .filterIsInstance<TCSlot>()
                                    .map { index to it }
                                    .forEach { pair -> pair.second.isEnabled = pair.first == it }
                        }
            }
        }
    }

    private fun drawTabs(partialTicks: Float, mouseX: Int, mouseY: Int) {
        this.tabs.withIndex().filterNot { it.index == this.tab }.forEach { (i, tab) ->
            val x = xSize.toDouble() + 3
            val y = (i * 28).toDouble() + 3
            val width = 25
            val height = 25

            drawWindow(x, y, width, height, tab.tint and inactiveTabTint, true)

            if (tab.icon != null) {
                GlStateManager.pushMatrix()
                GlStateManager.translate(x + (width - 16) / 2, y + (height - 16) / 2 + 2, 0.0)
                Minecraft.getMinecraft().textureManager.bindTexture(tab.icon)
                GlStateManager.color(1F, 1F, 1F, 1F)
                drawModalRectWithCustomSizedTexture(0, 0, 0F, 0F, 17, 17, 17F, 17F)
                GlStateManager.popMatrix()
            }
        }

        val activeTab: TCTab = this.tabs[this.tab]
        val activeTabX = xSize.toDouble()
        val activeTabY = (this.tab * 28).toDouble()
        val tabWidth = 28
        val tabHeight = 28

        drawWindow(activeTabX, activeTabY, tabWidth, tabHeight, activeTab.tint and -1, true)

        if (activeTab.icon != null) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(activeTabX + (tabWidth - 16) / 2, activeTabY + (tabHeight - 16) / 2 + 1, 0.0)
            Minecraft.getMinecraft().textureManager.bindTexture(activeTab.icon)
            GlStateManager.color(1F, 1F, 1F, 1F)
            drawModalRectWithCustomSizedTexture(0, 0, 0F, 0F, 17, 17, 17F, 17F)
            GlStateManager.popMatrix()
        }
    }

    fun drawWindow(x: Double, y: Double, width: Int, height: Int, tint: Int = -1, windowAttachment: Boolean = false) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, 0.0)

        Gui.drawRect(if (windowAttachment) 0 else 4, 4, width, height, windowBodyColor and tint)

        GlStateManager.color((tint shr 8 and 255).toFloat() / 255.0F, (tint and 255).toFloat() / 255.0F,
                (tint shr 16 and 255).toFloat() / 255.0F, (tint shr 24 and 255).toFloat() / 255.0F)

        Minecraft.getMinecraft().textureManager.bindTexture(guiComponents)

        for (i in 3 until height) {
            if (!windowAttachment) {
                drawTexturedModalRect(0, i, left.x, left.y, left.width, left.height)
            }

            drawTexturedModalRect(width - 1, i, right.x, right.y, right.width, right.height)
        }

        for (i in (if (windowAttachment) 0 else 3) until width) {
            drawTexturedModalRect(i, 0, top.x, top.y, top.width, top.height)
            drawTexturedModalRect(i, height - 1, bottom.x, bottom.y, bottom.width, bottom.height)
        }

        if (!windowAttachment) {
            drawTexturedModalRect(0, 0, cornerTopLeft.x, cornerTopLeft.y, cornerTopLeft.width, cornerTopLeft.height)
            drawTexturedModalRect(0, height, cornerBottomLeft.x, cornerBottomLeft.y, cornerBottomLeft.width, cornerBottomLeft.height)
        }

        drawTexturedModalRect(width, 0, cornerTopRight.x, cornerTopRight.y, cornerTopRight.width, cornerTopRight.height)
        drawTexturedModalRect(width - 1, height - 1, cornerBottomRight.x, cornerBottomRight.y, cornerBottomRight.width,
                cornerBottomRight.height)
        GlStateManager.popMatrix()
    }

    override fun updateScreen() {
        this.tabs[this.tab].update()
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    fun registerTab(tab: TCTab) {
        this.tabs.add(tab)
        tab.init()

        tab.components.forEach { this.container.registerComponent(it) }
    }

    fun unregisterTab(tab: TCTab) {
        this.tabs.remove(tab)
    }
}