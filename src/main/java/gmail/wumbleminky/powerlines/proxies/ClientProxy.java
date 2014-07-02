package gmail.wumbleminky.powerlines.proxies;

import gmail.wumbleminky.powerlines.Powerlines;
import gmail.wumbleminky.powerlines.render.LampItemRenderer;
import gmail.wumbleminky.powerlines.render.LampTileEntityRenderer;
import gmail.wumbleminky.powerlines.tileentity.TileEntityLamp;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

	
	public void registerRenderers(){
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLamp.class, new LampTileEntityRenderer());
		ClientRegistry.registerTileEntity(TileEntityLamp.class, "blockLamp", new LampTileEntityRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Powerlines.blockLamp), new LampItemRenderer());
	}
}
