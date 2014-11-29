package com.republic.rowingmod.proxy;


import com.republic.rowingmod.client.settings.Keybindings;
import com.republic.rowingmod.entity.EntityRowingShell;
import com.republic.rowingmod.init.ModItems;
import com.republic.rowingmod.item.ItemRowingShell;
import com.republic.rowingmod.renderer.ItemRenderShell;
import com.republic.rowingmod.renderer.RendererRowingShell;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy
{

    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityRowingShell.class, new RendererRowingShell());
        MinecraftForgeClient.registerItemRenderer(ModItems.rowingShell ,new ItemRenderShell());

    }

    @Override
    public void registerKeyBindings() {
        //ClientRegistry.registerKeyBinding(Keybindings.port);
        //ClientRegistry.registerKeyBinding(Keybindings.starboard);
        ClientRegistry.registerKeyBinding(Keybindings.boatperspective);
    }

}
