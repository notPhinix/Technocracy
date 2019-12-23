package net.cydhra.technocracy.foundation.model.tileentities.api.upgrades

import net.cydhra.technocracy.foundation.content.tileentities.components.MachineUpgradesTileEntityComponent
import net.cydhra.technocracy.foundation.model.tileentities.api.TCAggregatable

/**
 * Models instances of upgrades that can be granted by upgrade items. Does not take any parameters, as different
 * upgrades may encompass different approaches on how they improve their machine: One might add a multiplier on a
 * value, another one may just unlock a new slot in the machine, thus not requiring parameters in its instance.
 */
abstract class MachineUpgrade(val upgradeType: MachineUpgradeParameter) {

    /**
     * @return true, iff the upgrade can be installed in the given tile entity.
     */
    abstract fun canInstallUpgrade(tile: TCAggregatable): Boolean

    /**
     * Called when the upgrade is installed in the given tile entity.
     *
     * @param tile the tile entitiy that this upgrade is installed in
     * @param upgrades the upgrade component this upgrade is installed in
     */
    abstract fun onInstallUpgrade(tile: TCAggregatable,
            upgrades: MachineUpgradesTileEntityComponent)
}

/**
 * Upgrades that modify a machine multiplier are derived from this class and are handled specially.
 *
 * @param multiplier the multiplier that is added onto the machine multiplier, as long as the update is installed
 */
abstract class MultiplierUpgrade(val multiplier: Double, parameterName: MachineUpgradeParameter)
    : MachineUpgrade(parameterName) {
    override fun canInstallUpgrade(tile: TCAggregatable): Boolean {
        return true
    }

    override fun onInstallUpgrade(tile: TCAggregatable,
            upgrades: MachineUpgradesTileEntityComponent) {
    }
}

/**
 * Models exactly one parameter of a machine that can be modified. Actual upgrade items likely modify multiple
 * parameters, either positively or negatively.
 */
typealias MachineUpgradeParameter = String

/**
 * Every upgrade item has a class assigned and certain machines may only accept certain upgrade classes (so a
 * pulverizer is not upgradeable with a "semipermeable membrane" or something of that kind)
 */
enum class MachineUpgradeClass(val unlocalizedName: String) {
    MECHANICAL("mechanical"),
    ELECTRICAL("electrical"),
    MAGNETIC("magnetic"),
    OPTICAL("optical"),
    THERMAL("thermal"),
    CHEMICAL("chemical"),
    NUCLEAR("nuclear"),
    ALIEN("alien");
}