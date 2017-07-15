package cavern.item;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.gui.ChatFormatting;

import cavern.client.handler.MagicSpellEventHooks;
import cavern.core.Cavern;
import cavern.magic.IMagic;
import cavern.magic.MagicCompositing;
import cavern.magic.MagicExplosion;
import cavern.magic.MagicFlameBreath;
import cavern.magic.MagicHeal;
import cavern.magic.MagicHolyBless;
import cavern.magic.MagicReturn;
import cavern.magic.MagicStorage;
import cavern.magic.MagicSummon;
import cavern.magic.MagicThunderbolt;
import cavern.magic.MagicTorch;
import cavern.magic.MagicUnknown;
import cavern.magic.MagicVenomBlast;
import cavern.magic.MagicWarp;
import cavern.util.Roman;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMagicalBook extends Item
{
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
		EnumType type = EnumType.byItemStack(stack);

		if (type == EnumType.UNKNOWN)
		{
			bookName = "???";
		}
		else if (type.getMaxLevel() > 1)
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

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		if (!isInCreativeTab(tab))
		{
			return;
		}

		for (EnumType type : EnumType.VALUES)
		{
			for (int i = 1; i <= type.getMaxLevel(); ++i)
			{
				subItems.add(type.getItemStack(i));
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
			case UNKNOWN:
				return new MagicUnknown(level, type.getMagicSpellTime(level), stack);
			case TORCH:
				return new MagicTorch(level, type.getMagicSpellTime(level), type.getMagicRange(level));
			case SUMMON:
				return new MagicSummon(level, type.getMagicSpellTime(level <= 2 ? 1 : 2));
			case COMPOSITING:
				return new MagicCompositing(level, type.getMagicSpellTime(level), stack);
			default:
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag)
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
				Pair<BlockPos, DimensionType> warpPoint = MagicWarp.getWarpPoint(stack);

				if (warpPoint != null)
				{
					BlockPos pos = warpPoint.getLeft();
					DimensionType dim = warpPoint.getRight();
					String prefix = Cavern.proxy.translate("item.magicalBook.warp.point") + ": ";

					tooltip.add(prefix + String.format("%d, %d, %d (%s)", pos.getX(), pos.getY(), pos.getZ(), dim.getName()));
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
			ItemStack stack = inventory.getStackInSlot(i);

			if (!stack.isEmpty())
			{
				set.add(stack.getDisplayName());

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
			MagicSpellEventHooks.spellingBook != null && MagicSpellEventHooks.spellingBook == stack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return 1.0D - MathHelper.clamp(MagicSpellEventHooks.spellingProgress, 0.0D, 1.0D);
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		return 0x00A2D0;
	}

	public enum EnumType
	{
		FLAME_BREATH(0, "flameBreath", 4, null, 5.0D, 0.235D),
		EXPLOSION(1, "explosion", 4, null, 0.0D, 0.2D),
		THUNDERBOLT(2, "thunderbolt", 4, null, 5.0D, 0.2D),
		VENOM_BLAST(3, "venomBlast", 4, null, 5.0D, 0.2D),
		RETURN(4, "return", 2, new long[] {17000L, 25000L}, 3.0D, 0.1D),
		HEAL(5, "heal", 3, new long[] {5000L, 6500L, 8000L}, 5.0D, 0.2D),
		HOLY_BLESS(6, "holyBless", 4, new long[] {6000L, 8000L, 10000L, 12000L}, 10.0D, 0.2D),
		STORAGE(7, "storage", 4, new long[] {1000L, 1500L, 2500L, 3000L}, 0.0D, 0.2D),
		WARP(8, "warp", 4, new long[] {15000L, 20000L, 22000L, 25000L}, 0.0D, 0.1D),
		UNKNOWN(9, "unknown", 1, new long[] {5000L}, 0.0D, 0.0D),
		TORCH(10, "torch", 3, null, 7.0D, 0.2D),
		SUMMON(11, "summon", 4, null, 0.0D, 0.15D),
		COMPOSITING(12, "compositing", 1, new long[] {15000L}, 0.0D, 0.1D);

		public static final EnumType[] VALUES = new EnumType[values().length];

		private final int meta;
		private final String unlocalizedName;
		private final int maxLevel;
		private final long[] magicSpellTimes;
		private final double magicRange;
		private final double magicRarity;

		private EnumType(int meta, String name, int level, @Nullable long[] times, double range, double rarity)
		{
			this.meta = meta;
			this.unlocalizedName = name;
			this.maxLevel = level;
			this.magicSpellTimes = times == null ? new long[] {3000L, 5000L, 7000L, 10000L} : times;
			this.magicRange = range;
			this.magicRarity = rarity;
		}

		public int getMetadata()
		{
			return meta;
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
			return magicSpellTimes == null ? 10000L : magicSpellTimes[0];
		}

		public long getMagicSpellTime(int level)
		{
			if (level <= 1)
			{
				return getMagicSpellTime();
			}

			if (magicSpellTimes == null)
			{
				return 10000L + 1000L* level;
			}

			int max = magicSpellTimes.length - 1;

			if (level >= max)
			{
				return magicSpellTimes[max];
			}

			return magicSpellTimes[level];
		}

		public double getMagicRange()
		{
			return magicRange;
		}

		public double getMagicRange(int level)
		{
			return level <= 1 ? magicRange : magicRange * (1.0D + 0.45D * level);
		}

		public double getMagicRarity()
		{
			return magicRarity;
		}

		public ItemStack getItemStack()
		{
			return getItemStack(1);
		}

		public ItemStack getItemStack(int level)
		{
			return CaveItems.MAGICAL_BOOK.setMagicLevel(new ItemStack(CaveItems.MAGICAL_BOOK, 1, getMetadata()), level);
		}

		public static EnumType byMetadata(int meta)
		{
			if (meta < 0 || meta >= VALUES.length)
			{
				meta = 0;
			}

			return VALUES[meta];
		}

		public static EnumType byItemStack(ItemStack stack)
		{
			return byMetadata(stack.isEmpty() ? 0 : stack.getMetadata());
		}

		static
		{
			for (EnumType type : values())
			{
				VALUES[type.getMetadata()] = type;
			}
		}
	}
}