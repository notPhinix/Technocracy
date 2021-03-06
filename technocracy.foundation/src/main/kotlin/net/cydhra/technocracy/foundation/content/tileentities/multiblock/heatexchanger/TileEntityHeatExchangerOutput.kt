package net.cydhra.technocracy.foundation.content.tileentities.multiblock.heatexchanger

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.multiblock.HeatExchangerMultiBlock
import net.cydhra.technocracy.foundation.model.tileentities.impl.AggregatableDelegate
import net.cydhra.technocracy.foundation.model.tileentities.api.TCAggregatable
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.multiblock.TileEntityMultiBlockPart
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class TileEntityHeatExchangerOutput : TileEntityMultiBlockPart<HeatExchangerMultiBlock>(HeatExchangerMultiBlock::class,
        ::HeatExchangerMultiBlock), TCAggregatable by AggregatableDelegate() {

    private val internalFluidHandler = DynamicFluidCapability(4000, allowedFluid = mutableListOf(),
            tanktype = DynamicFluidCapability.TankType.OUTPUT)

    private val fluidComponent = FluidTileEntityComponent(internalFluidHandler, mutableSetOf(EnumFacing.NORTH, EnumFacing.EAST,
            EnumFacing.SOUTH, EnumFacing.WEST))

    init {
        this.registerComponent(fluidComponent, "output")
    }

    override fun onMachineActivated() {}

    override fun onMachineDeactivated() {}

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (multiblockController != null && multiblockController!!.isAssembled && facing != null) {
            capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
        } else false
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return (this.castCapability(capability, facing) ?: super.getCapability(capability, facing))
                ?: DynamicFluidCapability(1, allowedFluid = mutableListOf()) as T
    }

    override fun onActivate(world: World, pos: BlockPos, player: EntityPlayer, hand: EnumHand, facing: EnumFacing) {

    }
}
