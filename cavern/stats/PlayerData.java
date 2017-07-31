package cavern.stats;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.collect.Maps;

import cavern.api.IPlayerData;
import cavern.capability.CaveCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.util.Constants.NBT;

public class PlayerData implements IPlayerData
{
	private final Map<DimensionType, Long> lastTeleportTimes = Maps.newHashMap();

	private long lastSleepTime;
	private NBTTagList inventoryCache;

	@Override
	public long getLastTeleportTime(DimensionType type)
	{
		return lastTeleportTimes.getOrDefault(type, 0L).longValue();
	}

	@Override
	public void setLastTeleportTime(DimensionType type, long time)
	{
		lastTeleportTimes.put(type, time);
	}

	@Override
	public long getLastSleepTime()
	{
		return lastSleepTime;
	}

	@Override
	public void setLastSleepTime(long time)
	{
		lastSleepTime = time;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList tagList = new NBTTagList();

		for (Entry<DimensionType, Long> entry : lastTeleportTimes.entrySet())
		{
			NBTTagCompound tag = new NBTTagCompound();

			tag.setInteger("Dim", entry.getKey().getId());
			tag.setLong("Time", entry.getValue().longValue());
		}

		nbt.setTag("LastTeleportTimes", tagList);

		nbt.setLong("LastSleepTime", lastSleepTime);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList tagList = nbt.getTagList("LastTeleportTime", NBT.TAG_COMPOUND);

		if (tagList != null && tagList.tagCount() > 0)
		{
			for (int i = 0; i < tagList.tagCount(); ++i)
			{
				NBTTagCompound tag = tagList.getCompoundTagAt(i);
				DimensionType type = null;

				try
				{
					type = DimensionType.getById(tag.getInteger("Dim"));
				}
				catch (IllegalArgumentException e)
				{
					continue;
				}

				if (type != null)
				{
					lastTeleportTimes.put(type, tag.getLong("Time"));
				}
			}
		}

		lastSleepTime = nbt.getLong("LastSleepTime");
	}

	@Override
	public NBTTagList getInventoryCache()
	{
		return inventoryCache;
	}

	@Override
	public void setInventoryCache(NBTTagList list)
	{
		inventoryCache = list;
	}

	public static IPlayerData get(EntityPlayer player)
	{
		return ObjectUtils.defaultIfNull(CaveCapabilities.getCapability(player, CaveCapabilities.PLAYER_DATA), new PlayerData());
	}
}