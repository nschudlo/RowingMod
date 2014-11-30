package com.republic.rowingmod.renderer;


import com.republic.rowingmod.model.ModelRowingShell;
import com.republic.rowingmod.reference.Names;
import com.republic.rowingmod.reference.Reference;
import com.republic.rowingmod.utility.LogHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;

public class ItemRenderShell implements IItemRenderer {

    protected ModelRowingShell modelShell;
    private static final ResourceLocation shellTexture = new ResourceLocation(Reference.MOD_ID + ":" + Names.Models.SHELL_MODEL);

    public ItemRenderShell()
    {
        modelShell = new ModelRowingShell(false);
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        switch(type)
        {
            case EQUIPPED: return true;
            case EQUIPPED_FIRST_PERSON: return true;
            default: return false;
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        switch(type)
        {
            case EQUIPPED:
            {
                GL11.glPushMatrix();
                Minecraft.getMinecraft().renderEngine.bindTexture(this.shellTexture);

                //Initial Line up of boat on the player head
                GL11.glRotatef(140f,0.0f, 0.0f, 1.0f);
                GL11.glRotatef(-10f,1.0f,0.0f,0.0f);
                GL11.glTranslatef(-1.0f,.2f,0.3f);

                EntityPlayer player = (EntityPlayer) data[1];

                float swing = 0;

                //Used to access the private timer field in Minecraft class
                //This is needed to recalculate the arm swing angle of the player
                try{
                    Field field = Minecraft.getMinecraft().getClass().getDeclaredField("timer");
                    field.setAccessible(true);
                    Timer timer = (Timer) field.get(Minecraft.getMinecraft());

                    //Values calculated in RenderLivingEntity class - doRender
                    float f6 = player.prevLimbSwingAmount + (player.limbSwingAmount - player.prevLimbSwingAmount) * timer.renderPartialTicks;
                    float f7 = player.limbSwing - player.limbSwingAmount * (1.0F - timer.renderPartialTicks);

                    //Calculations in the ModelBiped class
                    swing = MathHelper.cos(f7 * 0.6662F + (float) Math.PI) * 2.0F * f6 * 0.5F;
                    swing = swing * 0.5F - ((float)Math.PI / 10F);
                    swing += MathHelper.sin((player.ticksExisted + timer.renderPartialTicks) * 0.067F) * 0.05F;

                    if(player.isSneaking())
                        swing += 0.4;

                }catch(Exception e)
                {
                    LogHelper.info("Getting Minecraft.timer threw an exception: " + e);
                }

                //Rotation and translation to offset the arm swing
                GL11.glRotatef(swing*60, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef(0.0f,-swing,0.0f);

                //Render the boat
                this.modelShell.render((Entity) data[1], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);

                GL11.glPopMatrix();
                break;
            }
            case EQUIPPED_FIRST_PERSON:
            {
                GL11.glPushMatrix();
                Minecraft.getMinecraft().renderEngine.bindTexture(this.shellTexture);

                //Initial alignment
                GL11.glRotatef(35f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef(-1.1f, 1f, -1.0f);


                EntityClientPlayerMP player = (EntityClientPlayerMP)data[1];

                //Calculation and adjustments to ignore the player head movement
                GL11.glRotatef(-player.rotationPitch,0.0f,0.0f,-0.5f);
                GL11.glTranslatef(0.0f,-player.rotationPitch/80,0.0f);

                this.modelShell.render((Entity) data[1], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);

                GL11.glPopMatrix();
                break;
            }

            default:
                break;
        }
    }
}
