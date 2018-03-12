package cavern.world;

import cavern.config.HugeCavernConfig;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.property.ConfigBiomeType;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.IChunkGenerator;

public class WorldProviderHugeCavern extends WorldProviderCavern
{
	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkGeneratorHugeCavern(world);
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveType.DIM_HUGE_CAVERN;
	}

	@Override
	public ConfigBiomeType.Type getBiomeType()
	{
		return ConfigBiomeType.Type.NATURAL;
	}

	@Override
	public int getWorldHeight()
	{
		return HugeCavernConfig.worldHeight;
	}

	@Override
	public boolean isRandomSeed()
	{
		return HugeCavernConfig.randomSeed;
	}

	@Override
	public CaveBiomeManager getBiomeManager()
	{
		return HugeCavernConfig.biomeManager;
	}

	@Override
	public int getMonsterSpawn()
	{
		return HugeCavernConfig.monsterSpawn;
	}

	@Override
	public double getBrightness()
	{
		return HugeCavernConfig.caveBrightness;
	}
}