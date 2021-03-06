package net.cydhra.technocracy.foundation.network.componentsync

import net.cydhra.technocracy.foundation.model.components.IComponent
import net.cydhra.technocracy.foundation.model.multiblock.api.BaseMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.cydhra.technocracy.foundation.network.PacketHandler
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.event.entity.player.PlayerContainerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

val guiInfoPacketSubscribers = mutableMapOf<EntityPlayerMP, Pair<BlockPos, Int>>()
val lastGuiStates = mutableMapOf<EntityPlayerMP, MutableMap<Pair<BlockPos, Int>, NBTTagCompound>>() // uff

class GuiUpdateListener {

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    fun onInventoryClose(event: PlayerContainerEvent.Close) {
        guiInfoPacketSubscribers.remove(event.entityPlayer)
    }

    @SubscribeEvent
    fun onTick(event: TickEvent) {
        if (event.side == Side.SERVER) {
            guiInfoPacketSubscribers.forEach { (player, tePos) ->
                val world = DimensionManager.getWorld(tePos.second)
                if (world.isBlockLoaded(tePos.first)) {
                    val te = world.getTileEntity(tePos.first)
                    var tag: NBTTagCompound? = null
                    if (te is MachineTileEntity) {
                        tag = getTagForMachine(te.getComponents())
                    } else if (te is TileEntityMultiBlockPart<*>) {
                        if(te.multiblockController != null)
                            tag = getTagForMachine((te.multiblockController as BaseMultiBlock).getComponents())
                    }

                    if (tag != null) {
                        tag.setLong("pos", tePos.first.toLong())
                        lastGuiStates.putIfAbsent(player, mutableMapOf())
                        if (lastGuiStates[player]!![tePos] != tag) {
                            lastGuiStates[player]!![tePos] = tag
                            PacketHandler.sendToClient(MachineInfoPacket(tag), player)
                        }
                    }
                }
            }
        }
    }

    fun getTagForMachine(components: MutableList<Pair<String, IComponent>>): NBTTagCompound {
        val tag = NBTTagCompound()
        components.forEach { (name, component) ->
            tag.setTag(name, component.serializeNBT())
        }
        return tag
    }

}