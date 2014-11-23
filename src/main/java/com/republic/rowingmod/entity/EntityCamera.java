package com.republic.rowingmod.entity;

import com.republic.rowingmod.client.settings.Keybindings;
import com.republic.rowingmod.reference.Reference;
import com.republic.rowingmod.utility.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class EntityCamera extends EntityLivingBase
{
    public EntityRowingShell boat;
    Minecraft minecraft;

    float goalX,goalY,goalZ;
    float goalYaw, goalPitch;
    int prevView;
    float[] prev = {0.0f,0.0f,0.0f,0.0f,0.0f};
    int togglePerspectiveOriginal;

    KeyBinding original;

    int currentView=0;

    int countdown;
    boolean pressing;

    public EntityCamera(World world)
    {
        super(world);
        setSize(0F, 0F);
        minecraft = Minecraft.getMinecraft();
        prevView=minecraft.gameSettings.thirdPersonView;
    }

    public EntityCamera(World world, EntityRowingShell d)
    {
        this(world);
        boat = d;
        //setPosition(d.posX, d.posY, d.posZ);
        minecraft = Minecraft.getMinecraft();
        prevView=minecraft.gameSettings.thirdPersonView;
        countdown = 0;

        togglePerspectiveOriginal = minecraft.gameSettings.keyBindTogglePerspective.getKeyCode();
        original = minecraft.gameSettings.keyBindTogglePerspective;
    }


    private void blockPerspectiveKey()
    {
        //Check if the binding has already been changed
        if(minecraft.gameSettings.keyBindTogglePerspective != Keybindings.boatperspective)
        {
            LogHelper.info("Switching");
            //If so, save the original and change to the dummy one
            original = minecraft.gameSettings.keyBindTogglePerspective;
            minecraft.gameSettings.keyBindTogglePerspective = Keybindings.boatperspective;
        }
    }

    private void revertPerspectiveKey()
    {
        //Set keybind back to normal
        LogHelper.info("Switching Back");
        minecraft.gameSettings.keyBindTogglePerspective = original;
        Keybindings.boatperspective.setKeyCode(original.getKeyCode());
        KeyBinding.unPressAllKeys();
    }

    //This function shifts player yaw by 360 until its within 360 of boat yaw
    private float makeYawCatchUp(float playerYaw, float boatYaw)
    {
        float newYaw = playerYaw;

        while(newYaw < boatYaw-180)
            newYaw += 360;
        while(newYaw > boatYaw+180)
            newYaw -= 360;

        return newYaw;
    }

    private void cycleView()
    {
        if(++currentView==3)
            currentView=0;

        if(currentView==0)
        {
            boat.riddenByEntity.rotationPitch=0;
        }
        else
        if(currentView==1)
        {
            boat.riddenByEntity.rotationPitch = 50;

            posX = boat.riddenByEntity.posX;
            posY = boat.riddenByEntity.posY-boat.riddenByEntity.yOffset;
            posZ = boat.riddenByEntity.posZ;

            rotationYaw = makeYawCatchUp(boat.riddenByEntity.rotationYaw,boat.rotationYaw);
            rotationPitch = boat.riddenByEntity.rotationPitch;
            minecraft.gameSettings.thirdPersonView = 1;

        }


        LogHelper.info("CurrentView: " + currentView);
    }

    @Override
    public void onUpdate()
    {
        if(boat.isDead) {
            Minecraft.getMinecraft().renderViewEntity = Minecraft.getMinecraft().thePlayer;
            revertPerspectiveKey();
            this.setDead();
        }

        if(boat.riddenByEntity==null)
        {
            revertPerspectiveKey();
            this.setDead();
            return;
        }
        else
        {
            blockPerspectiveKey();
        }



       /* if(prevView != minecraft.gameSettings.thirdPersonView || countdown != 0)
        {
            //If countdown is set to 2 or more this will run again
           // if(countdown == 0)
                countdown=20;

            setPosition(prev[0],prev[1],prev[2]);
            setRotation(prev[3]+160,prev[4]-30);
            prevView = minecraft.gameSettings.thirdPersonView;
            countdown--;
        }
        else*/
       {
        prev[0] = (float)posX;
        prev[1] = (float)posY;
        prev[2] = (float)posZ;
        prev[3] = rotationYaw;
        prev[4] = rotationPitch;

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
    }

        int radius = 5;


        if(!minecraft.inGameHasFocus)
        {
            //If player goes to main menu, set the key back to its original
            revertPerspectiveKey();
        }
        else
        {
            //Check if toggle key is being pressed. Need to release to press again
            if(Keyboard.isKeyDown(original.getKeyCode()))
            {
                if(!pressing)
                {
                    pressing = true;
                    LogHelper.info("Toggle Perspective Pressed " + Keyboard.getKeyName(original.getKeyCode()));
                    cycleView();
                }
            }
            else
                pressing = false;


            if (!Keyboard.isKeyDown(minecraft.gameSettings.keyBindBack.getKeyCode()))
            {
                if (currentView == 1)//Behind
                {
                    goalZ = (float) (boat.posZ + radius * Math.cos(Math.toRadians(boat.getFacingDirection() - 180)));
                    goalX = (float) (boat.posX + radius * Math.sin(Math.toRadians(boat.getFacingDirection() - 180)));
                    goalY = (float) boat.posY + 0.5f;

                    goalYaw = boat.rotationYaw + 90;
                    goalPitch = boat.rotationPitch + 15F;

                } else if (currentView == 2)//In Front
                {
                    goalZ = (float) (boat.posZ + radius * Math.cos(Math.toRadians(boat.getFacingDirection())));
                    goalX = (float) (boat.posX + radius * Math.sin(Math.toRadians(boat.getFacingDirection())));
                    goalY = (float) boat.posY + 0.5f;

                    goalYaw = boat.rotationYaw - 90;
                    goalPitch = boat.rotationPitch + 15F;
                } else//First Person
                {
                    goalZ = (float) (boat.riddenByEntity.posZ + 4.4f * Math.cos(Math.toRadians(boat.getFacingDirection())));
                    goalX = (float) (boat.riddenByEntity.posX + 4.4f * Math.sin(Math.toRadians(boat.getFacingDirection())));

                    goalY = (float) boat.riddenByEntity.posY - boat.riddenByEntity.yOffset;

                    goalYaw = boat.rotationYaw+90;


                    goalPitch = 0;//boat.riddenByEntity.rotationPitch;

                    if(goalZ-0.1 < posZ && posZ< goalZ+0.1)
                        if(goalY-0.2 < posY && posY < goalY+0.2) {
                            if (goalX - 0.1 < posX && posX < goalX + 0.1) {
                                minecraft.gameSettings.thirdPersonView=0;

                            }
                        }
                }
            }
            else
            {
                goalX = (float) boat.riddenByEntity.posX;
                goalY = (float) boat.riddenByEntity.posY - boat.riddenByEntity.yOffset;
                goalZ = (float) boat.riddenByEntity.posZ;

                goalYaw = boat.rotationYaw + 90;
                goalPitch = boat.riddenByEntity.rotationPitch;
            }
        }

        //if(currentView!=1)
        {
            int div = 8;


            if (this.posX < goalX - 0.01)
                posX += (goalX - posX) / div;
            else if (this.posX > goalX + 0.01)
                posX -= (posX - goalX) / div;

            if (this.posY < goalY - 0.2)
                posY += (goalY - posY) / div;
            else if (this.posY > goalY + 0.2)
                posY -= (posY - goalY) / div;

            if (this.posZ < goalZ - 0.01)
                posZ += (goalZ - posZ) / div;
            else if (this.posZ > goalZ + 0.01)
                posZ -= (posZ - goalZ) / div;


            if (this.rotationYaw < goalYaw - 0.01)
                rotationYaw += ((goalYaw - rotationYaw) / div);
            else if (this.rotationYaw > goalYaw + 0.01)
                rotationYaw -= ((rotationYaw - goalYaw) / div);

            if (this.rotationPitch < goalPitch - 0.01)
                rotationPitch += (goalPitch - rotationPitch) / div;
            else if (this.rotationPitch > goalPitch + 0.01)
                rotationPitch -= (rotationPitch - goalPitch) / div;

        }
        //setPosition(goalX,goalY,goalZ);
        //rotationYaw=goalYaw;
        //rotationPitch=goalPitch;

        //for(; rotationYaw - prevRotationYaw >= 180F; rotationYaw -= 360F) ;
        //for(; rotationYaw - prevRotationYaw < -180F; rotationYaw += 360F) ;
    }

    @Override
    public ItemStack getHeldItem()
    {
        return null;
    }

    @Override
    public ItemStack getEquipmentInSlot(int p_71124_1_)
    {
        return null;
    }

    @Override
    public void setCurrentItemOrArmor(int p_70062_1_, ItemStack p_70062_2_)
    {

    }

    @Override
    public ItemStack[] getLastActiveItems()
    {
        return null;
    }


}