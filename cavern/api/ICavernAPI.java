package cavern.api;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ICavernAPI
{
	public IMinerStats getMinerStats(EntityPlayer player);

	public Set<IMineBonus> getMineBonus();

	public void addMineBonus(IMineBonus bonus);

	public void addRandomiteItem(ItemStack item, int weight);
}