package net.cydhra.technocracy.foundation.content.tileentities.machines

import net.cydhra.technocracy.foundation.content.capabilities.fluid.DynamicFluidCapability
import net.cydhra.technocracy.foundation.content.capabilities.inventory.DynamicInventoryCapability
import net.cydhra.technocracy.foundation.content.fluids.hydrochloricAcidFluid
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.InventoryTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.MultiplierTileEntityComponent
import net.cydhra.technocracy.foundation.content.tileentities.logic.AdditiveConsumptionLogic
import net.cydhra.technocracy.foundation.content.tileentities.logic.ItemProcessingLogic
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_ADDITIVE_CONSUMPTION
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_ENERGY
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_GENERIC
import net.cydhra.technocracy.foundation.content.tileentities.upgrades.MACHINE_UPGRADE_SPEED
import net.cydhra.technocracy.foundation.data.crafting.IMachineRecipe
import net.cydhra.technocracy.foundation.data.crafting.RecipeManager
import net.cydhra.technocracy.foundation.model.tileentities.api.TEInventoryProvider
import net.cydhra.technocracy.foundation.model.tileentities.api.upgrades.MachineUpgradeClass
import net.cydhra.technocracy.foundation.model.tileentities.machines.MachineTileEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

/**
 *
 */
class TileEntityChemicalEtchingChamber : MachineTileEntity(), TEInventoryProvider {
    private val inputInventory = InventoryTileEntityComponent(3, this, EnumFacing.EAST)
    private val outputInventoryComponent = InventoryTileEntityComponent(1, this, EnumFacing.WEST,
            DynamicInventoryCapability.InventoryType.OUTPUT)

    private val additiveMultiplierComponent = MultiplierTileEntityComponent(MACHINE_UPGRADE_ADDITIVE_CONSUMPTION)

    private val acidFluidInput = FluidTileEntityComponent(4000, hydrochloricAcidFluid.name,
            tanktype = DynamicFluidCapability.TankType.INPUT, facing = mutableSetOf(EnumFacing.NORTH))

    private val upgradesComponent = MachineUpgradesTileEntityComponent(3,
            setOf(MACHINE_UPGRADE_ENERGY, MACHINE_UPGRADE_SPEED, MACHINE_UPGRADE_ADDITIVE_CONSUMPTION,
                    MACHINE_UPGRADE_GENERIC),
            setOf(MachineUpgradeClass.CHEMICAL, MachineUpgradeClass.OPTICAL,
                    MachineUpgradeClass.ELECTRICAL, MachineUpgradeClass.ALIEN),
            setOf(this.processingSpeedComponent, this.energyCostComponent, this.additiveMultiplierComponent))

    /**
     * All recipes of the etching chamber; loaded lazily so they are not loaded before game loop, as they might not have
     * been registered yet.
     */
    private val recipes: Collection<IMachineRecipe> by lazy {
        (RecipeManager.getMachineRecipesByType(RecipeManager.RecipeType.CHEMICAL_ETCHING) ?: emptyList())
    }

    init {
        registerComponent(inputInventory, "input")
        registerComponent(outputInventoryComponent, "output")
        registerComponent(acidFluidInput, "acid")
        registerComponent(additiveMultiplierComponent, "additive_speed")
        registerComponent(upgradesComponent, "upgrades")

        this.addLogicStrategy(AdditiveConsumptionLogic(acidFluidInput, 5, additiveMultiplierComponent),
                MACHINE_DEFAULT_CONSUMPTION_LOGIC_NAME)
        this.addLogicStrategy(ItemProcessingLogic(
                RecipeManager.RecipeType.CHEMICAL_ETCHING,
                inputInventory = inputInventory.inventory,
                outputInventory = outputInventoryComponent.inventory,
                energyStorage = this.energyStorageComponent.energyStorage,
                processSpeedComponent = this.processingSpeedComponent,
                energyCostComponent = this.energyCostComponent,
                progress = this.progressComponent,
                baseTickEnergyCost = 120
        ), MACHINE_PROCESSING_LOGIC_NAME)
    }


    override fun isItemValid(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack): Boolean {
        return if (inventory == this.inputInventory.inventory) {
            this.recipes.any { it.getInput()[slot].test(stack) }
        } else {
            false
        }
    }

    override fun onSlotUpdate(inventory: DynamicInventoryCapability, slot: Int, stack: ItemStack, originalStack: ItemStack) {}
}
