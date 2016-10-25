package cavern.api;

import net.minecraft.entity.Entity;

public interface IDimension
{
	public int getCavernDimension();

	public int getAquaCavernDimension();

	public int getCavelandDimension();

	public boolean isAquaCavernDisabled();

	public boolean isCavelandDisabled();

	public boolean isEntityInCavern(Entity entity);

	public boolean isEntityInAquaCavern(Entity entity);

	public boolean isEntityInCaveland(Entity entity);

	public boolean isEntityInCaves(Entity entity);

	public boolean isCavern(int dimension);

	public boolean isAquaCavern(int dimension);

	public boolean isCaveland(int dimension);

	public boolean isCaves(int dimension);
}