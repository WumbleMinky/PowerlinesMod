package gmail.wumbleminky.powerlines.item;

import gmail.wumbleminky.powerlines.help.Reference;
import gmail.wumbleminky.powerlines.tileentity.TileEntityPole;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemWireSpool extends Item {
	
	public static String ID = "itemWireSpool";

	public ItemWireSpool(){
		super();
		setCreativeTab(CreativeTabs.tabTools);
	    this.maxStackSize = 1;
	    this.setMaxDamage(256);
	    this.setHasSubtypes(true);
	    this.setUnlocalizedName("itemWireSpool");
	    this.setTextureName(Reference.MODID + ":itemSpool");
	    
	}
	
	@Override
    public boolean onItemUse (ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
		
		if (world.isRemote){
            return false;
        }
		
		TileEntityPole clicked_pole_te = (TileEntityPole) world.getTileEntity(x, y, z);
		if (clicked_pole_te != null){
			
			NBTTagCompound nbt = itemstack.getTagCompound();
			if (nbt == null) {
				nbt = new NBTTagCompound();
	            itemstack.stackTagCompound = nbt;
	        }
			
			int[] coords;
			if (nbt.hasKey("pole_coords")){
				coords = nbt.getIntArray("pole_coords");
				if (coords[0] != x || coords[1] != y || coords[2] != z){ //
					TileEntityPole stored_pole_te = (TileEntityPole) world.getTileEntity(coords[0], coords[1], coords[2]);
					if (clicked_pole_te != null && !clicked_pole_te.hasSameBasePole(stored_pole_te)){
						//Found new pole
						clicked_pole_te.connectPoles(stored_pole_te);
						//clicked_pole_te.setConnectedPole(stored_pole_te);
						//stored_pole_te.setConnectedPole(clicked_pole_te);
					}else{
						//Same Pole
					}
				}
				nbt.removeTag("pole_coords");
			}else{
				//Store New Pole information
				coords = new int[3];
				coords[0] = x;
				coords[1] = y;
				coords[2] = z;
				nbt.setIntArray("pole_coords", coords);
			}
			
		}
		
		return false;
		
    }
}
