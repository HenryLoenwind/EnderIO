package crazypants.enderio.base.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class Recipe implements IRecipe {

  private final @Nonnull RecipeInput[] inputs;
  private final @Nonnull RecipeOutput[] outputs;
  private final int energyRequired;
  private final @Nonnull RecipeBonusType bonusType;

  public Recipe(RecipeOutput output, int energyRequired, @Nonnull RecipeBonusType bonusType, @Nonnull RecipeInput... input) {
    this(input, new RecipeOutput[] { output }, energyRequired, bonusType);
  }

  public Recipe(RecipeInput input, int energyRequired, @Nonnull RecipeBonusType bonusType, @Nonnull RecipeOutput... output) {
    this(new RecipeInput[] { input }, output, energyRequired, bonusType);
  }

  public Recipe(@Nonnull RecipeInput[] input, @Nonnull RecipeOutput[] output, int energyRequired, @Nonnull RecipeBonusType bonusType) {
    this.inputs = input;
    this.outputs = output;
    this.energyRequired = energyRequired;
    this.bonusType = bonusType;
  }

  @Override
  public boolean isInputForRecipe(MachineRecipeInput... machineInputs) {
    if(machineInputs == null || machineInputs.length == 0) {
      return false;
    }

    // fail fast check
    for (MachineRecipeInput realInput : machineInputs) {
      if (realInput != null && (realInput.fluid != null || Prep.isValid(realInput.item)) && !isAnyInput(realInput)) {
        return false;
      }
    }

    List<RecipeInput> requiredInputs = new ArrayList<RecipeInput>();
    for(RecipeInput input : inputs) { 
      if (input.getFluidInput() != null || Prep.isValid(input.getInput())) {
        requiredInputs.add(input.copy());
      }
    }
    
    for (MachineRecipeInput input : machineInputs) {
      if (input != null && (input.fluid != null || Prep.isValid(input.item))) {
        RecipeInput required = null;        
        for(int i=0;i<requiredInputs.size() && required == null;i++) {
          RecipeInput tst = requiredInputs.get(i);
          if (tst.isInput(input.item) || tst.isInput(input.fluid)) {
             required = tst;
          }
        }        
        if(required == null) {
          return false;
        }
        //reduce the required input quantity by the available amount
        if(input.isFluid()) {
          required.shrinkAmount(input.fluid.amount);
        } else {
          required.shrinkAmount(input.item.getCount());
        }        
      }
    }
    
    for(RecipeInput required : requiredInputs) {
      if (required.hasAmount()) {
        return false;
      }
    }
    return true;
  }

  private boolean isAnyInput(@Nonnull MachineRecipeInput realInput) {
    for (RecipeInput recipeInput : inputs) {
      if (recipeInput != null && ((recipeInput.isInput(realInput.item)) || recipeInput.isInput(realInput.fluid))) {
        return true;
      }
    }
    return false;
  }

  protected int getMinNumInputs() {
    return inputs.length;
  }

  @Override
  public boolean isValidInput(int slot, @Nonnull ItemStack item) {
    return getInputForStack(item) != null;
  }

  @Override
  public boolean isValidInput(@Nonnull FluidStack fluid) {
    return getInputForStack(fluid) != null;
  }

  private RecipeInput getInputForStack(@Nonnull FluidStack input) {
    for (RecipeInput ri : inputs) {
      if(ri.isInput(input)) {
        return ri;
      }
    }
    return null;
  }

  private RecipeInput getInputForStack(@Nonnull ItemStack input) {
    for (RecipeInput ri : inputs) {
      if(ri.isInput(input)) {
        return ri;
      }
    }
    return null;
  }

  @Override
  public @Nonnull NNList<ItemStack> getInputStacks() {
    NNList<ItemStack> res = new NNList<ItemStack>();
    for (int i = 0; i < inputs.length; i++) {
      RecipeInput in = inputs[i];
      if (in != null && Prep.isValid(in.getInput())) {
        res.add(in.getInput());
      }
    }
    return res;
  }

  @Override
  public @Nonnull List<List<ItemStack>> getInputStackAlternatives() {
    NNList<List<ItemStack>> res = new NNList<List<ItemStack>>();
    for (int i = 0; i < inputs.length; i++) {
      RecipeInput in = inputs[i];
      if (in != null) {
        ItemStack[] equivelentInputs = in.getEquivelentInputs();
        if (equivelentInputs != null && equivelentInputs.length != 0) {
          res.add(new NNList<>(equivelentInputs));
        } else {
          ItemStack input = in.getInput();
          if (Prep.isValid(input)) {
            res.add(new NNList<>(input));
          } else {
            res.add(NNList.<ItemStack> emptyList());
          }
        }
      } else {
        res.add(NNList.<ItemStack> emptyList());
      }
    }
    return res;
  }

  @Override
  public NNList<FluidStack> getInputFluidStacks() {
    NNList<FluidStack> res = new NNList<FluidStack>();
    for (int i = 0; i < inputs.length; i++) {
      RecipeInput in = inputs[i];
      if(in != null && in.getFluidInput() != null) {
        res.add(in.getFluidInput());
      }
    }
    return res;
  }

  @Override
  public @Nonnull RecipeInput[] getInputs() {
    return inputs;
  }

  @Override
  public @Nonnull RecipeOutput[] getOutputs() {
    return outputs;
  }

  @Override
  public @Nonnull RecipeBonusType getBonusType() {
    return bonusType;
  }

  public boolean hasOuput(@Nonnull ItemStack result) {
    if (Prep.isInvalid(result)) {
      return false;
    }
    for(RecipeOutput output : outputs) {
      ItemStack os = output.getOutput();
      if (os.isItemEqual(result)) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public int getEnergyRequired() {
    return energyRequired;
  }

  @Override
  public boolean isValid() {
    if (energyRequired <= 0) {
      return false;
    }
    for(RecipeInput input : inputs) {
      if(!input.isValid()) {
        return false;
      }
    }
    for(RecipeOutput output : outputs) {
      if(!output.isValid()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return "Recipe [input=" + Arrays.toString(inputs) + ", output=" + Arrays.toString(outputs) + ", energyRequired=" + energyRequired + "]";
  }

  @Override
  public boolean isSynthetic() {
    return false;
  }
  

}
