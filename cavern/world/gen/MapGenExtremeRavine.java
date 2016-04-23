package cavern.world.gen;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenExtremeRavine extends MapGenCavernRavine
{
	@Override
	protected void recursiveGenerate(World world, int chunkX, int chunkZ, int x, int z, ChunkPrimer primer)
	{
		if (rand.nextInt(800) == 0)
		{
			double blockX = chunkX * 16 + rand.nextInt(16);
			double blockY = rand.nextInt(rand.nextInt(10) + world.provider.getAverageGroundLevel());
			double blockZ = chunkZ * 16 + rand.nextInt(16);

			for (int i = 0; i < 2; ++i)
			{
				float leftRightRadian = rand.nextFloat() * (float)Math.PI * 7.0F;
				float upDownRadian = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float scale = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 9.0F;

				func_180707_a(rand.nextLong(), x, z, primer, blockX, blockY, blockZ, scale, leftRightRadian, upDownRadian, 0, 0, 25.0D);
			}
		}
	}
}