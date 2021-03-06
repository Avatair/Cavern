package cavern.magic;

import javax.annotation.Nullable;

import cavern.core.CaveAchievements;
import cavern.core.CaveSounds;
import cavern.core.Cavern;
import cavern.entity.EntitySummonCavenicSkeleton;
import cavern.entity.EntitySummonCavenicZombie;
import cavern.entity.EntitySummonSkeleton;
import cavern.entity.EntitySummonZombie;
import cavern.util.CaveUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicSummon implements IMagic
{
	private final int magicLevel;
	private final long magicSpellTime;

	public MagicSummon(int level, long time)
	{
		this.magicLevel = level;
		this.magicSpellTime = time;
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
		return 50 * getMagicLevel();
	}

	@Override
	public int getMagicPoint(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return 2 * getMagicLevel();
	}

	@Override
	public SoundEvent getMagicSound()
	{
		return CaveSounds.MAGIC_SUMMON;
	}

	@Override
	public boolean executeMagic(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		if (getMagicLevel() >= 5)
		{
			int count = getMagicLevel() - 2;
			boolean flag = false;

			for (int i = 0; i < count; ++i)
			{
				if (executeSummonMagic(player, world, stack, hand))
				{
					flag = true;
				}
			}

			return flag;
		}

		return executeSummonMagic(player, world, stack, hand);
	}

	public boolean executeSummonMagic(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		BlockPos summonPos;
		Vec3d hitVec = ForgeHooks.rayTraceEyeHitVec(player, Cavern.proxy.getBlockReachDistance(player));

		if (hitVec != null)
		{
			summonPos = getSummonPos(player, new BlockPos(hitVec.x, hitVec.y + 1, hitVec.z));

			if (summonPos != null)
			{
				summon(player, summonPos);

				return true;
			}
		}

		BlockPos origin = player.getPosition();
		EnumFacing frontFace = player.getHorizontalFacing();

		for (int i = 0; i < 3; ++i)
		{
			summonPos = getSummonPos(player, origin.offset(frontFace, i));

			if (summonPos != null)
			{
				summon(player, summonPos);

				return true;
			}
		}

		for (BlockPos pos : BlockPos.getAllInBoxMutable(origin.add(2, 0, 2), origin.add(-2, 0, -2)))
		{
			summonPos = getSummonPos(player, pos);

			if (summonPos != null)
			{
				summon(player, summonPos);

				return true;
			}
		}

		return false;
	}

	@Nullable
	protected BlockPos getSummonPos(EntityPlayer player, BlockPos checkPos)
	{
		World world = player.world;
		BlockPos pos = checkPos;
		int diff = 0;

		if (world.isAirBlock(pos))
		{
			while (diff < 5 && world.isAirBlock(pos))
			{
				pos = pos.down();

				++diff;
			}

			pos = pos.up();
		}
		else while (diff < 5 && !world.isAirBlock(pos))
		{
			pos = pos.up();

			++diff;
		}

		if (!world.isAirBlock(pos) || !world.isAirBlock(pos.up()) || world.isAirBlock(pos.down()))
		{
			return null;
		}

		if (!world.checkNoEntityCollision(new AxisAlignedBB(pos)))
		{
			return null;
		}

		if (world.rayTraceBlocks(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), new Vec3d(pos), false, true, false) != null)
		{
			return null;
		}

		BlockPos blockpos = player.getPosition();
		double prev = pos.distanceSq(blockpos);

		if (prev - pos.distanceSq(blockpos.offset(player.getHorizontalFacing())) < 0.0D)
		{
			return null;
		}

		return pos;
	}

	protected EntityLivingBase getSummonMob(World world, EntityPlayer player, int level)
	{
		switch (level)
		{
			case 1:
				return new EntitySummonZombie(world, player);
			case 2:
				return new EntitySummonSkeleton(world, player);
			case 3:
				return new EntitySummonCavenicZombie(world, player);
			case 4:
				return new EntitySummonCavenicSkeleton(world, player);
			default:
				return getSummonMob(world, player, player.getRNG().nextInt(4) + 1);
		}
	}

	public void summon(EntityPlayer player, BlockPos pos)
	{
		World world = player.world;
		EntityLivingBase entity = getSummonMob(world, player, getMagicLevel());

		entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);

		if (entity instanceof EntityLiving)
		{
			((EntityLiving)entity).onInitialSpawn(world.getDifficultyForLocation(pos), null);
		}

		if (world.spawnEntity(entity))
		{
//			CaveUtils.grantAdvancement(player, "magic_summon");
			player.addStat(CaveAchievements.SUMMONER);
		}
	}
}