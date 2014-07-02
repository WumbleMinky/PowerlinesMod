package gmail.wumbleminky.powerlines.block;

import gmail.wumbleminky.powerlines.Powerlines;
import gmail.wumbleminky.powerlines.help.Reference;
import gmail.wumbleminky.powerlines.tileentity.TileEntityLampSpacer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLampSpacer extends BlockContainer {
	public BlockLampSpacer(){
		
		super(Material.cloth);
		setBlockName("lampSpacer");
		this.setBlockBounds(0.0f, -1f, 0f, 1f, 1f, 1f);
		setBlockTextureName(Reference.MODID + ":" + Powerlines.blockLamp.getUnlocalizedName().substring(5));
	}
	
	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int par6){
		// when the block breaks, break the primary block
		TileEntityLampSpacer tileEntity = (TileEntityLampSpacer)world.getTileEntity(i, j, k);
		if (tileEntity != null){
			world.setBlockToAir(tileEntity.primary_x, tileEntity.primary_y,  tileEntity.primary_z);
			world.removeTileEntity(tileEntity.primary_x, tileEntity.primary_y , tileEntity.primary_z );
		}
		world.removeTileEntity(i, j, k);
		
	}
	
	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block block){
		// Check the neighbor block, and if the primary block is air, destroy myself
		TileEntityLampSpacer tileEntity = (TileEntityLampSpacer)world.getTileEntity(i, j, k);
		
		if (tileEntity != null){
			if (world.isAirBlock(tileEntity.primary_x, tileEntity.primary_y, tileEntity.primary_z)){
				world.setBlockToAir(i, j, k);
				world.removeTileEntity(i, j, k);
			}
		}
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l){
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(){
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityLampSpacer();
	}
}
