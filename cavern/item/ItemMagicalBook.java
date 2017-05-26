package cavern.item;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.gui.ChatFormatting;

import cavern.client.handler.MagicSpellEventHooks;
import cavern.core.Cavern;
import cavern.magic.IMagic;
import cavern.magic.MagicExplosion;
import cavern.magic.MagicFlameBreath;
import cavern.magic.MagicHeal;
import cavern.magic.MagicHolyBless;
import cavern.magic.MagicReturn;
import cavern.magic.MagicStorage;
import cavern.magic.MagicThunderbolt;
import cavern.magic.MagicVenomBlast;
import cavern.magic.MagicWarp;
import cavern.util.Roman;
import cavern.util.WeightedItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMagicalBook extends Item
{
	public static final List<WeightedItem> MAGIC_ITEMS = Lists.newArrayList();

	public ItemMagicalBook()
	{
		super();
		this.setUnlocalizedName("magicalBook");
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return "item.magicalBook." + EnumType.byItemStack(stack).getUnlocalizedName();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		String bookName = super.getItemStackDisplayName(stack);
		String name = Cavern.proxy.translate(Cavern.proxy.translate(getUnlocalizedName()) + ".name").trim();

		if (EnumType.byItemStack(stack).getMaxLevel() > 1)
		{
			bookName += " " + Roman.toRoman(getMagicLevel(stack));
		}

		return name + ": " + bookName;
	}

	public int getMagicLevel(ItemStack stack)
	{
		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt == null)
		{
			return 1;
		}

		return MathHelper.clamp(nbt.getInteger("MagicLevel"), 1, EnumType.byItemStack(stack).getMaxLevel());
	}

	public ItemStack setMagicLevel(ItemStack stack, int level)
	{
		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt == null)
		{
			nbt = new NBTTagCompound();
		}

		nbt.setInteger("MagicLevel", level);
		stack.setTagCompound(nbt);

		return stack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		for (EnumType type : EnumType.values())
		{
			for (int i = 1; i <= type.getMaxLevel(); ++i)
			{
				subItems.add(setMagicLevel(new ItemStack(item, 1, type.getItemDamage()), i));
			}
		}
	}

	@Nullable
	public IMagic getMagic(ItemStack stack)
	{
		EnumType type = EnumType.byItemStack(stack);
		int level = getMagicLevel(stack);

		switch (type)
		{
			case FLAME_BREATH:
				return new MagicFlameBreath(level, type.getMagicSpellTime(level), type.getMagicRange(level));
			case EXPLOSION:
				return new MagicExplosion(level, type.getMagicSpellTime(level), type.getMagicRange(level));
			case THUNDERBOLT:
				return new MagicThunderbolt(level, type.getMagicSpellTime(level), type.getMagicRange(level));
			case VENOM_BLAST:
				return new MagicVenomBlast(level, type.getMagicSpellTime(level), type.getMagicRange(level));
			case RETURN:
				return new MagicReturn(level, type.getMagicSpellTime(level), type.getMagicRange(level));
			case HEAL:
				return new MagicHeal(level, type.getMagicSpellTime(level), type.getMagicRange(level));
			case HOLY_BLESS:
				return new MagicHolyBless(level, type.getMagicSpellTime(level), type.getMagicRange(level));
			case STORAGE:
				return new MagicStorage(level, type.getMagicSpellTime(level), stack);
			case WARP:
				return new MagicWarp(level, type.getMagicSpellTime(level), stack);
			default:
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		EnumType type = EnumType.byItemStack(stack);

		switch (type)
		{
			case STORAGE:
				IInventory inventory = InventoryEquipment.get(stack).getInventory();

				if (inventory != null)
				{
					Set<String> stocks = getInventoryStocks(inventory, 10);

					if (!stocks.isEmpty())
					{
						tooltip.add(ChatFormatting.BOLD + Cavern.proxy.translate("item.magicalBook.storage.stock") + ChatFormatting.RESET);

						for (String stock : stocks)
						{
							if (!Strings.isNullOrEmpty(stock))
							{
								tooltip.add(" " + stock);
							}
						}
					}
				}

				break;
			case WARP:
				Pair<BlockPos, Integer> warpPoint = MagicWarp.getWarpPoint(stack);

				if (warpPoint != null)
				{
					BlockPos pos = warpPoint.getLeft();
					int dim = warpPoint.getRight();
					String prefix = Cavern.proxy.translate("item.magicalBook.warp.point") + ": ";

					tooltip.add(prefix + String.format("%d, %d, %d (%d)", pos.getX(), pos.getY(), pos.getZ(), dim));
				}

				break;
			default:
		}
	}

	private Set<String> getInventoryStocks(IInventory inventory, int limit)
	{
		Set<String> set = Sets.newTreeSet();

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack itemstack = inventory.getStackInSlot(i);

			if (!itemstack.isEmpty())
			{
				set.add(itemstack.getDisplayName());

				if (set.size() >= limit)
				{
					break;
				}
			}
		}

		return set;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return MagicSpellEventHooks.spellingProgress > 0.0D &&
			MagicSpellEventHooks.spellingBook != null && ItemStack.areItemStacksEqual(stack, MagicSpellEventHooks.spellingBook);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return 1.0D - MathHelper.clamp(MagicSpellEventHooks.spellingProgress, 0.0D, 1.0D);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		return 0x00A2D0;
	}

	public enum EnumType
	{
		FLAME_BREATH(0, "flameBreath", 4, 3500L, 5.0D),
		EXPLOSION(1, "explosion", 4, 3000L, 0.0D),
		THUNDERBOLT(2, "thunderbolt", 4, 3500L, 5.0D),
		VENOM_BLAST(3, "venomBlast", 4, 3500L, 5.0D),
		RETURN(4, "return", 2, 20000L, 3.0D),
		HEAL(5, "heal", 3, 5000L, 5.0D),
		HOLY_BLESS(6, "holyBless", 4, 10000L, 10.0D),
		STORAGE(7, "storage", 4, 1000L, 0.0D),
		WARP(8, "warp", 4, 20000L, 0.0D);

		private static final EnumType[] DAMAGE_LOOKUP = new EnumType[values().length];

		private final int itemDamage;
		private final String unlocalizedName;
		private final int maxLevel;
		private final long magicSpellTime;
		private final double magicRange;

		private EnumType(int damage, String name, int level, long time, double range)
		{
			this.itemDamage = damage;
			this.unlocalizedName = name;
			this.maxLevel = level;
			this.magicSpellTime = time;
			this.magicRange = range;
		}

		public int getItemDamage()
		{
			return itemDamage;
		}

		public String getUnlocalizedName()
		{
			return unlocalizedName;
		}

		public int getMaxLevel()
		{
			return maxLevel;
		}

		public long getMagicSpellTime()
		{
			return magicSpellTime;
		}

		public long getMagicSpellTime(int level)
		{
			return level <= 1 ? magicSpellTime : magicSpellTime * ((level - 1) * 2);
		}

		public double getMagicRange()
		{
			return magicRange;
		}

		public double getMagicRange(int level)
		{
			return level <= 1 ? magicRange : magicRange * (1.0D + 0.5D * level);
		}

		public ItemStack getItemStack()
		{
			return getItemStack(1);
		}

		public ItemStack getItemStack(int amount)
		{
			return new ItemStack(CaveItems.MAGICAL_BOOK, amount, getItemDamage());
		}

		public static EnumType byDamage(int damage)
		{
			if (damage < 0 || damage >= DAMAGE_LOOKUP.length)
			{
				damage = 0;
			}

			return DAMAGE_LOOKUP[damage];
		}

		public static EnumType byItemStack(ItemStack itemstack)
		{
			return byDamage(itemstack == null ? 0 : itemstack.getItemDamage());
		}

		static
		{
			for (EnumType type : values())
			{
				DAMAGE_LOOKUP[type.getItemDamage()] = type;
			}
		}
	}

	public static boolean heldMagicItem(EntityPlayer player)
	{
		if (player == null)
		{
			return false;
		}

		for (ItemStack held : player.getHeldEquipment())
		{
			if (!held.isEmpty())
			{
				return held.getItem() instanceof ItemMagicalBook || held.getItem() instanceof ItemElixir;
			}
		}

		return false;
	}

	public static void registerMagicItems()
	{
		for (EnumType type : EnumType.values())
		{
			int max = type.getMaxLevel();

			for (int i = 1; i <= max; ++i)
			{
				ItemStack stack = CaveItems.MAGICAL_BOOK.setMagicLevel(new ItemStack(CaveItems.MAGICAL_BOOK, 1, type.getItemDamage()), i);

				MAGIC_ITEMS.add(new WeightedItem(stack, (max + 1 - i) * 10 - i));
			}
		}

		MAGIC_ITEMS.add(new WeightedItem(ItemElixir.EnumType.ELIXIR_NORMAL.getItemStack(), 30));
		MAGIC_ITEMS.add(new WeightedItem(ItemElixir.EnumType.ELIXIR_MEDIUM.getItemStack(), 10));
		MAGIC_ITEMS.add(new WeightedItem(ItemElixir.EnumType.ELIXIR_HIGH.getItemStack(), 3));
	}
}