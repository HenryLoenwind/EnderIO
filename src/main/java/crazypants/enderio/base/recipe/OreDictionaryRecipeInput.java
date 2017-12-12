package crazypants.enderio.base.recipe;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public class OreDictionaryRecipeInput extends RecipeInput {

  public OreDictionaryRecipeInput(@Nonnull ItemStack stack, @Nonnull String oreDict, float multiplier, int slot) {
    super(stack, multiplier, slot);
    input.addOredict(oreDict);
  }

  public OreDictionaryRecipeInput(@Nonnull OreDictionaryRecipeInput copy) {
    super(copy);
  }

  @Override
  public @Nonnull RecipeInput copy() {
    return new OreDictionaryRecipeInput(this);
  }

}
