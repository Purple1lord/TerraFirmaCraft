/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class SimpleFluidIngredient implements FluidIngredient
{
    private final Fluid fluid;
    private final int amount;

    private SimpleFluidIngredient(Fluid fluid, int amount)
    {
        this.fluid = fluid;
        this.amount = amount;
    }

    @Override
    public boolean test(FluidStack stack)
    {
        return stack.getFluid() == this.fluid && stack.getAmount() >= this.amount;
    }

    @Override
    public boolean testIgnoreAmount(Fluid fluid)
    {
        return this.fluid == fluid;
    }

    @Override
    public FluidIngredient.Serializer<?> getSerializer()
    {
        return FluidIngredient.FLUID;
    }

    public static class Serializer implements FluidIngredient.Serializer<SimpleFluidIngredient>
    {
        @Override
        public SimpleFluidIngredient fromJson(JsonObject json)
        {
            final int amount = GsonHelper.getAsInt(json, "amount", FluidAttributes.BUCKET_VOLUME);
            final String fluidName = GsonHelper.getAsString(json, "fluid");
            final Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
            if (fluid == null)
            {
                throw new JsonParseException("Not a fluid: " + fluidName);
            }
            return new SimpleFluidIngredient(fluid, amount);
        }

        @Override
        public SimpleFluidIngredient fromNetwork(FriendlyByteBuf buffer)
        {
            final int amount = buffer.readVarInt();
            final Fluid fluid = buffer.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
            return new SimpleFluidIngredient(fluid, amount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SimpleFluidIngredient ingredient)
        {
            buffer.writeVarInt(ingredient.amount);
            buffer.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, ingredient.fluid);
        }
    }
}