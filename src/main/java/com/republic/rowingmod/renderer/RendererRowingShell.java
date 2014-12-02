package com.republic.rowingmod.renderer;

import com.republic.rowingmod.entity.EntityRowingShell;
import com.republic.rowingmod.model.ModelRowingShell;
import com.republic.rowingmod.reference.Names;
import com.republic.rowingmod.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RendererRowingShell extends Render
{
    private static final ResourceLocation shellTexture = new ResourceLocation(Reference.MOD_ID + ":" + Names.Models.SHELL_MODEL);

    protected ModelRowingShell shellModel;

    public RendererRowingShell ()
    {
        shellModel = new ModelRowingShell();
        this.shadowSize = 0.5f;
    }


    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float something)
    {
        EntityRowingShell rowingShell = (EntityRowingShell) entity;

        float[] oarData = rowingShell.getOarData();
        this.shellModel.setOars(oarData[0],oarData[1],oarData[2],oarData[3]);

        GL11.glPushMatrix();
        final float var24 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * something;
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(180.0F - f, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-var24, 0.0F, 0.0F, 1.0F);

        GL11.glRotatef(180,1.0f,0.0f,0.0F);

        this.bindTexture(RendererRowingShell.shellTexture);
        this.shellModel.render(entity, 0.0f,0.0f,0.0f,0.0f,0.0f,0.0625f);

        GL11.glPopMatrix();

        for(int rad=2; rad < 20; rad++) {
            float goalZ = (float) (rowingShell.posZ + rad * Math.cos(Math.toRadians(rowingShell.getFacingDirection() + 0)));
            float goalX = (float) (rowingShell.posX + rad * Math.sin(Math.toRadians(rowingShell.getFacingDirection() + 0)));
            Minecraft.getMinecraft().theWorld.spawnParticle("reddust", goalX, 19, goalZ, 0, 0, 0);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return shellTexture;
    }
}
