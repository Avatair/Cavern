package cavern.entity;

import cavern.api.CavernAPI;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.world.World;

public class EntityAquaSquid extends EntitySquid
{
	public EntityAquaSquid(World world)
	{
		super(world);
	}

	@Override
	public boolean getCanSpawnHere()
	{
		return CavernAPI.dimension.isEntityInAquaCavern(this);
	}
}