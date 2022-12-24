package umpaz.brewinandchewin.common.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class FluidItem extends Item {

    //This needs to be a supplier, since fluids might not be ready when loading blocks
    private final Supplier<Fluid> fluid;
    //amount of fluid, this is both the max and the amount that gets stored in the item
    private final int amount;

    public FluidItem(Properties p_41383_, Supplier<Fluid> fluid, int amount) {
        super(p_41383_);
        this.fluid = fluid;
        this.amount = amount;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        //Fluid handler, replace the bottle with the item you want to return when empty.
        FluidHandlerItemStack.SwapEmpty fluidHandlerItemStack = new FluidHandlerItemStack.SwapEmpty(stack, new ItemStack(Items.GLASS_BOTTLE), this.amount) {
            //Only drain for the full amount. If partially empty is allowed, remove this code.
            @Override
            public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
                FluidStack drain = super.drain(maxDrain, action);
                return drain.getAmount() == amount? drain : FluidStack.EMPTY;
            }
        };
        //fill with fluid
        fluidHandlerItemStack.fill(new FluidStack(fluid.get(), this.amount), IFluidHandler.FluidAction.EXECUTE);
        return fluidHandlerItemStack;
    }
}
