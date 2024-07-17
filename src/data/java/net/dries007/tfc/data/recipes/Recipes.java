package net.dries007.tfc.data.recipes;

import java.util.Objects;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import net.dries007.tfc.data.Accessors;

public interface Recipes extends Accessors
{
    HolderLookup.Provider lookup();

    default void add(Recipe<?> recipe)
    {
        final ItemStack output = recipe.getResultItem(lookup());
        assert !output.isEmpty() : "Empty item used in name";
        add(BuiltInRegistries.ITEM.getKey(output.getItem()).getPath(), recipe);
    }

    default void add(String name, Recipe<?> recipe)
    {
        add(Objects.requireNonNull(BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType()), "No recipe type").getPath(), name, recipe);
    }

    void add(String prefix, String name, Recipe<?> recipe);
}