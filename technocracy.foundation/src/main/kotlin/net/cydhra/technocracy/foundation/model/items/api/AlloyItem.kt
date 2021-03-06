package net.cydhra.technocracy.foundation.model.items.api

import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.model.items.api.ColoredPrefixedItem
import net.cydhra.technocracy.foundation.model.items.color.ConstantItemColor

/**
 * Any additional ingots created by this mod are ultimately an instance of this class
 */
class AlloyItem(ingotMaterialName: String, color: ConstantItemColor)
    : ColoredPrefixedItem("ingot", ingotMaterialName, color) {

    override val modelLocation: String = "${TCFoundation.MODID}:alloy"
}