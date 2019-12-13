package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.components.ComponentType
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeClass
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeParameter
import net.minecraft.nbt.NBTTagCompound

/**
 * A machine component that handles all machine upgrades.
 *
 * @param supportedUpgradeTypes a set of supported upgrade types. If a player tries to install an upgrade into the
 * machine, all of its upgrade types must be supported for installation to work.
 * @param numberOfUpgradeSlots how many upgrade slots the machine has.
 */
class MachineUpgradesComponent(val numberOfUpgradeSlots: Int,
                               val supportedUpgradeTypes: Set<MachineUpgradeParameter>,
                               val acceptsUpgradeClasses: Set<MachineUpgradeClass>) : AbstractComponent() {

    companion object {
        private const val NBT_KEY_UPGRADES = "upgrades"
    }

    override val type: ComponentType = ComponentType.OTHER

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {

    }
}