package crazypants.enderio.machine.vat;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.recipe.RecipeOutput;
import crazypants.enderio.machine.recipe.RecipeBonusType;

public class VatRecipe implements IRecipe {

  protected final FluidStack inputFluidStack;
  protected final List<ItemStack> inputStacks;
  protected final FluidStack outputFluidStack;

  protected final RecipeInput[] inputs;
  protected final RecipeOutput[] output;
  protected final int energyRequired;
  private int requiredItems;

  public VatRecipe(IRecipe recipe) {
    List<FluidStack> fluids = recipe.getInputFluidStacks();
    if(fluids != null && !fluids.isEmpty()) {
      inputFluidStack = fluids.get(0).copy();
    } else {
      inputFluidStack = null;
    }

    FluidStack os = null;
    for (RecipeOutput output : recipe.getOutputs()) {
      if(output.isFluid()) {
        os = output.getFluidOutput().copy();
        break;
      }
    }
    outputFluidStack = os;

    this.inputStacks = recipe.getInputStacks();
    inputs = recipe.getInputs();

    output = new RecipeOutput[] { new RecipeOutput(outputFluidStack) };
    energyRequired = recipe.getEnergyRequired();
    
    requiredItems = 1;
    for (RecipeInput ri : inputs) {
      if(!ri.isFluid() && ri.getSlotNumber() == 1) {
        requiredItems = 2;
      }
    }

  }

  @Override
  public boolean isValid() {
    return inputFluidStack != null && inputStacks != null && !inputStacks.isEmpty() && inputStacks.size() > 0 && outputFluidStack != null;
  }

  @Override
  public int getEnergyRequired() {
    return energyRequired;
  }

  @Override
  public RecipeOutput[] getOutputs() {
    return output;
  }

  @Override
  public RecipeInput[] getInputs() {
    return inputs;
  }

  @Override
  public List<ItemStack> getInputStacks() {
    return inputStacks;
  }

  @Override
  public RecipeBonusType getBonusType() {
    return RecipeBonusType.NONE;
  }

  @Override
  public boolean isValidInput(int slot, ItemStack item) {
    if(item == null) {
      return false;
    }
    for (RecipeInput ri : inputs) {
      if(item != null && ri.getSlotNumber() == slot && ri.isInput(item)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isValidInput(FluidStack item) {
    if(item == null) {
      return false;
    }
    for (RecipeInput ri : inputs) {
      if(item.getFluid() != null && item.isFluidEqual(ri.getFluidInput())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isInputForRecipe(MachineRecipeInput... inputs) {
    if(!isValid() || inputs == null || inputs.length < 2) {
      return false;
    }

    int found = 0;
    for (MachineRecipeInput in : inputs) {
      if(in.isFluid()) {
        FluidStack needFluid = getRequiredFluidInput(inputs);
        boolean validFluid = needFluid.isFluidEqual(in.fluid);
        if(validFluid && needFluid.amount <= in.fluid.amount) {
          found++;
        }

      } else {
        if(isValidInput(in.slotNumber, in.item)) { //TODO: Check stack counts
          found++;
        }
      }
    }
    return found == requiredItems + 1;

  }

  @Override
  public List<FluidStack> getInputFluidStacks() {
    return Collections.singletonList(inputFluidStack);
  }

  public float getMultiplierForInput(FluidStack item, int slotNumber) {
    for (RecipeInput input : inputs) {
      if(input.isInput(item) && input.getSlotNumber() == slotNumber) {
        return input.getMulitplier();
      }
    }
    return 1;
  }

  public FluidStack getRequiredFluidInput(MachineRecipeInput... inputs) {
    if(!isValid() || inputs == null || inputs.length < 2) {
      return null;
    }

    MachineRecipeInput fluidInput = null;
    MachineRecipeInput input1 = null;
    MachineRecipeInput input2 = null;

    for (MachineRecipeInput in : inputs) {
      if(in.isFluid()) {
        fluidInput = in;
      } else {
        if(isValidInput(in.slotNumber, in.item)) {
          if (in.slotNumber == 0) {
            input1 = in;
          } else {
            input2 = in;
          }
        }
      }
    }

    if (fluidInput == null || input1 == null || ((requiredItems == 3) && input2 == null)) {
      return null;
    }

    FluidStack inputFluidStack = fluidInput.fluid.copy();
    float inputFluidMul = getMultiplierForInput(input1.item);
    if (input2 != null) {
        inputFluidMul *= getMultiplierForInput(input2.item);
    }

    inputFluidStack.amount = Math.round(inputFluidMul * FluidContainerRegistry.BUCKET_VOLUME);
    return inputFluidStack;
  }

  public FluidStack getFluidOutput(MachineRecipeInput... inputs) {
    FluidStack outFluid = getOutputFluid().copy();
    float outMul = 1;
    for (MachineRecipeInput ri : inputs) {
      outMul *= getMultiplierForInput(ri.item);
      outMul *= getMultiplierForInput(ri.fluid, ri.slotNumber);
    }
    outFluid.amount = Math.round(outMul * FluidContainerRegistry.BUCKET_VOLUME);
    return outFluid;
  }

  public float getMultiplierForInput(ItemStack item) {
    for (RecipeInput input : inputs) {
      if(input.isInput(item)) {
        return input.getMulitplier();
      }
    }
    return 1;
  }

  public int getNumConsumed(ItemStack item) {
    for (RecipeInput input : inputs) {
      if(input.isInput(item)) {
        return input.getInput().stackSize;
      }
    }
    return 1;
  }

  public FluidStack getOutputFluid() {
    return outputFluidStack;
  }

}
