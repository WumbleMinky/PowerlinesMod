package gmail.wumbleminky.powerlines.help;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockInfo {

	public World world;
	public int x;
	public int y;
	public int z;
	
	public BlockInfo(World world, int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
	
	public BlockInfo(TileEntity te){
		this.x = te.xCoord;
		this.y = te.yCoord;
		this.z = te.zCoord;
		this.world = te.getWorldObj();
	}
	
	public Block getBlock(){
		return world.getBlock(x, y, z);
	}
	
	public TileEntity getTileEntity(){
		return world.getTileEntity(x, y, z);
	}
	
	public int getRedstoneSignal(){
		return world.getBlockPowerInput(x, y, z);
	}
	
	public boolean equals(BlockInfo b){
		if (b.x == this.x && b.y == this.y && b.z == this.z){
			return true;
		}
		return false;
	}
}
