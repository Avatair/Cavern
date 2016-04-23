package cavern.network;

import cavern.core.Cavern;
import cavern.network.client.CaveMusicMessage;
import cavern.network.client.LastMineMessage;
import cavern.network.client.MinerStatsAdjustMessage;
import cavern.network.client.RegenerationGuiMessage;
import cavern.network.server.RegenerationMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CaveNetworkRegistry
{
	public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(Cavern.MODID);

	public static int messageId;

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side)
	{
		network.registerMessage(messageHandler, requestMessageType, messageId++, side);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType)
	{
		registerMessage(messageHandler, requestMessageType, Side.CLIENT);
		registerMessage(messageHandler, requestMessageType, Side.SERVER);
	}

	public static Packet<?> getPacket(IMessage message)
	{
		return network.getPacketFrom(message);
	}

	public static void sendToAll(IMessage message)
	{
		network.sendToAll(message);
	}

	public static void sendToOthers(IMessage message, EntityPlayerMP player)
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

		if (server != null && server.isDedicatedServer())
		{
			for (EntityPlayerMP thePlayer : server.getPlayerList().getPlayerList())
			{
				if (player == thePlayer)
				{
					sendTo(message, thePlayer);
				}
			}
		}
	}

	public static void sendTo(IMessage message, EntityPlayerMP player)
	{
		network.sendTo(message, player);
	}

	public static void sendToDimension(IMessage message, int dimensionId)
	{
		network.sendToDimension(message, dimensionId);
	}

	public static void sendToServer(IMessage message)
	{
		network.sendToServer(message);
	}

	public static void registerMessages()
	{
		registerMessage(MinerStatsAdjustMessage.class, MinerStatsAdjustMessage.class, Side.CLIENT);
		registerMessage(LastMineMessage.class, LastMineMessage.class, Side.CLIENT);
		registerMessage(CaveMusicMessage.class, CaveMusicMessage.class, Side.CLIENT);
		registerMessage(RegenerationGuiMessage.class, RegenerationGuiMessage.class, Side.CLIENT);
		registerMessage(RegenerationMessage.class, RegenerationMessage.class, Side.SERVER);
	}
}