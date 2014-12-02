package com.republic.rowingmod.init;

import com.republic.rowingmod.entity.EntityRowingShell;
import com.republic.rowingmod.item.ItemRowingShell;
import com.republic.rowingmod.utility.LogHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

public class RowingModEventHandler
{
    @SubscribeEvent
    public void rotatePlayer(RenderPlayerEvent.Post event)
    {
        RenderPlayer renderer = event.renderer;
        EntityPlayer player = event.entityPlayer;

        renderer.modelArmorChestplate.heldItemLeft = 0;
        renderer.modelArmor.heldItemLeft = 0;
        renderer.modelBipedMain.heldItemLeft = 0;

        if(player.ridingEntity instanceof EntityRowingShell)
        {
            player.renderYawOffset = player.ridingEntity.rotationYaw+90;
            player.rotationYaw = player.ridingEntity.rotationYaw+90;
            renderer.modelBipedMain.isRiding=false;

        }
        else
        if(player.getCurrentEquippedItem() != null)
            if(player.getCurrentEquippedItem().getItem() instanceof ItemRowingShell)
            {
                int liftHeight = 9;
                renderer.modelArmorChestplate.heldItemLeft = liftHeight;
                renderer.modelArmor.heldItemLeft = liftHeight;
                renderer.modelBipedMain.heldItemLeft = liftHeight;
            }
    }

    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;

        if(event.type == RenderGameOverlayEvent.ElementType.TEXT)
        {
            if(player != null)
            {
                if(player.ridingEntity != null && player.ridingEntity instanceof EntityRowingShell)
                {
                    float[] speed = ((EntityRowingShell) player.ridingEntity).getSpeed();
                    String left = "Left: " + Float.toString(speed[0]);
                    String right = "Right: " + Float.toString(speed[1]);
                    String sp = "Speed: " + Float.toString(speed[2]);
                    mc.fontRenderer.drawStringWithShadow(left, 4, 4, 0xffFFFFFF);
                    mc.fontRenderer.drawStringWithShadow(right, 4, 12, 0xffFFFFFF);
                    mc.fontRenderer.drawStringWithShadow(sp, 4, 20, 0xffFFFFFF);

                }
            }
        }
    }
}
