package cavern.api.event;

import cavern.stats.MinerStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class MinerStatsEvent extends PlayerEvent
{
	private final MinerStats stats;

	public MinerStatsEvent(EntityPlayer player, MinerStats stats)
	{
		super(player);
		this.stats = stats;
	}

	public MinerStats getStats()
	{
		return stats;
	}

	@Cancelable
	public static class AddPoint extends MinerStatsEvent
	{
		private final int originalPoint;

		private int newPoint;

		public AddPoint(EntityPlayer player, MinerStats stats, int point)
		{
			super(player, stats);
			this.originalPoint = point;
			this.newPoint = point;
		}

		public int getPoint()
		{
			return originalPoint;
		}

		public int getNewPoint()
		{
			return newPoint;
		}

		public void setNewPoint(int point)
		{
			newPoint = point;
		}
	}

	public static class PromoteRank extends MinerStatsEvent
	{
		public PromoteRank(EntityPlayer player, MinerStats stats)
		{
			super(player, stats);
		}
	}
}