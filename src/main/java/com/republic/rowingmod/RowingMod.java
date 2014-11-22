package com.republic.rowingmod;

import com.republic.rowingmod.init.ModEntities;
import com.republic.rowingmod.init.ModItems;
import com.republic.rowingmod.proxy.IProxy;
import com.republic.rowingmod.reference.Reference;
import com.republic.rowingmod.utility.network.PacketHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid= Reference.MOD_ID, name=Reference.MOD_NAME, version=Reference.VERSION)
public class RowingMod
{
    @Mod.Instance(Reference.MOD_ID)
    public static RowingMod instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS,serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {

        ModItems.init();
        ModEntities.init();

        proxy.registerRenderers();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        PacketHandler.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {

    }

}
