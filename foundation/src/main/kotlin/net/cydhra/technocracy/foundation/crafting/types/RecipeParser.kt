package net.cydhra.technocracy.foundation.crafting.types

import com.google.gson.JsonObject
import net.minecraftforge.common.crafting.JsonContext

interface RecipeParser<T : IRecipe> {

    fun process(json: JsonObject, context: JsonContext): T
}