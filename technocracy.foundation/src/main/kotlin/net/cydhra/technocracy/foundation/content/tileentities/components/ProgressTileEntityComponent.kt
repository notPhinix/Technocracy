package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.model.components.ComponentType
import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractTileEntityComponent
import net.minecraft.nbt.NBTTagCompound

class ProgressTileEntityComponent: AbstractTileEntityComponent() {

    override val type: ComponentType = ComponentType.OTHER

    /**
     * progress from 0 to 100
     */
    var progress: Int = 0

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply { setInteger("progress", progress) }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        progress = nbt.getInteger("progress")
    }

}