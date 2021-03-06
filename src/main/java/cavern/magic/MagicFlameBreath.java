package cavern.magic;

import java.util.Random;

import cavern.api.ISummonMob;
import cavern.core.CaveAchievements;
import cavern.util.CaveUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicFlameBreath implements IMagic
{
	private static final Random RANDOM = new Random();

	private final int magicLevel;
	private final long magicSpellTime;
	private final double magicRange;

	public MagicFlameBreath(int level, long time, double range)
	{
		this.magicLevel = level;
		this.magicSpellTime = time;
		this.magicRange = range;
	}

	@Override
	public int getMagicLevel()
	{
		return magicLevel;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public long getMagicSpellTime(ItemStack stack, EnumHand hand)
	{
		return magicSpellTime;
	}

	@Override
	public int getMagicCost(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return 30 * getMagicLevel();
	}

	public double getMagicRange()
	{
		return magicRange;
	}

	@Override
	public boolean executeMagic(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		int level = getMagicLevel();
		double range = getMagicRange();
		BlockPos blockPos = player.getPosition();
		int count = 0;

		for (BlockPos pos : BlockPos.getAllInBox(blockPos.add(range, range, range), blockPos.add(-range, -range, -range)))
		{
			double dist = Math.sqrt(blockPos.distanceSq(pos));

			if (dist > range || !world.isAirBlock(pos))
			{
				continue;
			}

			BlockPos down = pos.down();

			if (world.isAirBlock(down) || world.isRainingAt(down))
			{
				continue;
			}

			BlockPos up = pos.up();

			if (world.isAirBlock(up) && RANDOM.nextInt(Math.max(5 - level, 2)) == 0)
			{
				world.setBlockState(pos, Blocks.FIRE.getDefaultState());

				++count;
			}
		}

		if (count > 0)
		{
			for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, player.getEntityBoundingBox().grow(range * 0.5F)))
			{
				if (entity instanceof EntityPlayer)
				{
					continue;
				}

				if (entity instanceof ISummonMob)
				{
					continue;
				}

				if (entity instanceof IEntityOwnable && ((IEntityOwnable)entity).getOwner() != null)
				{
					continue;
				}

				if (entity.isEntityAlive() && !entity.isImmuneToFire())
				{
					if (entity instanceof IMob)
					{
						entity.setFire(5 * level);
					}

					entity.attackEntityFrom(DamageSource.MAGIC, 1.0F * level);
				}
			}
		}

		if (count > 0)
		{
			if (!player.isPotionActive(MobEffects.FIRE_RESISTANCE))
			{
				player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 10 * level * 20, 0, false, false));
			}

//			CaveUtils.grantAdvancement(player, "magic_flame_breath");
			player.addStat(CaveAchievements.MAGIC_FLAME_BREATH);

			return true;
		}

		return false;
	}
}