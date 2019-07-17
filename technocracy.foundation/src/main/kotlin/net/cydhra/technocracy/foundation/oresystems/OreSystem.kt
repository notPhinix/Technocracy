@file:Suppress("unused")

package net.cydhra.technocracy.foundation.oresystems

import com.google.common.base.Predicate
import net.cydhra.technocracy.foundation.TCFoundation
import net.cydhra.technocracy.foundation.blocks.OreBlock
import net.cydhra.technocracy.foundation.blocks.general.BlockManager
import net.cydhra.technocracy.foundation.crafting.RecipeManager
import net.cydhra.technocracy.foundation.crafting.types.*
import net.cydhra.technocracy.foundation.items.color.ConstantItemColor
import net.cydhra.technocracy.foundation.items.general.BaseItem
import net.cydhra.technocracy.foundation.items.general.ColoredItem
import net.cydhra.technocracy.foundation.items.general.ItemManager
import net.cydhra.technocracy.foundation.liquids.general.BaseFluid
import net.cydhra.technocracy.foundation.liquids.general.FluidManager
import net.cydhra.technocracy.foundation.liquids.general.drossFluid
import net.cydhra.technocracy.foundation.world.gen.OreGenerator
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.block.state.pattern.BlockMatcher
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.ResourceLocation
import net.minecraft.world.DimensionType
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.registry.GameRegistry
import java.awt.Color

fun oreSystem(block: OreSystemBuilder.() -> Unit) = OreSystemBuilder().apply(block).build()

class OreSystem(
        val materialName: String,
        val ore: Block,
        val ingot: Item,
        val dust: Item,
        val crystal: ColoredItem,
        val grit: ColoredItem,
        val gear: ColoredItem?,
        val sheet: ColoredItem?,
        val slag: BaseFluid,
        val slurry: BaseFluid,
        val enrichedSlurry: BaseFluid,
        val preInit: OreSystem.(BlockManager, ItemManager, FluidManager) -> Unit,
        val init: OreSystem.() -> Unit)

class OreSystemBuilder {
    lateinit var name: String
    var color: Int = 0

    private var generateOre = true
    private var generateIngot = true
    private var generateDust = true
    private var intermediates: Array<out IntermediateProductType> = emptyArray()

    private lateinit var ore: Block
    private lateinit var ingot: Item
    private lateinit var dust: Item

    private lateinit var oreGeneratorSettings: OreGeneratorSettings

    /**
     * Import the ore block from elsewhere instead of creating own ore block. This prevents any registration of
     * ores in the world
     */
    fun importOre(ore: Block) {
        this.generateOre = false
        this.ore = ore
    }

    /**
     * Import the ingot item from elsewhere instead of creating own ingot item.
     */
    fun importIngot(ingot: Item) {
        this.generateIngot = false
        this.ingot = ingot
    }

    /**
     * Import the dust item from elsewhere instead of creating own dust item.
     */
    fun importDust(dust: Item) {
        this.generateDust = false
        this.dust = dust
    }

    /**
     * Overwrite the set of intermediate production goods generated of this ore type.
     */
    fun create(vararg types: IntermediateProductType) {
        this.intermediates = types
    }

    fun generate(settings: OreGeneratorSettings.() -> Unit) {
        this.oreGeneratorSettings = OreGeneratorSettings().apply(settings)
    }

