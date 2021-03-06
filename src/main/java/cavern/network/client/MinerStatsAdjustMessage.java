package cavern.network.client;

import cavern.api.IMinerStats;
import cavern.stats.MinerStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MinerStatsAdjustMessage implements IPlayerMessage<MinerStatsAdjustMessage, IMessage>
{
	private int point;
	private int rank;
	private int miningAssist;

	public MinerStatsAdjustMessage() {}

	public MinerStatsAdjustMessage(IMinerStats stats)
	{
		this.point = stats.getPoint();
		this.rank = stats.getRank();
		this.miningAssist = stats.getMiningAssist();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		point = buf.readInt();
		rank = buf.readInt();
		miningAssist = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(point);
		buf.writeInt(rank);
		buf.writeInt(miningAssist);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(EntityPlayerSP player)
	{
		IMinerStats stats = MinerStats.get(player, true);

		if (stats != null)
		{
			stats.setPoint(point, false);
			stats.setRank(rank, false);
			stats.setMiningAssist(miningAssist, false);
		}

		return null;
	}
}