package net.cydhra.technocracy.foundation.integration.waila.providers

import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import mcp.mobius.waila.api.IWailaDataProvider
import net.cydhra.technocracy.foundation.tileentity.MachineTileEntity
import net.cydhra.technocracy.foundation.tileentity.components.EnergyStorageComponent
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class MachineProvider: IWailaDataProvider {

    override fun getWailaBody(itemStack: ItemStack?, tooltip: MutableList<String>?, accessor: IWailaDataAccessor?, config: IWailaConfigHandler?): MutableList<String> {
        accessor!!
        tooltip!!
        config!!

        if(!accessor.nbtData.hasKey("TCMachine")) return tooltip
        val tag: NBTTagCompound = accessor.nbtData.getCompoundTag("TCMachine")
        if(tag.hasKey("currentEnergy") && tag.hasKey("maxEnergy") && config.getConfig("capability.energyinfo")) {
            tooltip.add("${tag.getInteger("currentEnergy")}RF/${tag.getInteger("maxEnergy")}RF")
        }
        return tooltip
    }

    override fun getNBTData(player: EntityPlayerMP?, te: TileEntity?, tag: NBTTagCompound?, world: World?, pos: BlockPos?): NBTTagCompound {
        if(te is MachineTileEntity) {
            val compound = NBTTagCompound()
            te.getComponents().forEach {
                if(it.second is EnergyStorageComponent) {
                    compound.setInteger("currentEnergy", (it.second as EnergyStorageComponent).energyStorage.currentEnergy)
                    compound.setInteger("maxEnergy", (it.second as EnergyStorageComponent).energyStorage.capacity)
                    tag!!.setTag("TCMachine", compound)
                }
            }
        }
        return tag!!
    }

}