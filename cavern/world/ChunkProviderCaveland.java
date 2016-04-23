package cavern.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import cavern.config.CavelandConfig;
import cavern.config.manager.CaveVein;
import cavern.world.gen.MapGenCavelandCaves;
import cavern.world.gen.MapGenCavelandRavine;
import cavern.world.gen.WorldGenAcresia;
import cavern.world.gen.WorldGenBirchTreePerverted;
import cavern.world.gen.WorldGenSpruceTreePerverted;
import cavern.world.gen.WorldGenTreesPerverted;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ChunkProviderCaveland implements IChunkGenerator
{
	private final World worldObj;
	private final Random rand;

	private BiomeGenBase[] biomesForGeneration;

	private final MapGenBase caveGenerator = new MapGenCavelandCaves();
	private final MapGenBase ravineGenerator = new MapGenCavelandRavine();

	private WorldGenerator lakeWaterGen = new WorldGenLakes(Blocks.water);
	private WorldGenerator lakeLavaGen = new WorldGenLakes(Blocks.lava);
	private WorldGenerator liquidWaterGen = new WorldGenLiquids(Blocks.flowing_water);
	private WorldGenerator liquidLavaGen = new WorldGenLiquids(Blocks.flowing_lava);
	private WorldGenerator deadBushGen = new WorldGenDeadBush();
	private WorldGenerator acresiaGen = new WorldGenAcresia();

	public ChunkProviderCaveland(World world)
	{
		this.worldObj = world;
		this.rand = new Random(world.getSeed());
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);

		int worldHeight = worldObj.provider.getActualHeight();
		int blockHeight = worldHeight - 1;

		biomesForGeneration = worldObj.getBiomeProvider().loadBlockGeneratorData(biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);

		ChunkPrimer primer = new ChunkPrimer();

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				for (int y = 255; y >= 0; --y)
				{
					primer.setBlockState(x, y, z, Blocks.dirt.getDefaultState());
				}
			}
		}

		caveGenerator.generate(worldObj, chunkX, chunkZ, primer);

		if (CavelandConfig.generateRiver)
		{
			ravineGenerator.generate(worldObj, chunkX, chunkZ, primer);
		}

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				BiomeGenBase biome = biomesForGeneration[x * 16 + z];
				IBlockState top = biome.topBlock;
				IBlockState filter = biome.fillerBlock;

				if (filter.getBlock() == Blocks.sand)
				{
					filter = Blocks.sandstone.getDefaultState();
				}

				primer.setBlockState(x, 0, z, Blocks.bedrock.getDefaultState());
				primer.setBlockState(x, blockHeight, z, Blocks.bedrock.getDefaultState());
				primer.setBlockState(x, 1, z, primer.getBlockState(x, 2, z));

				for (int y = 1; y <= blockHeight - 1; ++y)
				{
					if (primer.getBlockState(x, y, z).getBlock() == Blocks.grass ||
						primer.getBlockState(x, y, z).getMaterial().isSolid() && primer.getBlockState(x, y + 1, z).getBlock() == Blocks.air)
					{
						primer.setBlockState(x, y, z, top);
					}
					else if (primer.getBlockState(x, y, z).getBlock() == Blocks.dirt)
					{
						primer.setBlockState(x, y, z, filter);
					}
				}

				if (blockHeight < 255)
				{
					for (int y = blockHeight + 1; y < 256; ++y)
					{
						primer.setBlockState(x, y, z, Blocks.air.getDefaultState());
					}
				}
			}
		}

		Chunk chunk = new Chunk(worldObj, primer, chunkX, chunkZ);
		byte[] biomeArray = chunk.getBiomeArray();

		for (int i = 0; i < biomeArray.length; ++i)
		{
			biomeArray[i] = (byte)BiomeGenBase.getIdForBiome(biomesForGeneration[i]);
		}

		chunk.resetRelightChecks();

		return chunk;
	}

	@Override
	public void populate(int chunkX, int chunkZ)
	{
		BlockFalling.fallInstantly = true;

		int worldX = chunkX * 16;
		int worldZ = chunkZ * 16;
		BlockPos blockPos = new BlockPos(worldX, 0, worldZ);
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(blockPos.add(16, 0, 16));
		BiomeDecorator decorator = biome.theBiomeDecorator;
		int worldHeight = worldObj.provider.getActualHeight();

		ForgeEventFactory.onChunkPopulate(true, this, this.worldObj, chunkX, chunkZ, false);

		int x, y, z;

		if (CavelandConfig.generateLakes)
		{
			if (TerrainGen.populate(this, worldObj, rand, chunkX, chunkZ, false, EventType.LAKE))
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 16);
				z = rand.nextInt(16) + 8;

				lakeWaterGen.generate(worldObj, rand, blockPos.add(x, y, z));
			}

			if (rand.nextInt(30) == 0 && TerrainGen.populate(this, worldObj, rand, chunkX, chunkZ, false, EventType.LAVA))
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight / 2);
				z = rand.nextInt(16) + 8;

				lakeLavaGen.generate(worldObj, rand, blockPos.add(x, y, z));
			}
		}

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(worldObj, rand, blockPos));

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(worldObj, rand, blockPos));

		for (CaveVein vein : CavelandConfig.veinManager.getCaveVeins())
		{
			vein.generateVeins(worldObj, rand, blockPos);
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(worldObj, rand, blockPos));

		for (int i = 0; i < 10; ++i)
		{
			x = rand.nextInt(16) + 8;
			y = rand.nextInt(worldHeight - 10);
			z = rand.nextInt(16) + 8;

			acresiaGen.generate(worldObj, rand, blockPos.add(x, y, z));
		}

		for (int i = 0; i < 15; ++i)
		{
			x = rand.nextInt(16) + 8;
			y = rand.nextInt(worldHeight / 2 - 10) + worldHeight / 2;
			z = rand.nextInt(16) + 8;

			acresiaGen.generate(worldObj, rand, blockPos.add(x, y, z));
		}

		if (TerrainGen.decorate(worldObj, rand, blockPos, Decorate.EventType.SHROOM))
		{
			for (int i = 0; i < 5; ++i)
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 10);
				z = rand.nextInt(16) + 8;

				decorator.mushroomBrownGen.generate(worldObj, rand, blockPos.add(x, y, z));
			}

			for (int i = 0; i < 5; ++i)
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 10);
				z = rand.nextInt(16) + 8;

				decorator.mushroomRedGen.generate(worldObj, rand, blockPos.add(x, y, z));
			}
		}

		if (BiomeDictionary.isBiomeOfType(biome, Type.SANDY))
		{
			if (TerrainGen.decorate(worldObj, rand, blockPos, Decorate.EventType.CACTUS))
			{
				for (int i = 0; i < 80; ++i)
				{
					x = rand.nextInt(16) + 8;
					y = rand.nextInt(worldHeight - 5);
					z = rand.nextInt(16) + 8;

					decorator.cactusGen.generate(worldObj, rand, blockPos.add(x, y, z));
				}
			}

			if (TerrainGen.decorate(worldObj, rand, blockPos, Decorate.EventType.DEAD_BUSH))
			{
				for (int i = 0; i < 10; ++i)
				{
					x = rand.nextInt(16) + 8;
					y = rand.nextInt(worldHeight - 5);
					z = rand.nextInt(16) + 8;

					deadBushGen.generate(worldObj, rand, blockPos.add(x, y, z));
				}
			}
		}
		else
		{
			if (TerrainGen.decorate(worldObj, rand, blockPos, Decorate.EventType.FLOWERS))
			{
				for (int i = 0; i < 8; ++i)
				{
					x = rand.nextInt(16) + 8;
					y = rand.nextInt(worldHeight - 5);
					z = rand.nextInt(16) + 8;

					decorator.yellowFlowerGen.generate(worldObj, rand, blockPos.add(x, y, z));
				}
			}

			for (int i = 0; i < 18; ++i)
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 5);
				z = rand.nextInt(16) + 8;

				biome.getRandomWorldGenForGrass(rand).generate(worldObj, rand, blockPos.add(x, y, z));
			}

			if (TerrainGen.decorate(worldObj, rand, blockPos, Decorate.EventType.TREE))
			{
				WorldGenAbstractTree treeGen = null;

				if (BiomeDictionary.isBiomeOfType(biome, Type.JUNGLE))
				{
					treeGen = new WorldGenTreesPerverted(false, 4 + rand.nextInt(7), BlockPlanks.EnumType.JUNGLE, true);
				}
				else if (BiomeDictionary.isBiomeOfType(biome, Type.FOREST) || !BiomeDictionary.isBiomeOfType(biome, Type.PLAINS) || rand.nextInt(10) == 0)
				{
					if (BiomeDictionary.isBiomeOfType(biome, Type.COLD))
					{
						treeGen = new WorldGenSpruceTreePerverted(false);
					}
					else if (rand.nextInt(3) == 0)
					{
						treeGen = new WorldGenBirchTreePerverted(false, false);
					}
					else
					{
						treeGen = new WorldGenTreesPerverted(false, 3, BlockPlanks.EnumType.OAK, true);
					}
				}

				if (treeGen != null)
				{
					for (int i = 0; i < 80; ++i)
					{
						x = rand.nextInt(16) + 8;
						y = rand.nextInt(worldHeight);
						z = rand.nextInt(16) + 8;

						BlockPos pos = blockPos.add(x, y, z);

						if (treeGen.generate(worldObj, rand, pos))
						{
							treeGen.func_180711_a(worldObj, rand, pos);
						}
					}

					for (int i = 0; i < 60; ++i)
					{
						x = rand.nextInt(16) + 8;
						y = 8 + rand.nextInt(5);
						z = rand.nextInt(16) + 8;

						BlockPos pos = blockPos.add(x, y, z);

						if (treeGen.generate(worldObj, rand, pos))
						{
							treeGen.func_180711_a(worldObj, rand, pos);
						}
					}
				}
			}

			if (decorator.generateLakes)
			{
				if (BiomeDictionary.isBiomeOfType(biome, Type.WATER))
				{
					if (TerrainGen.decorate(worldObj, rand, blockPos, Decorate.EventType.LAKE_WATER))
					{
						for (int i = 0; i < 150; ++i)
						{
							x = rand.nextInt(16) + 8;
							y = rand.nextInt(rand.nextInt(worldHeight - 16) + 10);
							z = rand.nextInt(16) + 8;

							liquidWaterGen.generate(worldObj, rand, blockPos.add(x, y, z));
						}
					}
				}
				else
				{
					if (TerrainGen.decorate(worldObj, rand, blockPos, Decorate.EventType.LAKE_WATER))
					{
						for (int i = 0; i < 100; ++i)
						{
							x = rand.nextInt(16) + 8;
							y = rand.nextInt(rand.nextInt(worldHeight - 16) + 10);
							z = rand.nextInt(16) + 8;

							liquidWaterGen.generate(worldObj, rand, blockPos.add(x, y, z));
						}
					}

					if (TerrainGen.decorate(worldObj, rand, blockPos, Decorate.EventType.LAKE_LAVA))
					{
						for (int i = 0; i < 20; ++i)
						{
							x = rand.nextInt(16) + 8;
							y = rand.nextInt(worldHeight / 2);
							z = rand.nextInt(16) + 8;

							liquidLavaGen.generate(worldObj, rand, blockPos.add(x, y, z));
						}
					}
				}
			}
		}

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(worldObj, rand, blockPos));

		ForgeEventFactory.onChunkPopulate(false, this, this.worldObj, chunkX, chunkZ, false);

		BlockFalling.fallInstantly = false;
	}

	@Override
	public boolean generateStructures(Chunk chunk, int x, int z)
	{
		return false;
	}

	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
	{
		if (creatureType == EnumCreatureType.MONSTER)
		{
			return Collections.emptyList();
		}

		BiomeGenBase biome = worldObj.getBiomeGenForCoords(pos);

		if (creatureType == CaveType.CAVELAND_MONSTER)
		{
			return biome.getSpawnableList(EnumCreatureType.MONSTER);
		}

		return biome.getSpawnableList(creatureType);
	}

	@Override
	public BlockPos getStrongholdGen(World world, String structureName, BlockPos pos)
	{
		return null;
	}

	@Override
	public void recreateStructures(Chunk chunk, int x, int z) {}
}