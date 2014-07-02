package gmail.wumbleminky.powerlines.help;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.registry.GameRegistry;

public class ForgeHelper {

	public static void registerBlock(Block block){
		GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));
	}
	
	public static void registerBlock(Block block, Class<?extends ItemBlock> itemclass){
		GameRegistry.registerBlock(block, itemclass, block.getUnlocalizedName().substring(5));
	}
	
}
