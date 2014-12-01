package com.republic.rowingmod.init;

import com.republic.rowingmod.entity.EntityRowingShell;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class RowingModEventHandler
{
    @SubscribeEvent
    public void rotatePlayer(RenderPlayerEvent.Specials.Post event)
    {
        RenderPlayer renderer = event.renderer;
        EntityPlayer player = event.entityPlayer;

        if(player.ridingEntity instanceof EntityRowingShell) {
                player.rotationYaw = player.ridingEntity.rotationYaw+90;
                renderer.modelBipedMain.isRiding=false;

            }

    }
}
