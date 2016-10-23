package cavern.client.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLoadCaveTerrain extends GuiDownloadCaveTerrain
{
	public GuiLoadCaveTerrain(NetHandlerPlayClient handler)
	{
		super(handler);
	}

	@Override
	public String getInfoText()
	{
		return I18n.format("cavern.terrain.load");
	}

	@Override
	public void updateScreen()
	{
		if (mc.thePlayer != null && (mc.thePlayer.onGround || mc.thePlayer.capabilities.isFlying || mc.thePlayer.isInWater()))
		{
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}

		super.updateScreen();
	}

	@Override
	protected void keyTyped(char c, int code)
	{
		if (code == Keyboard.KEY_ESCAPE)
		{
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}
	}
}