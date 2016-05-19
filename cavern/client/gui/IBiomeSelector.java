package cavern.client.gui;

import java.util.Collection;

import net.minecraft.world.biome.Biome;

public interface IBiomeSelector
{
	public void onBiomeSelected(int id, Collection<Biome> selected);

	public boolean canSelectBiome(int id, Biome biome);
}