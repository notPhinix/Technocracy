package net.cydhra.technocracy.foundation.content.tileentities.components

import net.cydhra.technocracy.foundation.conduits.types.PipeType
import net.cydhra.technocracy.foundation.model.components.ComponentType
import net.cydhra.technocracy.foundation.model.tileentities.api.components.AbstractTileEntityComponent
import net.minecraft.nbt.NBTTagCompound
import java.util.*


class PipeTypesTileEntityComponent : AbstractTileEntityComponent() {

    override val type: ComponentType = ComponentType.OTHER

    var types = mutableSetOf<PipeType>()

    override fun serializeNBT(): NBTTagCompound {
        val base = NBTTagCompound()
        base.setInteger("amount", types.size)
        types.forEachIndexed { index, type ->
            base.setString(index.toString(), type.name)
        }

        return base
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        if (nbt.hasKey("amount")) {
            val amount = nbt.getInteger("amount")

            for (i in 0 until amount) {
                val name = nbt.getString("$i")
                val optional = Arrays.stream(PipeType.values()).filter { it.name == name }.findFirst()
                if (optional.isPresent)
                    types.add(optional.get())
            }
        }
    }
}