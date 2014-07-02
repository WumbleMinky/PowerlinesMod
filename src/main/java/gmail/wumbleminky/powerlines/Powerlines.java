package gmail.wumbleminky.powerlines;

import gmail.wumbleminky.powerlines.block.BlockLamp;
import gmail.wumbleminky.powerlines.block.BlockLampSpacer;
import gmail.wumbleminky.powerlines.block.BlockPole;
import gmail.wumbleminky.powerlines.help.ForgeHelper;
import gmail.wumbleminky.powerlines.help.Reference;
import gmail.wumbleminky.powerlines.item.ItemBlockLamp;
import gmail.wumbleminky.powerlines.item.ItemWireSpool;
import gmail.wumbleminky.powerlines.proxies.CommonProxy;
import gmail.wumbleminky.powerlines.tileentity.TileEntityLamp;
import gmail.wumbleminky.powerlines.tileentity.TileEntityLampSpacer;
import gmail.wumbleminky.powerlines.tileentity.TileEntityPole;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Reference.MODID, version = Reference.VERSION)
public class Powerlines
{
    
    public static Block blockLamp;
    public static Block blockLampLit;
    public static Block lampSpacer;
    public static Block blockPole;
    
    public static Item itemWireSpool;
    
    public static ItemBlock itemBlockLamp;
    
    public static int LampRenderType;
    
    @SidedProxy(clientSide="gmail.wumbleminky.powerlines.proxies.ClientProxy", serverSide="gmail.wumbleminky.powerlines.proxies.CommonProxy")
	public static CommonProxy proxy;
    
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		blockPole = new BlockPole();
    	blockLamp = new BlockLamp(false);
		blockLampLit = new BlockLamp(true);
		lampSpacer = new BlockLampSpacer();
		
		itemBlockLamp = new ItemBlockLamp(blockLamp);
		itemWireSpool = new ItemWireSpool();
		
		LampRenderType = RenderingRegistry.getNextAvailableRenderId();
		
		ForgeHelper.registerBlock(blockLamp, ItemBlockLamp.class);
		ForgeHelper.registerBlock(blockLampLit);
		ForgeHelper.registerBlock(lampSpacer);
		ForgeHelper.registerBlock(blockPole);
		
		GameRegistry.registerItem(itemWireSpool, ItemWireSpool.ID);
		GameRegistry.registerTileEntity(TileEntityLamp.class, "LampTE");
		GameRegistry.registerTileEntity(TileEntityPole.class, TileEntityPole.ID);
		GameRegistry.registerTileEntity(TileEntityLampSpacer.class, TileEntityLampSpacer.ID);
		proxy.registerRenderers();
		
    }
    
    @EventHandler
    public void load(FMLInitializationEvent event) {
            //proxy.registerRenderers();
            //GameRegistry.registerTileEntity(TileEntityLamp.class, "lampTileEntityRenderer");
    }
    
}
