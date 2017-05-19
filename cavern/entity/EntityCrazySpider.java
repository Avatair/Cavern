package cavern.entity;

import cavern.api.CavernAPI;
import cavern.client.particle.ParticleCrazyMob;
import cavern.core.CaveAchievements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCrazySpider extends EntityCavenicSpider
{
	private final BossInfoServer bossInfo = new BossInfoServer(getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);

	public EntityCrazySpider(World world)
	{
		super(world);
		this.experienceValue = 50;
	}

	@Override
	protected void applyMobAttributes()
	{
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1500.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.60000001192092896D);
	}

	@Override
	protected int getBlindnessAttackPower()
	{
		switch (world.getDifficulty())
		{
			case NORMAL:
				return 10;
			case HARD:
				return 20;
			default:
				return 5;
		}
	}

	@Override
	protected int getPoisonAttackPower()
	{
		switch (world.getDifficulty())
		{
			case NORMAL:
				return 5;
			case HARD:
				return 8;
			default:
				return 3;
		}
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return 1;
	}

	@Override
	public int getHuntingPoint()
	{
		return 100;
	}

	@Override
	public boolean isNonBoss()
	{
		return false;
	}

	@Override
	protected boolean canBeRidden(Entity entity)
	{
		return false;
	}

	@Override
	public boolean getCanSpawnHere()
	{
		return CavernAPI.dimension.isEntityInCavenia(this) && super.getCanSpawnHere();
	}

	@Override
	protected Achievement getKillAchievement()
	{
		return CaveAchievements.CRAZY_SPIDER;
	}

	@Override
	public void onStruckByLightning(EntityLightningBolt lightningBolt) {}

	@SideOnly(Side.CLIENT)
	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (world.isRemote)
		{
			for (int i = 0; i < 3; ++i)
			{
				int var1 = rand.nextInt(2) * 2 - 1;
				int var2 = rand.nextInt(2) * 2 - 1;
				double ptX = posX + 0.25D * var1;
				double ptY = posY + 0.65D + rand.nextFloat();
				double ptZ = posZ + 0.25D * var2;
				double motionX = rand.nextFloat() * 1.0F * var1;
				double motionY = (rand.nextFloat() - 0.25D) * 0.125D;
				double motionZ = rand.nextFloat() * 1.0F * var2;
				ParticleCrazyMob particle = new ParticleCrazyMob(world, ptX, ptY, ptZ, motionX, motionY, motionZ);

				FMLClientHandler.instance().getClient().effectRenderer.addEffect(particle);
			}
		}
	}

	@Override
	protected void updateAITasks()
	{
		super.updateAITasks();

		if (!world.isRemote)
		{
			boolean canSee = false;

			for (EntityPlayerMP player : bossInfo.getPlayers())
			{
				if (canEntityBeSeen(player))
				{
					canSee = true;

					break;
				}
			}

			bossInfo.setVisible(canSee);
		}

		bossInfo.setPercent(getHealth() / getMaxHealth());
	}

	@Override
	public void addTrackingPlayer(EntityPlayerMP player)
	{
		super.addTrackingPlayer(player);

		bossInfo.addPlayer(player);
	}

	@Override
	public void removeTrackingPlayer(EntityPlayerMP player)
	{
		super.removeTrackingPlayer(player);

		bossInfo.removePlayer(player);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		if (hasCustomName())
		{
			bossInfo.setName(getDisplayName());
		}
	}

	@Override
	public void setCustomNameTag(String name)
	{
		super.setCustomNameTag(name);

		bossInfo.setName(getDisplayName());
	}
}