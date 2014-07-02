package gmail.wumbleminky.powerlines.render;

import org.lwjgl.opengl.GL11;

import gmail.wumbleminky.powerlines.tileentity.TileEntityLamp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class LampItemRenderer implements IItemRenderer {

    public LampItemRenderer() {
    	
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        TileEntityRendererDispatcher.instance.renderTileEntityAt(new TileEntityLamp(), 0, 0, 0, 0);
    }
    
}
