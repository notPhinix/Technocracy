package net.cydhra.technocracy.foundation.tileentity.components

import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyStorage
import net.cydhra.technocracy.foundation.capabilities.energy.DynamicEnergyStorageStategy
import net.cydhra.technocracy.foundation.capabilities.energy.EnergyCapabilityProvider
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
 * A machine component that handles energy storage and transfer speeds of the machine. The storage defaults to a
 * capacity of 8000 and a receiving limit of 8000 FE. If a machine wants to change the defaults, it has to do so in
 * its constructor.
 */
class EnergyStorageComponent : AbstractCapabilityComponent() {

    companion object {
        private const val NBT_KEY_ENERGY = "energy"
    }

    /**
     * The energy storage capability instance containing energy storage state
     */
    val energyStorage: DynamicEnergyStorage = DynamicEnergyStorage(
            capacity = 8000,
            currentEnergy = 0,
            extractionLimit = 0,
            receivingLimit = 8000)

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return capability == EnergyCapabilityProvider.CAPABILITY_ENERGY
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return EnergyCapabilityProvider.CAPABILITY_ENERGY!!.cast(this.energyStorage)
    }

    override fun writeToNBT(compound: NBTTagCompound) {
        compound.setTag(NBT_KEY_ENERGY, DynamicEnergyStorageStategy.writeNBT(this.energyStorage))
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        if (compound.hasKey(NBT_KEY_ENERGY)) {
            DynamicEnergyStorageStategy.readNBT(this.energyStorage, compound.getCompoundTag("energy"))
        }
    }


}