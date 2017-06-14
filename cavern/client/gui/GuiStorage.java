package cavern.client.gui;

import cavern.inventory.ContainerStorage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiStorage extends GuiContainer
{
	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

	private final IInventory playerInventory;
	private final int inventoryRows;

	public GuiStorage(IInventory playerInventory, IInventory storageInventory, EntityPlayer player)
	{
		super(new ContainerStorage(playerInventory, storageInventory, player));
		this.playerInventory = playerInventory;
		this.allowUserInput = false;
		this.inventoryRows = storageInventory.getSizeInventory() / 9;
		this.ySize = 114 + inventoryRows * 18;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(new TextComponentTranslation("item.magicalBook.storage.name").getUnformattedText(), 8, 6, 4210752);
		fontRendererObj.drawString(playerInventory.getDisplayName().getUnformattedText(), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
		int i = getGuiLeft();
		int j = getGuiTop();
		drawTexturedModalRect(i, j, 0, 0, xSize, inventoryRows * 18 + 17);
		drawTexturedModalRect(i, j + inventoryRows * 18 + 17, 0, 126, xSize, 96);
	}
}