package net.cydhra.technocracy.foundation.content.tileentities.logic

import net.cydhra.technocracy.foundation.content.tileentities.components.ConsumptionMultiplierComponent
import net.cydhra.technocracy.foundation.content.tileentities.components.FluidComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.logic.ILogic
import kotlin.math.roundToInt

/**
 * A logic that consumes an additive to a machine and disables the machine progress if no additive is remaining
 */
class AdditiveConsumptionLogic(private val additiveComponent: FluidComponent,
                               private val baseConsumption: Int,
                               private val consumptionMultiplier: ConsumptionMultiplierComponent) : ILogic {
    override fun preProcessing(): Boolean {
        return additiveComponent.fluid.currentFluid?.amount ?: 0 >= baseConsumption
    }

    override fun processing() {
        additiveComponent.fluid.drain((baseConsumption * this.consumptionMultiplier.energyMultiplier).roundToInt(), true)
    }

    override fun postProcessing(wasProcessing: Boolean) {

    }
}