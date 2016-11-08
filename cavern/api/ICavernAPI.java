package cavern.api;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICavernAPI
{
	public IMinerStats getMinerStats(EntityPlayer player);

	@SideOnly(Side.CLIENT)
	public int getMineCombo();

	public Set<IMineBonus> getMineBonus();

	public void addMineBonus(IMineBonus bonus);

	public void addRandomiteItem(ItemStack item, int weight);

	public void addRandomiteItem(Item item, int weight);

	public void addRandomiteItem(Item item, int amount, int weight);

	public void addRandomiteItem(Block block, int weight);

	public void addRandomiteItem(Block block, int amount, int weight);

	public void addHibernateItem(ItemStack item, int weight);

	public void addHibernateItem(Item item, int weight);

	public void addHibernateItem(Item item, int amount, int weight);

	public void addHibernateItem(Block block, int weight);

	public void addHibernateItem(Block block, int amount, int weight);

	public void addFissureBreakEvent(IFissureBreakEvent event, int weight);

	public void registerIceEquipment(Item item);

	public boolean isIceEquipment(Item item);

	public boolean isIceEquipment(ItemStack item);

	public IIceEquipment getIceEquipment(ItemStack item);

	public ItemStack getChargedIceItem(Item item, int charge);

	public void addEscapeMissionAchievement(Achievement achievement);

	public List<Achievement> getEscapeMissionAchievements();
}