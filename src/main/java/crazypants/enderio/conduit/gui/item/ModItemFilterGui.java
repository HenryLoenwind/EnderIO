package crazypants.enderio.conduit.gui.item;

import java.awt.Color;
import java.awt.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemConduit;
import crazypants.enderio.conduit.item.filter.ModItemFilter;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.network.PacketHandler;

public class ModItemFilterGui implements IItemFilterGui {

  private static final int MOD_NAME_COLOR = ColorUtil.getRGB(Color.white);

  private final IItemConduit itemConduit;
  private final GuiExternalConnection gui;
  
  boolean isInput;

  private final ModItemFilter filter;
  
  private final Rectangle[] inputBounds;
  
  private final IconButton[] deleteButs;
  
  private final int inputOffsetX;
  private final int tfWidth;
  private final int tfTextureX;
  private final int tfTextureY;
  
  public ModItemFilterGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
    this.gui = gui;
    this.itemConduit = itemConduit;
    this.isInput = isInput;

    
    if(isInput) {
      filter = (ModItemFilter) itemConduit.getInputFilter(gui.getDir());
      inputOffsetX = 50;
      tfWidth = 96;
      tfTextureX = 120;
      tfTextureY = 214;
    } else {
      filter = (ModItemFilter) itemConduit.getOutputFilter(gui.getDir());
      inputOffsetX = 32;
      tfWidth = 114;
      tfTextureX = 120;
      tfTextureY = 238;
    }
    
    
    inputBounds = new Rectangle[] {
        new Rectangle(inputOffsetX,47,16,16),
        new Rectangle(inputOffsetX,68,16,16),
        new Rectangle(inputOffsetX,89,16,16)
      };
    
    deleteButs = new IconButton[inputBounds.length];    
    for(int i=0; i < deleteButs.length; i++) {
      Rectangle r = inputBounds[i];
      IconButton but = new IconButton(gui, GuiExternalConnection.nextButtonId(),  r.x + 19, r.y, IconEIO.MINUS);
      deleteButs[i] = but;
    }
    
  }

  @Override
  public void deactivate() {   
    for(IconButton but : deleteButs) {
      but.detach();
    }
  }

  @Override
  public void updateButtons() {
    for(IconButton but : deleteButs) {
      but.onGuiInit();
    }
  }

  @Override
  public void actionPerformed(GuiButton guiButton) {
    for(int i=0; i < deleteButs.length; i++) {
      IconButton but = deleteButs[i];
      if(but.id == guiButton.id) {
        setMod(i, null);
        return;
      }
    }
  }

  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {    
    GL11.glColor3f(1, 1, 1);
    gui.bindGuiTexture();
    for(Rectangle r : inputBounds) {
      //slot
      gui.drawTexturedModalRect(gui.getGuiLeft() + r.x - 1, gui.getGuiTop() + r.y - 1, 24, 214, 18, 18);
      //text box
      gui.drawTexturedModalRect(gui.getGuiLeft() + r.x + 38, gui.getGuiTop() + r.y - 1, tfTextureX, tfTextureY, tfWidth, 18);
    }    
    
    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    for(int i=0;i<inputBounds.length;i++) {
      String mod = filter.getModAt(i);
      if(mod != null) {
        Rectangle r = inputBounds[i];
        mod = fr.trimStringToWidth(mod, tfWidth - 6);
        fr.drawStringWithShadow(mod, gui.getGuiLeft() + r.x + 41, gui.getGuiTop() + r.y + 4, MOD_NAME_COLOR);
      }
    }
  }
  
  @Override
  public void mouseClicked(int x, int y, int par3) {
    ItemStack st = Minecraft.getMinecraft().thePlayer.inventory.getItemStack();
    if(st == null) {
      return;
    }
    
    for(int i=0;i<inputBounds.length;i++) {
      Rectangle bound = inputBounds[i];
      if(bound.contains(x,y)) {
        setMod(i, st);
      }
    }    
  }

  private void setMod(int i, ItemStack st) {
    String mod = filter.setMod(i, st);    
    PacketHandler.INSTANCE.sendToServer(new PacketModItemFilter((ItemConduit) itemConduit, gui.getDir(), isInput, i, mod));
    
  }

}
