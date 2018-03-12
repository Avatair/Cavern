package cavern.magic;

import cavern.network.CaveNetworkRegistry;
import cavern.network.server.MagicFlyingMessage;
import cavern.network.server.MagicResultMessage;
import cavern.stats.MagicianStats;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicFlying implements IMagic
{
	private final int magicLevel;
	private final long magicSpellTime;

	private boolean prevFlying;
	private boolean allowFlying;

	private int flyingTime;

	public MagicFlying(int level, long time)
	{
		this.magicLevel = level;
		this.magicSpellTime = time;
	}

	@Override
	public boolean isClientMagic()
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean onSpellingTick(ItemStack stack, EnumHand hand, long spellingTime, long magicSpellTime, double progress)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc.player;
		World world = mc.world;

		if (!allowFlying && spellingTime >= 3000L)
		{
			if (player.capabilities.isFlying || player.isElytraFlying())
			{
				return false;
			}

			prevFlying = player.capabilities.allowFlying;

			player.capabilities.isFlying = true;
			player.capabilities.allowFlying = true;
			player.onGround = false;
			player.addVelocity(0.0D, 0.5D, 0.0D);

			CaveNetworkRegistry.sendToServer(new MagicFlyingMessage(true, true, getMagicPoint(player, world, stack, hand)));

			allowFlying = true;
		}

		if (allowFlying)
		{
			if (player.isDead || player.onGround || MagicianStats.get(player).getMP() < getMagicCost(player, world, stack, hand))
			{
				allowFlying = false;

				return false;
			}

			if (++flyingTime % (player.isSprinting() ? 10 : 20) == 0)
			{
				CaveNetworkRegistry.sendToServer(new MagicResultMessage(getMagicCost(player, world, stack, hand), 0));
			}
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onStopSpelling(ItemStack stack, EnumHand hand, long spellingTime, double progress)
	{
		if (flyingTime <= 0)
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc.player;
		World world = mc.world;

		player.capabilities.isFlying = false;
		player.capabilities.allowFlying = prevFlying;

		CaveNetworkRegistry.sendToServer(new MagicFlyingMessage(false, prevFlying, getMagicPoint(player, world, stack, hand)));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getSpellingSpeed(int spellingSpeed)
	{
		return allowFlying ? 30 : spellingSpeed;
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
		return Math.max(6 - getMagicLevel(), 1);
	}

	@Override
	public int getMagicPoint(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return flyingTime > 0 ? IMagic.super.getMagicPoint(player, world, stack, hand) : 0;
	}

	@Override
	public boolean executeMagic(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void sendMagicResult(int cost, int point, boolean finish)
	{
		if (!finish)
		{
			CaveNetworkRegistry.sendToServer(new MagicResultMessage(0, point));
		}
	}

	@Override
	public SoundEvent getMagicSound()
	{
		return null;
	}
}