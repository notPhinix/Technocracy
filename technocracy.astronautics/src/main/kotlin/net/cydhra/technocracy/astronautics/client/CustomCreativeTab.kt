package net.cydhra.technocracy.astronautics.client

import net.cydhra.technocracy.astronautics.content.blocks.scaffoldBlock
import net.cydhra.technocracy.astronautics.content.blocks.wetReinforcedConcreteBlock
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack


/**
 * Creative tab for all items of this mod
 */
val astronauticsCreativeTabs = object : CreativeTabs("astronautics.main") {
    init {
        this.backgroundImageName = "items.png"
    }

    override fun getTabIconItem(): ItemStack {
        return ItemStack(ItemBlock.getItemFromBlock(scaffoldBlock))
    }

    override fun hasSearchBar(): Boolean {
        return false
    }
}
/**
 * Creative tab for all colored blocks of this mod
 */
val astronauticsColorTabs = object : CreativeTabs("astronautics.color") {
    init {
        this.backgroundImageName = "items.png"
    }

    override fun getTabIconItem(): ItemStack {
        return ItemStack(ItemBlock.getItemFromBlock(wetReinforcedConcreteBlock))
    }

    override fun hasSearchBar(): Boolean {
        return false
    }
}