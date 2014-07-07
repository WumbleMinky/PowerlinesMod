package gmail.wumbleminky.powerlines.block;

import gmail.wumbleminky.powerlines.help.BlockInfo;
import gmail.wumbleminky.powerlines.help.Reference;
import gmail.wumbleminky.powerlines.tileentity.TileEntityPole;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;


public class BlockPole extends BlockContainer {

	public BlockPole() {
		super(Material.wood);
		this.setBlockTextureName(Reference.MODID + ":blockLamp");
		setBlockBounds(0.25f, 0f, 0.25f, 0.75f, 1f, 0.75f);
		setCreativeTab(CreativeTabs.tabBlock);
		setBlockName("blockPole");
		
	}
	
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side){
		return true;
	}
	
	public boolean isOpaqueCube()
    {
        return false;
    }
	
	public boolean renderAsNormalBlock()
    {
        return false;
    }
	
	public boolean canProvidePower()
    {
		return true;
    }
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		//check the blocks above and below, and update the connected_coords for the TileEntity
		TileEntityPole te = (TileEntityPole) world.getTileEntity(x, y, z);
		TileEntityPole te_up = (TileEntityPole) world.getTileEntity(x, y + 1, z);
		TileEntityPole te_down = (TileEntityPole) world.getTileEntity(x, y - 1, z);
		if (te == null){
			return;
		}
		
		te.setBase(te);  //give the TE it's initial coords
		
		//THis could be more efficient
		if (te_up != null && te_down == null){
			te.updatePole();
			
		}else if(te_up == null && te_down != null){
			te.updatePole();
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta){
		TileEntityPole te = (TileEntityPole) world.getTileEntity(x, y, z);
		if (te != null){
			te.blockBroken();
		}
		super.breakBlock(world, x, y, z, block, meta);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par1, float par2, float par3, float par4){
		TileEntityPole te = (TileEntityPole) world.getTileEntity(x, y, z);
		if (te != null){
			if (te.base_pole != null){
				if (!world.isRemote){
					String strBase = te.base_pole.xCoord + "," + te.base_pole.yCoord + "," + te.base_pole.zCoord;
					boolean base = te.isBase();
					System.out.println("------------");
					System.out.println(te);
					if (!base){
						System.out.println("Base: " + strBase);
					}else{
						System.out.println("Base (Me): " + strBase);
					}
					System.out.println("Side: " + te.side);
					System.out.println("Redstone Input:" + te.redstone_input);
					if (te.getPole1() != null){
						System.out.println("Pole1: " + te.getPole1().xCoord + "," + te.getPole1().yCoord + "," + te.getPole1().zCoord + " = " + te.getPole1().redstone_output);
					}
					if (te.getPole2()!= null){
						System.out.println("Pole2: " + te.getPole2().xCoord + "," + te.getPole2().yCoord + "," + te.getPole2().zCoord + " = " + te.getPole2().redstone_output);
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
		TileEntityPole te = (TileEntityPole) world.getTileEntity(x, y, z);
		if (te != null){
			te.needs_update = true;
		}
	}
	
	public int isProvidingStrongPower(IBlockAccess block, int x, int y, int z, int side){
		if (ForgeDirection.getOrientation(side) == ForgeDirection.DOWN || ForgeDirection.getOrientation(side) == ForgeDirection.UP){
			return isProvidingWeakPower(block, x, y, z, side);
		}
		return 0;
	}
	
	public int isProvidingWeakPower(IBlockAccess block, int x, int y, int z, int side){
		TileEntityPole te = (TileEntityPole) block.getTileEntity(x, y, z);
		if (te != null){
			return te.getRedstoneOutput(side);
		}
		return 0;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityPole();
	}

}
