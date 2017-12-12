package crazypants.enderio.base.recipe;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RecipeInput {

  private final boolean original;

  private final int slot;
  protected final Things input;

  private final FluidStack fluid;

  private final float multiplier;

  private int amount;

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    if (original) {
      throw new RuntimeException("Logic error: Changing original recipe");
    }
    this.amount = amount;
  }

  public void shrinkAmount(int i) {
    if (original) {
      throw new RuntimeException("Logic error: Changing original recipe");
    }
    this.amount -= i;
  }

  public boolean hasAmount() {
    return amount > 0;
  }

  public RecipeInput(@Nonnull ItemStack input) {
    this(input, null, 1, -1);
  }

  public RecipeInput(FluidStack fluid) {
    this(Prep.getEmpty(), fluid, 1f, -1);
  }

  public RecipeInput(FluidStack fluidStack, float mulitplier) {
    this(Prep.getEmpty(), fluidStack, mulitplier, -1);
  }

  public RecipeInput(@Nonnull ItemStack item, float multiplier, int slot) {
    this(item, null, multiplier, slot);
  }

  protected RecipeInput(@Nonnull ItemStack input, FluidStack fluid, float mulitplier, int slot) {
    if (Prep.isValid(input)) {
      this.input = new Things().add(input.copy());
      amount = input.getCount();
      this.fluid = null;
    } else if (fluid != null) {
      this.input = null;
      this.fluid = fluid.copy();
      this.amount = fluid.amount;
    } else {
      throw new RuntimeException("Invalid recipe input (neither item nor fluid)");
    }
    this.multiplier = mulitplier;
    this.slot = slot;
    this.original = true;
  }

  protected RecipeInput(@Nonnull RecipeInput copyFrom) {
    input = copyFrom.input;
    fluid = copyFrom.fluid;
    multiplier = copyFrom.multiplier;
    slot = copyFrom.slot;
    this.amount = copyFrom.amount;
    this.original = false;
  }

  protected RecipeInput(@Nonnull RecipeInput copyFrom, int multiply) {
    input = copyFrom.input;
    fluid = copyFrom.fluid;
    multiplier = copyFrom.multiplier;
    slot = copyFrom.slot;
    this.amount = copyFrom.amount * multiply;
    this.original = true;
  }

  public @Nonnull RecipeInput copy(int multiply) {
    return new RecipeInput(this, multiply);
  }

  public @Nonnull RecipeInput copy() {
    return new RecipeInput(this);
  }

  public boolean isFluid() {
    return fluid != null;
  }

  @Deprecated
  public @Nonnull ItemStack getInput() {
    return input.getItemStacks().get(0).copy();
  }

  @Deprecated
  public FluidStack getFluidInput() {
    return fluid.copy();
  }

  public float getMulitplier() {
    return multiplier;
  }

  public int getSlotNumber() {
    return slot;
  }

  public boolean isInput(@Nonnull ItemStack test) {
    return input != null && input.contains(test);
  }

  public boolean isInput(FluidStack test) {
    if (test == null || fluid == null) {
      return false;
    }
    return test.isFluidEqual(fluid);
  }

  public ItemStack[] getEquivelentInputs() {
    // raw == wildcard metas are present in the list
    return input.getItemStacksRaw().toArray(new ItemStack[0]);
  }

  @Override
  public String toString() {
    if (isValid()) {
      return "RecipeInput [input=" + input + ", fluid=" + fluid + "]";
    }
    return "RecipeInput invalid";
  }

  public boolean isValid() {
    if (isFluid()) {
      return fluid != null && fluid.getFluid() != null;
    } else {
      return input != null && !input.isEmpty();
    }

  }

}
