package cavern.block;

import cavern.item.CaveItems;
import cavern.item.ItemAcresia;
import cavern.item.ItemBlockCave;
import cavern.item.ItemBlockPerverted;
import cavern.item.ItemCave;
import cavern.item.ItemPortalCave;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class CaveBlocks
{
	public static final BlockPortalCavern CAVERN_PORTAL = new BlockPortalCavern();
	public static final BlockPortalAquaCavern AQUA_CAVERN_PORTAL = new BlockPortalAquaCavern();
	public static final BlockPortalCaveland CAVELAND_PORTAL = new BlockPortalCaveland();
	public static final BlockCave CAVE_BLOCK = new BlockCave();
	public static final BlockAcresia ACRESIA = new BlockAcresia();
	public static final BlockLogPerverted PERVERTED_LOG = new BlockLogPerverted();
	public static final BlockLeavesPerverted PERVERTED_LEAVES = new BlockLeavesPerverted();
	public static final BlockSaplingPerverted PERVERTED_SAPLING = new BlockSaplingPerverted();

	public static void registerBlocks()
	{
		GameRegistry.register(CAVERN_PORTAL.setRegistryName("cavern_portal"));
		GameRegistry.register(new ItemPortalCave(CAVERN_PORTAL));

		GameRegistry.register(AQUA_CAVERN_PORTAL.setRegistryName("aqua_cavern_portal"));
		GameRegistry.register(new ItemPortalCave(AQUA_CAVERN_PORTAL));

		GameRegistry.register(CAVELAND_PORTAL.setRegistryName("caveland_portal"));
		GameRegistry.register(new ItemPortalCave(CAVELAND_PORTAL));

		GameRegistry.register(CAVE_BLOCK.setRegistryName("cave_block"));
		GameRegistry.register(new ItemBlockCave(CAVE_BLOCK));

		GameRegistry.register(ACRESIA.setRegistryName("acresia"));
		GameRegistry.register(new ItemAcresia(ACRESIA));

		GameRegistry.register(PERVERTED_LOG.setRegistryName("perverted_log"));
		GameRegistry.register(new ItemBlockPerverted(PERVERTED_LOG, Blocks.LOG));

		GameRegistry.register(PERVERTED_LEAVES.setRegistryName("perverted_leaves"));
		GameRegistry.register(new ItemBlockPerverted(PERVERTED_LEAVES, Blocks.LEAVES));

		GameRegistry.register(PERVERTED_SAPLING.setRegistryName("perverted_sapling"));
		GameRegistry.register(new ItemBlockPerverted(PERVERTED_SAPLING, Blocks.SAPLING));

		OreDictionary.registerOre("oreAquamarine", new ItemStack(CAVE_BLOCK, 1, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()));
		OreDictionary.registerOre("oreMagnite", new ItemStack(CAVE_BLOCK, 1, BlockCave.EnumType.MAGNITE_ORE.getMetadata()));
		OreDictionary.registerOre("treeLeaves", new ItemStack(PERVERTED_LEAVES, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("treeSapling", new ItemStack(PERVERTED_SAPLING, 1, OreDictionary.WILDCARD_VALUE));
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels()
	{
		ModelLoader.setCustomStateMapper(CAVE_BLOCK, new StateMap.Builder().withName(BlockCave.VARIANT).build());
		ModelLoader.setCustomStateMapper(PERVERTED_LOG, new StateMap.Builder().withName(BlockOldLog.VARIANT).withSuffix("_log").build());
		ModelLoader.setCustomStateMapper(PERVERTED_LEAVES, new StateMap.Builder().withName(BlockOldLeaf.VARIANT).withSuffix("_leaves").ignore(new IProperty[] {BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE}).build());
		ModelLoader.setCustomStateMapper(PERVERTED_SAPLING, new StateMap.Builder().withName(BlockSapling.TYPE).withSuffix("_sapling").build());

		registerModel(CAVERN_PORTAL, "cavern_portal");
		registerModel(AQUA_CAVERN_PORTAL, "aqua_cavern_portal");
		registerModel(CAVELAND_PORTAL, "caveland_portal");
		registerModelWithMeta(CAVE_BLOCK, "aquamarine_ore", "aquamarine_block", "magnite_ore", "magnite_block", "randomite_ore", "hexcite_ore", "hexcite_block");
		registerModelWithMeta(ACRESIA, "acresia_seeds", "acresia_fruits");
		registerVanillaModelWithMeta(PERVERTED_LOG, "oak_log", "spruce_log", "birch_log", "jungle_log");
		registerVanillaModelWithMeta(PERVERTED_LEAVES, "oak_leaves", "spruce_leaves", "birch_leaves", "jungle_leaves");
		registerVanillaModelWithMeta(PERVERTED_SAPLING, "oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling", "acacia_sapling", "dark_oak_sapling");
	}

	@SideOnly(Side.CLIENT)
	public static void registerModel(Block block, String modelName)
	{
		CaveItems.registerModel(Item.getItemFromBlock(block), modelName);
	}

	@SideOnly(Side.CLIENT)
	public static void registerModelWithMeta(Block block, String... modelName)
	{
		CaveItems.registerModelWithMeta(Item.getItemFromBlock(block), modelName);
	}

	@SideOnly(Side.CLIENT)
	public static void registerVanillaModel(Block block, String modelName)
	{
		CaveItems.registerVanillaModel(Item.getItemFromBlock(block), modelName);
	}

	@SideOnly(Side.CLIENT)
	public static void registerVanillaModelWithMeta(Block block, String... modelName)
	{
		CaveItems.registerVanillaModelWithMeta(Item.getItemFromBlock(block), modelName);
	}

	@SideOnly(Side.CLIENT)
	public static void registerBlockColors()
	{
		final Minecraft mc = FMLClientHandler.instance().getClient();
		final BlockColors colors = mc.getBlockColors();

		colors.registerBlockColorHandler((state, world, pos, tintIndex) ->
		{
			PERVERTED_LEAVES.setGraphicsLevel(mc.gameSettings.fancyGraphics);

			BlockPlanks.EnumType type = state.getValue(BlockOldLeaf.VARIANT);

			return type == BlockPlanks.EnumType.SPRUCE ? ColorizerFoliage.getFoliageColorPine() : type == BlockPlanks.EnumType.BIRCH ? ColorizerFoliage.getFoliageColorBirch() : world != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(world, pos) : ColorizerFoliage.getFoliageColorBasic();
		}, new Block[] {PERVERTED_LEAVES});
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemBlockColors()
	{
		final Minecraft mc = FMLClientHandler.instance().getClient();
		final BlockColors blockColors = mc.getBlockColors();
		final ItemColors colors = mc.getItemColors();

		colors.registerItemColorHandler((IItemColor) (stack, tintIndex) ->
		{
			IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());

			return blockColors.colorMultiplier(state, null, null, tintIndex);
		}, new Block[] {PERVERTED_LEAVES});
	}

	public static void registerRecipes()
	{
		GameRegistry.addShapedRecipe(new ItemStack(CAVE_BLOCK, 1, BlockCave.EnumType.AQUAMARINE_BLOCK.getMetadata()),
			"AAA", "AAA", "AAA",
			'A', new ItemStack(CaveItems.CAVE_ITEM, 1, ItemCave.EnumType.AQUAMARINE.getItemDamage())
		);

		GameRegistry.addShapedRecipe(new ItemStack(CAVE_BLOCK, 1, BlockCave.EnumType.MAGNITE_BLOCK.getMetadata()),
			"MMM", "MMM", "MMM",
			'M', new ItemStack(CaveItems.CAVE_ITEM, 1, ItemCave.EnumType.MAGNITE_INGOT.getItemDamage())
		);

		GameRegistry.addShapedRecipe(new ItemStack(CAVE_BLOCK, 1, BlockCave.EnumType.HEXCITE_BLOCK.getMetadata()),
			"HHH", "HHH", "HHH",
			'H', new ItemStack(CaveItems.CAVE_ITEM, 1, ItemCave.EnumType.HEXCITE.getItemDamage())
		);

		GameRegistry.addShapelessRecipe(new ItemStack(Items.STICK, 8), new ItemStack(PERVERTED_LOG, 1, OreDictionary.WILDCARD_VALUE));

		for (BlockPlanks.EnumType type : BlockOldLog.VARIANT.getAllowedValues())
		{
			int meta = type.getMetadata();

			GameRegistry.addShapedRecipe(new ItemStack(Blocks.PLANKS, 4, meta),
				"LL", "LL",
				'L', new ItemStack(PERVERTED_LOG, 1, meta)
			);

			GameRegistry.addShapelessRecipe(new ItemStack(PERVERTED_SAPLING, 1, meta), new ItemStack(Blocks.SAPLING, 1, meta), Items.FERMENTED_SPIDER_EYE);
		}

		GameRegistry.addSmelting(new ItemStack(CAVE_BLOCK, 1, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()),
			new ItemStack(CaveItems.CAVE_ITEM, 1, ItemCave.EnumType.AQUAMARINE.getItemDamage()), 1.0F);

		GameRegistry.addSmelting(new ItemStack(CAVE_BLOCK, 1, BlockCave.EnumType.MAGNITE_ORE.getMetadata()),
			new ItemStack(CaveItems.CAVE_ITEM, 1, ItemCave.EnumType.MAGNITE_INGOT.getItemDamage()), 0.7F);

		GameRegistry.addSmelting(new ItemStack(CAVE_BLOCK, 1, BlockCave.EnumType.HEXCITE_ORE.getMetadata()),
			new ItemStack(CaveItems.CAVE_ITEM, 1, ItemCave.EnumType.HEXCITE.getItemDamage()), 1.0F);

		GameRegistry.addSmelting(new ItemStack(PERVERTED_LOG, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.COAL, 1, 1), 0.0F);
	}
}