    /**
     * Build an ore system from all settings given
     */
    fun build(): OreSystem {
        val itemColor = ConstantItemColor(this.color)

        if (generateOre)
            this.ore = OreBlock(this.name, this.color)

        if (generateIngot)
            this.ingot = ColoredItem("ingot", this.name, itemColor, true)

        if (generateDust)
            this.dust = ColoredItem("dust", this.name, itemColor, true)

        val sheet = if (IntermediateProductType.SHEET in intermediates)
            ColoredItem("sheet", this.name, itemColor, true)
        else
            null

        val gear = if (IntermediateProductType.GEAR in intermediates)
            ColoredItem("gear", this.name, itemColor, true)
        else
            null

        return OreSystem(
                materialName = this.name,
                ore = this.ore,
                ingot = this.ingot,
                dust = this.dust,
                crystal = ColoredItem("crystal", this.name, itemColor, true),
                grit = ColoredItem("grit", this.name, itemColor, true),
                gear = gear,
                sheet = sheet,
                slag = BaseFluid("slag.$name", Color(this.color), opaqueTexture = true),
                slurry = BaseFluid("slurry.$name", Color(this.color).darker(), opaqueTexture = true),
                enrichedSlurry = BaseFluid("enriched_slurry.$name", Color(this.color).darker().darker(), opaqueTexture = true),
                preInit = { blockManager, itemManager, fluidManager ->
                    if (this.ingot is BaseItem)
                        itemManager.prepareItemForRegistration(this.ingot)
                    if (this.dust is BaseItem)
                        itemManager.prepareItemForRegistration(this.dust)
                    if (this.sheet != null)
                        itemManager.prepareItemForRegistration(this.sheet)
                    if (this.gear != null)
                        itemManager.prepareItemForRegistration(this.gear)

                    itemManager.prepareItemForRegistration(this.crystal)
                    itemManager.prepareItemForRegistration(this.grit)

                    if (this.ore is OreBlock)
                        blockManager.prepareBlocksForRegistration(this.ore)

                    fluidManager.registerFluid(this.slag)
                    fluidManager.registerFluid(this.slurry)
                    fluidManager.registerFluid(this.enrichedSlurry)
                },
                init = {
                    // add default ingot recipe
                    GameRegistry.addSmelting(ore, ItemStack(ingot, 1), 0.5f)

                    // add dust smelting recipe
                    GameRegistry.addSmelting(dust, ItemStack(ingot, 1), 0.5f)

                    // add slag recipe
                    // TODO: there is now way to combine item and fluid to fluid

                    // add slurry recipe
                    RecipeManager.registerRecipe(RecipeManager.RecipeType.ELECTROLYSIS,
                            ElectrolysisRecipe(FluidStack(slag, 1000),
                                    listOf(FluidStack(slurry, 500), FluidStack(drossFluid, 500)),
                                    200))

                    // add slurry enriching recipe
                    RecipeManager.registerRecipe(RecipeManager.RecipeType.KILN,
                            KilnRecipe(FluidStack(slurry, 500),
                                    FluidStack(enrichedSlurry, 500),
                                    200))

                    // add crystal recipe
                    // TODO add crystallizer machine

                    // add grit recipe
                    RecipeManager.registerRecipe(RecipeManager.RecipeType.PULVERIZER,
                            PulverizerRecipe(Ingredient.fromItem(crystal),
                                    ItemStack(grit, 2),
                                    100))

                    // add dust recipe
                    RecipeManager.registerRecipe(RecipeManager.RecipeType.CENTRIFUGE,
                            CentrifugeRecipe(Ingredient.fromItem(grit),
                                    ItemStack(dust, 1),
                                    null,
                                    100))

                    // add gear recipe
                    if (gear != null)
                        GameRegistry.addShapedRecipe(ResourceLocation(TCFoundation.MODID, gear.registryName!!
                                .resourcePath + "_recipe"), null, ItemStack(gear), " # ", "# #", " # ", '#', ingot)

                    // add sheet recipe
                    if (sheet != null)
                        RecipeManager.registerRecipe(RecipeManager.RecipeType.COMPACTOR,
                                CompactorRecipe(Ingredient.fromItem(ingot),
                                        ItemStack(sheet),
                                        40))

                    if (generateOre)
                        GameRegistry.registerWorldGenerator(OreGenerator(
                                oreGeneratorSettings.oreDimensions,
                                oreGeneratorSettings.replacementPredicate,
                                this.ore.defaultState,
                                oreGeneratorSettings.veinsPerChunk,
                                oreGeneratorSettings.amountPerVein,
                                oreGeneratorSettings.minHeight,
                                oreGeneratorSettings.maxHeight), 0)
                }
        )
    }

    /**
     * An enumeration of all intermediate products that can be obtained from metals
     */
    enum class IntermediateProductType {
        GEAR, SHEET
    }

    /**
     * Settings for ore generation, if the ore is not imported from elsewhere
     */
    class OreGeneratorSettings {
        var oreDimensions: Array<Int> = arrayOf(DimensionType.OVERWORLD.id)
        var replacementPredicate: Predicate<IBlockState> = BlockMatcher.forBlock(Blocks.STONE)
        var veinsPerChunk = 8
        var amountPerVein = 6
        var minHeight = 0
        var maxHeight = 64
    }
}
