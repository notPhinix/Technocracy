package net.cydhra.technocracy.foundation.client.gui.machine

import net.cydhra.technocracy.foundation.client.gui.TCGui
import net.cydhra.technocracy.foundation.client.gui.TCTab
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.util.ResourceLocation

// nullable platform type: its null if the te is not placed in the world (required for jei)
@Suppress("UNNECESSARY_SAFE_CALL")
abstract class BaseMachineTab(val machine: MachineTileEntity, parent: TCGui, icon: ResourceLocation) :
        TCTab(name = machine.blockType?.localizedName ?: "", parent = parent, icon = icon)

