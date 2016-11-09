package cavern.plugin;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import cavern.stats.MinerRank;
import cavern.stats.MinerStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import shift.mceconomy3.api.shop.IProduct;
import shift.mceconomy3.api.shop.IShop;

public class MCEPortalShop implements IShop
{
	private final ArrayList<IProduct> products = Lists.newArrayList();

	@Override
	public String getShopName(World world, EntityPlayer player)
	{
		return "shop.cavern.portal.name";
	}

	@Override
	public void addProduct(IProduct product)
	{
		if (product != null)
		{
			products.add(product);
		}
	}

	@Override
	public ArrayList<IProduct> getProductList(World world, EntityPlayer player)
	{
		return products;
	}

	public void addProduct(ItemStack item, int cost)
	{
		addProduct(item, cost, null);
	}

	public void addProduct(ItemStack item, int cost, @Nullable MinerRank rank)
	{
		addProduct(new ShopProduct(item, cost, rank));
	}

	public static class ShopProduct implements IProduct
	{
		private final ItemStack productItem;
		private final int cost;
		private final MinerRank minerRank;

		public ShopProduct(ItemStack item, int cost, @Nullable MinerRank rank)
		{
			this.productItem = item;
			this.cost = cost;
			this.minerRank = rank;
		}

		@Override
		public ItemStack getItem(IShop shop, World world, EntityPlayer player)
		{
			return productItem.copy();
		}

		@Override
		public int getCost(IShop shop, World world, EntityPlayer player)
		{
			if (world != null && player != null)
			{
				BlockPos pos = player.getPosition();
				DifficultyInstance difficulty = world.getDifficultyForLocation(pos);

				return (int)(cost * MathHelper.clamp_float(difficulty.getAdditionalDifficulty(), 1.0F, 3.0F));
			}

			return cost;
		}

		@Override
		public boolean canBuy(IShop shop, World world, EntityPlayer player)
		{
			return minerRank == null || player != null && MinerStats.get(player).getRank() >= minerRank.getRank();
		}
	}
}