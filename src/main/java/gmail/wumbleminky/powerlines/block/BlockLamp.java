package gmail.wumbleminky.powerlines.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gmail.wumbleminky.powerlines.Powerlines;
import gmail.wumbleminky.powerlines.help.Reference;
import gmail.wumbleminky.powerlines.tileentity.TileEntityLamp;
import gmail.wumbleminky.powerlines.tileentity.TileEntityLampSpacer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLamp extends BlockContainer {
	
	public boolean lit;
	
	public BlockLamp(boolean lit){
		super(Material.rock);
		this.lit = lit;
				
		this.setBlockBounds(0.0f, 0f, 0f, 1f, 2f, 1f);
		this.setHarvestLevel("shovel", 1);
		if (lit){
			this.setLightLevel(1.0f);
			setBlockName("blockLampLit");
			this.setTickRandomly(true);
		}else{
			setBlockName("blockLamp");
			setCreativeTab(CreativeTabs.tabBlock);
		}
		
	}
	
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random){
		if (this.lit){
			world.spawnParticle("smoke", (double)x + 0.5, (double)y + 1.5, (double)z + 0.5, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("flame", (double)x + 0.5, (double)y + 1.5, (double)z + 0.5, 0.0D, 0.0D, 0.0D);
		}
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
		if (this.lit && !world.isBlockIndirectlyGettingPowered(x, y, z)) {
			world.scheduleBlockUpdate(x, y, z, this, 4);
		}else if(!this.lit && world.isBlockIndirectlyGettingPowered(x, y, z)){
			world.setBlock(x, y, z, Powerlines.blockLampLit, 0, 2);
		}
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
    {
		if (this.lit && !world.isBlockIndirectlyGettingPowered(x, y, z)){
			world.setBlock(x, y, z, Powerlines.blockLamp, 0, 2);
		}
    }
	
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side){
		return true;
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		super.onBlockAdded(world, x, y, z);
		boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z);
		if (this.lit && !powered){
			world.scheduleBlockUpdate(x, y, z, this, 4);
		}else if(!this.lit && powered){
			world.setBlock(x, y, z, Powerlines.blockLampLit, 0, 2);
		}
		
		world.setBlock(x, y +1 , z, Powerlines.lampSpacer);
		TileEntityLampSpacer te_spacer = (TileEntityLampSpacer)world.getTileEntity(x, y+1, z);
		if (te_spacer != null){
			te_spacer.primary_x = x;
			te_spacer.primary_y = y;
			te_spacer.primary_z = z;
		}
	}
	
	@Override
    public int getRenderType() {
            return Powerlines.LampRenderType;
    }
	
	public boolean isOpaqueCube()
    {
        return false;
    }
	
	public boolean renderAsNormalBlock()
    {
        return false;
    }

	@Override
	public TileEntity createNewTileEntity(World world, int var2) {
		return new TileEntityLamp();
	}
	
	public void registerBlockIcons(IIconRegister icon) {
        this.blockIcon = icon.registerIcon(Reference.MODID + ":blockLamp");
	}
	
}
