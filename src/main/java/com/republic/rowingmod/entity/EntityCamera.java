package com.republic.rowingmod.entity;

import com.republic.rowingmod.client.settings.Keybindings;
import com.republic.rowingmod.utility.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;


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

    boolean pressing;
    boolean toggleFreemode = false;
    boolean toggleFreemodePressed = false;

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
        minecraft = Minecraft.getMinecraft();
        prevView=minecraft.gameSettings.thirdPersonView;

        togglePerspectiveOriginal = minecraft.gameSettings.keyBindTogglePerspective.getKeyCode();
        original = minecraft.gameSettings.keyBindTogglePerspective;


        posX = d.posX;
        posZ = d.posZ;
        posY = d.posY;
    }


    private void blockPerspectiveKey()
    {
        //Check if the binding has already been changed
        if(minecraft.inGameHasFocus && minecraft.gameSettings.keyBindTogglePerspective != Keybindings.boatperspective)
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
        if(minecraft.gameSettings.keyBindTogglePerspective == Keybindings.boatperspective)
        {
            LogHelper.info("Switching Back");
            minecraft.gameSettings.keyBindTogglePerspective = original;
            // Keybindings.boatperspective.setKeyCode(original.getKeyCode()); //Seems to cause problems when set to the same
            KeyBinding.unPressAllKeys();
        }
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
        if(++currentView==7)
            currentView=0;

        if(currentView==0)
        {
            boat.riddenByEntity.rotationPitch=20;
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

        prev[0] = (float)posX;
        prev[1] = (float)posY;
        prev[2] = (float)posZ;
        prev[3] = rotationYaw;
        prev[4] = rotationPitch;

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;


        int radius = 5;


        if(!minecraft.inGameHasFocus)
        {
            //If player goes to main menu, set the key back to its original
            revertPerspectiveKey();
        }
        else
        {
            //Check if toggle perspective key is being pressed. Need to release to press again
            if(Keyboard.isKeyDown(original.getKeyCode()))
            {
                if(!pressing)
                {
                    pressing = true;
                    LogHelper.info("Toggle Perspective Pressed. Current view: " + Keyboard.getKeyName(original.getKeyCode()));
                    cycleView();
                }
            }
            else
                pressing = false;


            if (Keyboard.isKeyDown(minecraft.gameSettings.keyBindBack.getKeyCode()))
            {
                if(!toggleFreemodePressed) {
                    toggleFreemodePressed = true;

                    toggleFreemode = !toggleFreemode;
                }
            }
            else
                toggleFreemodePressed=false;

            if(!toggleFreemode)
            {
                switch(currentView)
                {
                    case 1: setGoals(radius, 180, 0.5f, 90, 15f); break;
                    case 2: setGoals(radius, 0, 0.5f, -90, 15); break;
                    case 3: setGoals(radius, 270, -0.5f, 0, -15); break;
                    case 4: setGoals(radius, 90, -0.5f, 180, -15); break;
                    case 5: setGoals(-1,0,-0.5f,-100,-15);break;
                    case 6: setGoals(2,180,-1f,100,-20);break;

                    default:
                        setGoals(3,0,0,90,0);
                        goalY = (float) boat.riddenByEntity.posY - boat.riddenByEntity.yOffset;
                        goalPitch = 0;


                        if(goalZ-0.5 < posZ && posZ< goalZ+0.5)
                            if(goalY-0.2 < posY && posY < goalY+0.2)
                                if (goalX - 0.5 < posX && posX < goalX + 0.5)
                                    minecraft.gameSettings.thirdPersonView=0;
                }
            }
            else
            {
                goalX = (float) boat.riddenByEntity.posX;
                goalY = (float) boat.riddenByEntity.posY - boat.riddenByEntity.yOffset;
                goalZ = (float) boat.riddenByEntity.posZ;

                goalYaw = boat.rotationYaw - 90;
                goalPitch = -boat.riddenByEntity.rotationPitch;
            }
        }


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

    private void setGoals(int radius, float angleOff, float yOff, float yawOff, float pitchOff)
    {
        goalZ = (float) (boat.posZ + radius * Math.cos(Math.toRadians(boat.getFacingDirection() + angleOff)));
        goalX = (float) (boat.posX + radius * Math.sin(Math.toRadians(boat.getFacingDirection() + angleOff)));
        goalY = (float) boat.posY + yOff;

        goalYaw = boat.rotationYaw + yawOff;
        goalPitch = boat.rotationPitch + pitchOff;
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