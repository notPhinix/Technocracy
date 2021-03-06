package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCContainer
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent

open class MachineContainer(val machine: MachineTileEntity) : TCContainer(
        (machine.getComponents().filter { (name, component) -> component is InventoryTileEntityComponent && name.contains("input") }
                .elementAtOrNull(0)?.second as? InventoryTileEntityComponent)?.inventory?.slots ?: 0,
        (machine.getComponents().filter { (name, component) -> component is InventoryTileEntityComponent && name.contains("output") }
                .elementAtOrNull(0)?.second as? InventoryTileEntityComponent)?.inventory?.slots ?: 0
)