package cavern.config;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.client.config.CaveConfigEntries;
import cavern.config.manager.CaveBiome;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.manager.CaveVein;
import cavern.config.manager.CaveVeinManager;
import cavern.config.property.ConfigBiomeType;
import cavern.core.Cavern;
import cavern.util.BlockMeta;
import cavern.world.CaveType;
import cavern.world.gen.WorldGenCavernDungeons;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CavernConfig
{
	public static Configuration config;

	public static int dimensionId;
	public static int worldHeight;
	public static boolean randomSeed;
	public static ConfigBiomeType biomeType = new ConfigBiomeType();

	public static boolean generateCaves;
	public static boolean generateRavine;
	public static boolean generateExtremeCaves;
	public static boolean generateExtremeRavine;
	public static boolean generateLakes;
	public static boolean generateDungeons;
	public static boolean generateMineshaft;

	public static String[] dungeonMobs;

	public static int monsterSpawn;
	public static double caveBrightness;

	public static CaveBiomeManager biomeManager = new CaveBiomeManager(CaveType.CAVERN);
	public static CaveVeinManager veinManager = new CaveVeinManager(CaveType.CAVERN);

	public static void syncConfig()
	{
		String category = "dimension";
		Property prop;
		String comment;
		List<String> propOrder = Lists.newArrayList();

		if (config == null)
		{
			config = Config.loadConfig(category);
		}

		prop = config.get(category, "dimension", -50);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		dimensionId = prop.getInt(dimensionId);

		prop = config.get(category, "worldHeight", 128);
		prop.setMinValue(64).setMaxValue(256);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		worldHeight = prop.getInt(worldHeight);

		prop = config.get(category, "randomSeed", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		randomSeed = prop.getBoolean(randomSeed);

		prop = config.get(category, "biomeType", ConfigBiomeType.Type.NATURAL.ordinal());
		prop.setMinValue(0).setMaxValue(ConfigBiomeType.Type.values().length - 1).setConfigEntryClass(CaveConfigEntries.cycleInteger);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";

		int min = Integer.parseInt(prop.getMinValue());
		int max = Integer.parseInt(prop.getMaxValue());

		for (int i = min; i <= max; ++i)
		{
			comment += Configuration.NEW_LINE + i + ": " + Cavern.proxy.translate(prop.getLanguageKey() + "." + i);

			if (i < max)
			{
				comment += ",";
			}
		}

		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		biomeType.setValue(prop.getInt(biomeType.getValue()));

		prop = config.get(category, "generateCaves", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateCaves = prop.getBoolean(generateCaves);

		prop = config.get(category, "generateRavine", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateRavine = prop.getBoolean(generateRavine);

		prop = config.get(category, "generateExtremeCaves", false);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateExtremeCaves = prop.getBoolean(generateExtremeCaves);

		prop = config.get(category, "generateExtremeRavine", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateExtremeRavine = prop.getBoolean(generateExtremeRavine);

		prop = config.get(category, "generateLakes", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateLakes = prop.getBoolean(generateLakes);

		prop = config.get(category, "generateDungeons", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateDungeons = prop.getBoolean(generateDungeons);

		prop = config.get(category, "generateMineshaft", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateMineshaft = prop.getBoolean(generateMineshaft);

		Set<Class<? extends Entity>> classes = Sets.newHashSet();

		classes.add(EntityZombie.class);
		classes.add(EntitySkeleton.class);
		classes.add(EntitySpider.class);
		classes.add(EntityCaveSpider.class);
		classes.add(EntityCreeper.class);
		classes.add(EntityEnderman.class);
		classes.add(EntitySilverfish.class);

		Set<String> mobs = Sets.newTreeSet();

		for (Class<? extends Entity> clazz : classes)
		{
			mobs.add(EntityList.CLASS_TO_NAME.get(clazz));
		}

		prop = config.get(category, "dungeonMobs", mobs.toArray(new String[mobs.size()]));
		prop.setConfigEntryClass(CaveConfigEntries.selectMobs);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		dungeonMobs = prop.getStringList();

		prop = config.get(category, "monsterSpawn", 0);
		prop.setMinValue(0).setMaxValue(5000);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		monsterSpawn = prop.getInt(monsterSpawn);

		prop = config.get(category, "caveBrightness", 0.035D);
		prop.setMinValue(0.0D).setMaxValue(1.0D);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		caveBrightness = prop.getDouble(caveBrightness);

		config.setCategoryPropertyOrder(category, propOrder);
		config.setCategoryLanguageKey(category, Config.LANG_KEY + category + ".cavern");

		Config.saveConfig(config);
	}

	public static void syncBiomesConfig()
	{
		if (biomeManager.config == null)
		{
			biomeManager.config = Config.loadConfig("biomes");
		}
		else
		{
			biomeManager.getCaveBiomes().clear();
		}

		if (biomeManager.config.getCategoryNames().isEmpty())
		{
			List<CaveBiome> biomes = Lists.newArrayList();

			biomes.add(new CaveBiome(Biomes.OCEAN, 15));
			biomes.add(new CaveBiome(Biomes.PLAINS, 100));
			biomes.add(new CaveBiome(Biomes.DESERT, 70));
			biomes.add(new CaveBiome(Biomes.DESERT_HILLS, 10));
			biomes.add(new CaveBiome(Biomes.FOREST, 80));
			biomes.add(new CaveBiome(Biomes.FOREST_HILLS, 10));
			biomes.add(new CaveBiome(Biomes.TAIGA, 80));
			biomes.add(new CaveBiome(Biomes.TAIGA_HILLS, 10));
			biomes.add(new CaveBiome(Biomes.JUNGLE, 80));
			biomes.add(new CaveBiome(Biomes.JUNGLE_HILLS, 10));
			biomes.add(new CaveBiome(Biomes.SWAMPLAND, 60));
			biomes.add(new CaveBiome(Biomes.EXTREME_HILLS, 50));
			biomes.add(new CaveBiome(Biomes.SAVANNA, 50));
			biomes.add(new CaveBiome(Biomes.MESA, 50));

			generateBiomesConfig(biomeManager, biomes);
		}
		else
		{
			addBiomesFromConfig(biomeManager);
		}

		Config.saveConfig(biomeManager.config);
	}

	public static void syncVeinsConfig()
	{
		if (veinManager.config == null)
		{
			veinManager.config = Config.loadConfig("veins");
		}
		else
		{
			veinManager.getCaveVeins().clear();
		}

		if (veinManager.config.getCategoryNames().isEmpty())
		{
			List<CaveVein> veins = Lists.newArrayList();

			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.GRANITE.getMetadata()), 28, 25, 1, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.DIORITE.getMetadata()), 28, 25, 1, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.ANDESITE.getMetadata()), 30, 25, 1, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.COAL_ORE, 0), 35, 17, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.IRON_ORE, 0), 30, 10, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.GOLD_ORE, 0), 5, 7, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.REDSTONE_ORE, 0), 12, 7, 1, 40));
			veins.add(new CaveVein(new BlockMeta(Blocks.LAPIS_ORE, 0), 4, 5, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIAMOND_ORE, 0), 2, 6, 1, 20));
			veins.add(new CaveVein(new BlockMeta(Blocks.EMERALD_ORE, 0), 8, 5, 50, 127, Type.MOUNTAIN, Type.HILLS));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()), 12, 8, 20, 127, Type.COLD, Type.WATER, Type.WET));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.MAGNITE_ORE.getMetadata()), 30, 10, 1, 127));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.RANDOMITE_ORE.getMetadata()), 24, 4, 1, 127));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.HEXCITE_ORE.getMetadata()), 4, 5, 1, 30));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIRT, 0), 20, 25, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.GRAVEL, 0), 10, 20, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.SAND, BlockSand.EnumType.SAND.getMetadata()), 10, 20, 1, 127, Type.SANDY));

			generateVeinsConfig(veinManager, veins);
		}
		else
		{
			if (addVeinsFromConfig(veinManager))
			{
				try
				{
					FileUtils.forceDelete(new File(veinManager.config.toString()));

					veinManager.getCaveVeins().clear();
					veinManager.config = null;

					syncVeinsConfig();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		Config.saveConfig(veinManager.config);
	}

	public static void generateBiomesConfig(CaveBiomeManager manager, Collection<CaveBiome> biomes)
	{
		String category = "biomes";
		Property prop;
		String comment;

		for (CaveBiome caveBiome : biomes)
		{
			Biome biome = caveBiome.getBiome();
			String entry = biome.getRegistryName().toString();
			List<String> propOrder = Lists.newArrayList();

			prop = manager.config.get(entry, "weight", 0);
			prop.setMinValue(0).setMaxValue(100);
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + "]";
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(caveBiome.getWeight());

			prop = manager.config.get(entry, "terrainBlock", biome.fillerBlock.getBlock().getRegistryName().toString());
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(caveBiome.getTerrainBlock().getBlockName());

			prop = manager.config.get(entry, "terrainBlockMeta", Integer.toString(biome.fillerBlock.getBlock().getMetaFromState(biome.fillerBlock)));
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(caveBiome.getTerrainBlock().getMetaString());

			prop = manager.config.get(entry, "topBlock", biome.topBlock.getBlock().getRegistryName().toString());
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(caveBiome.getTopBlock().getBlockName());

			prop = manager.config.get(entry, "topBlockMeta", Integer.toString(biome.topBlock.getBlock().getMetaFromState(biome.topBlock)));
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(caveBiome.getTopBlock().getMetaString());

			manager.config.setCategoryPropertyOrder(entry, propOrder);

			manager.addCaveBiome(caveBiome);
		}
	}

	public static void addBiomesFromConfig(CaveBiomeManager manager)
	{
		for (String name : manager.config.getCategoryNames())
		{
			ResourceLocation entry = new ResourceLocation(name);

			if (Biome.REGISTRY.containsKey(entry))
			{
				Biome biome = Biome.REGISTRY.getObject(entry);
				ConfigCategory category = manager.config.getCategory(name);

				int weight = category.get("weight").getInt();
				String terrainBlock = category.get("terrainBlock").getString();
				String terrainBlockMeta = category.get("terrainBlockMeta").getString();
				String topBlock = category.get("topBlock").getString();
				String topBlockMeta = category.get("topBlockMeta").getString();

				CaveBiome caveBiome = new CaveBiome(biome, weight);

				caveBiome.setTerrainBlock(new BlockMeta(terrainBlock, terrainBlockMeta));
				caveBiome.setTopBlock(new BlockMeta(topBlock, topBlockMeta));

				manager.addCaveBiome(caveBiome);
			}
		}
	}

	public static void generateVeinsConfig(CaveVeinManager manager, Collection<CaveVein> veins)
	{
		String category = "veins";
		Property prop;
		String comment;
		String blockDefault = Blocks.STONE.getRegistryName().toString();
		int index = 0;

		for (CaveVein vein : veins)
		{
			String entry = Integer.toString(index);
			List<String> propOrder = Lists.newArrayList();

			prop = manager.config.get(entry, "block", blockDefault);
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(vein.getBlockMeta().getBlockName());

			prop = manager.config.get(entry, "blockMeta", Integer.toString(0));
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(vein.getBlockMeta().getMetaString());

			prop = manager.config.get(entry, "targetBlock", blockDefault);
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(vein.getTarget().getBlockName());

			prop = manager.config.get(entry, "targetBlockMeta", Integer.toString(0));
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(vein.getTarget().getMetaString());

			prop = manager.config.get(entry, "weight", 1);
			prop.setMinValue(0).setMaxValue(100);
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + "]";
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(vein.getWeight());

			prop = manager.config.get(entry, "chance", 1.0D);
			prop.setMinValue(0.0D).setMaxValue(1.0D);
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(vein.getChance());

			prop = manager.config.get(entry, "size", 1);
			prop.setMinValue(0).setMaxValue(100);
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + "]";
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(vein.getSize());

			prop = manager.config.get(entry, "minHeight", 0);
			prop.setMinValue(0).setMaxValue(255);
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(vein.getMinHeight());

			prop = manager.config.get(entry, "maxHeight", 255);
			prop.setMinValue(0).setMaxValue(255);
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(vein.getMaxHeight());

			prop = manager.config.get(entry, "biomes", new int[0]);
			prop.setMaxListLength(256);
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			prop.setComment(comment);
			propOrder.add(prop.getName());
			prop.set(vein.getBiomes());

			manager.config.setCategoryPropertyOrder(entry, propOrder);

			manager.addCaveVein(vein);

			++index;
		}
	}

	public static boolean addVeinsFromConfig(CaveVeinManager manager)
	{
		boolean flag = false;

		for (String name : manager.config.getCategoryNames())
		{
			if (NumberUtils.isNumber(name))
			{
				try
				{
					ConfigCategory category = manager.config.getCategory(name);

					String block = category.get("block").getString();
					String blockMeta = category.get("blockMeta").getString();
					String targetBlock = category.get("targetBlock").getString();
					String targetBlockMeta = category.get("targetBlockMeta").getString();
					int weight = category.get("weight").getInt();
					double chance = category.get("chance").getDouble();
					int size = category.get("size").getInt();
					int minHeight = category.get("minHeight").getInt();
					int maxHeight = category.get("maxHeight").getInt();
					int[] biomes = category.get("biomes").getIntList();

					CaveVein vein = new CaveVein();

					vein.setBlockMeta(new BlockMeta(block, blockMeta));
					vein.setTarget(new BlockMeta(targetBlock, targetBlockMeta));
					vein.setWeight(weight);
					vein.setChance(chance);
					vein.setSize(size);
					vein.setMinHeight(minHeight);
					vein.setMaxHeight(maxHeight);
					vein.setBiomes(biomes);

					manager.addCaveVein(vein);
				}
				catch (Exception e) {}
			}
			else
			{
				flag = true;
			}
		}

		return flag;
	}

	public static void refreshDungeonMobs()
	{
		WorldGenCavernDungeons.clearDungeonMobs();
		WorldGenCavernDungeons.addDungeonMobs(Sets.newHashSet(dungeonMobs));
	}
}