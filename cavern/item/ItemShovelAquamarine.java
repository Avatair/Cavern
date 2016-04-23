package cavern.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ItemShovelAquamarine extends ItemShovelCave implements IAquaTool
{
	public ItemShovelAquamarine()
	{
		super(CaveItems.AQUAMARINE, "shovelAquamarine");
	}

	@Override
	public float getAquaBreakSpeed(ItemStack itemstack, EntityPlayer player, BlockPos pos, IBlockState state, float originalSpeed)
	{
		return originalSpeed * 10.0F;
	}
}