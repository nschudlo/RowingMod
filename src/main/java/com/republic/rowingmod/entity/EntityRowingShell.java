package com.republic.rowingmod.entity;

import com.republic.rowingmod.utility.LogHelper;
import com.republic.rowingmod.utility.network.MessageOarKeyPressed;
import com.republic.rowingmod.utility.network.MessageOarsMoving;
import com.republic.rowingmod.utility.network.PacketHandler;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class EntityRowingShell extends EntityRowMod
{

    /** true if no player in boat */
    private boolean isBoatEmpty;
    private double speedMultiplier;
    private int boatPosRotationIncrements;
    private double boatX;
    private double boatY;
    private double boatZ;
    private double boatYaw;
    private double boatPitch;
    @SideOnly(Side.CLIENT)
    private double velocityX;
    @SideOnly(Side.CLIENT)
    private double velocityY;
    @SideOnly(Side.CLIENT)
    private double velocityZ;

    private float leftHeight, prevLeftHeight;
    private float leftRotation, prevLeftRotation;
    private float rightHeight, prevRightHeight;
    private float rightRotation, prevRightRotation;

    private boolean leftOarDown, rightOarDown, holdWater;

    Minecraft minecraft;

    float leftPower;
    float rightPower;
    float leftSpeed;
    float rightSpeed;

    EntityLivingBase camera;

    float trueYaw;



    public EntityRowingShell(World world)
    {
        super(world);
        this.isBoatEmpty = true;
        this.speedMultiplier = 0.07D;
        this.preventEntitySpawning = true;
        this.setSize(1.5F, 0.6F);
        this.yOffset = this.height / 2.0F;

        leftHeight=prevLeftHeight=-0.139623F;
        leftRotation=prevLeftRotation=0.0f;
        rightHeight=prevRightHeight=-0.139623F;
        rightRotation=prevRightRotation=0.0f;

        leftOarDown=rightOarDown=holdWater=false;

        leftPower = rightPower = leftSpeed = rightSpeed = 0.0f;
        trueYaw = this.rotationYaw;

    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(17, new Integer(0));
        this.dataWatcher.addObject(18, new Integer(1));
        this.dataWatcher.addObject(19, new Float(0.0F));

        this.dataWatcher.addObject(20, new Float(0.0F));//left rotation
        this.dataWatcher.addObject(21, new Float(0.0F));//left height
        this.dataWatcher.addObject(22, new Float(0.0F));//right rotation
        this.dataWatcher.addObject(23, new Float(0.0F));//right height

    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB getCollisionBox(Entity entity)
    {
        return entity.boundingBox;
    }

    /**
     * returns the bounding box for this entity
     */
    public AxisAlignedBB getBoundingBox()
    {
        return this.boundingBox;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return true;
    }

    public EntityRowingShell(World world, double x, double y, double z)
    {
        this(world);
        this.setPosition(x, y + (double)this.yOffset, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)this.height * 0.0D - 0.30000001192092896D;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float p_70097_2_)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else if (!this.worldObj.isRemote && !this.isDead)
        {
            this.setForwardDirection(-this.getForwardDirection());
            this.setTimeSinceHit(10);
            this.setDamageTaken(this.getDamageTaken() + p_70097_2_ * 10.0F);
            this.setBeenAttacked();
            boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;

            if (flag)// || this.getDamageTaken() > 40.0F)
            {
                if (this.riddenByEntity != null)
                {
                    this.riddenByEntity.mountEntity(this);
                }

                if (!flag)
                {
                    this.func_145778_a(Items.boat, 1, 0.0F);
                }
                this.setDead();

            }

            return true;
        }
        else
        {
            return true;
        }
    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    @SideOnly(Side.CLIENT)
    public void performHurtAnimation()
    {
        this.setForwardDirection(-this.getForwardDirection());
        this.setTimeSinceHit(10);
        this.setDamageTaken(this.getDamageTaken() * 11.0F);
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_)
    {
        if (this.isBoatEmpty)
        {
            this.boatPosRotationIncrements = p_70056_9_ + 5;
        }
        else
        {
            double d3 = p_70056_1_ - this.posX;
            double y = p_70056_3_ - this.posY;
            double d5 = p_70056_5_ - this.posZ;
            double d6 = d3 * d3 + y * y + d5 * d5;

            if (d6 <= 1.0D)
            {
                return;
            }

            this.boatPosRotationIncrements = 3;
        }

        this.boatX = p_70056_1_;
        this.boatY = p_70056_3_;
        this.boatZ = p_70056_5_;
        this.boatYaw = (double)p_70056_7_;
        this.boatPitch = (double)p_70056_8_;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    @SideOnly(Side.CLIENT)
    public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_)
    {
        this.velocityX = this.motionX = p_70016_1_;
        this.velocityY = this.motionY = p_70016_3_;
        this.velocityZ = this.motionZ = p_70016_5_;
    }

    public float[] getOarData()
    {
        float[] data = {getLeftRotation(),getLeftHeight(),getRightRotation(),getRightHeight()}; //{leftRotation,leftHeight,rightRotation,rightHeight};
        return data;
    }

    public void setOarData(boolean leftOar, boolean rightOar, boolean holdWater)
    {
        this.leftOarDown = leftOar;
        this.rightOarDown = rightOar;
        this.holdWater = holdWater;
    }



    public void upDateOars()
    {
        if(!this.holdWater)
        {
            if (this.rightOarDown) {
                if (leftRotation < 1.20F)
                    leftRotation += 0.05F;

                if (leftHeight < 0.0F)
                    leftHeight += 0.05F;
            } else {
                if (leftRotation > 0) {
                    leftRotation -= 0.05F;
                    leftSpeed += leftRotation;
                } else {
                    if (leftSpeed > 0)
                        leftSpeed -= 0.8;
                    else
                        leftSpeed = 0;
                }

                if (leftHeight > -0.139623F)
                    leftHeight -= 0.05F;
                else
                    leftHeight = -0.139623F;
            }

            if (this.leftOarDown) {
                if (rightRotation < 1.20F)
                    rightRotation += 0.05F;

                if (rightHeight < 0.0F)
                    rightHeight += 0.05F;
            } else {
                if (rightRotation > 0) {
                    rightRotation -= 0.05F;
                    rightSpeed += rightRotation;
                } else {
                    if (rightSpeed > 0)
                        rightSpeed -= 0.8;
                    else
                        rightSpeed = 0;
                }
                if (rightHeight > -0.139623F)
                    rightHeight -= 0.05F;
                else
                    rightHeight = -0.139623F;
            }
        }
        else {
            if (leftHeight > -0.15F)
                leftHeight -= 0.05F;
            if (rightHeight > -0.15F)
                rightHeight -= 0.05F;


            if (leftSpeed > 0)
                leftSpeed -= 1;
            else
                leftSpeed = 0;

            if (rightSpeed > 0)
                rightSpeed -= 1;
            else
                rightSpeed = 0;

            leftSpeed = 0;
            rightSpeed = 0;

        }
        this.dataWatcher.updateObject(20, Float.valueOf(leftRotation));
        this.dataWatcher.updateObject(21, Float.valueOf(leftHeight));
        this.dataWatcher.updateObject(22, Float.valueOf(rightRotation));
        this.dataWatcher.updateObject(23, Float.valueOf(rightHeight));



    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();


        if(this.worldObj.isRemote) //Client side
        {
            final Minecraft minecraft = Minecraft.getMinecraft();
            if(this.riddenByEntity != null)
            {
                final EntityPlayer rider = (EntityPlayer)this.riddenByEntity;

                final boolean isLocalPlayer = rider == minecraft.thePlayer;

                //If the current player is in the boat, then these controls will effect it
                if(isLocalPlayer)
                {
                    boolean leftDown = Keyboard.isKeyDown(minecraft.gameSettings.keyBindLeft.getKeyCode());
                    boolean rightDown = Keyboard.isKeyDown(minecraft.gameSettings.keyBindRight.getKeyCode());
                    boolean forwardDown = Keyboard.isKeyDown(minecraft.gameSettings.keyBindForward.getKeyCode());

                    if(minecraft.inGameHasFocus)//This prevents the ability to row while in guis and chatting...
                        PacketHandler.INSTANCE.sendToServer(new MessageOarKeyPressed(this.getEntityId(), leftDown, rightDown, forwardDown));
                }

                if (camera == null) {
                    camera = new EntityCamera(worldObj, this);
                    worldObj.spawnEntityInWorld(camera);
                }

                if(minecraft.gameSettings.thirdPersonView==0)
                    minecraft.renderViewEntity = minecraft.thePlayer;
                else
                    minecraft.renderViewEntity = camera;

            }
            else
            {
                minecraft.renderViewEntity = minecraft.thePlayer;
                camera = null;
            }



        }
        else //Server Side
        {
            upDateOars();
        }



        if (this.getTimeSinceHit() > 0)
        {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        if (this.getDamageTaken() > 0.0F)
        {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        byte b0 = 5;
        double d0 = 0.0D;

        for (int i = 0; i < b0; ++i)
        {
            double d1 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(i + 0) / (double)b0 - 0.125D;
            double d3 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(i + 1) / (double)b0 - 0.125D;
            AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(this.boundingBox.minX, d1, this.boundingBox.minZ, this.boundingBox.maxX, d3, this.boundingBox.maxZ);

            if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.water))
            {
                d0 += 1.0D / (double)b0;
            }
        }

        double d10 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        double x;
        double y;
        int j;

        /*
        //Calculations to add water particles
        if (d10 > 0.26249999999999996D)
        {
            x = Math.cos((double)this.rotationYaw * Math.PI / 180.0D);
            y = Math.sin((double)this.rotationYaw * Math.PI / 180.0D);

            for (j = 0; (double)j < 1.0D + d10 * 60.0D; ++j)
            {
                double d5 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
                double d6 = (double)(this.rand.nextInt(2) * 2 - 1) * 0.7D;
                double d8;
                double d9;

                if (this.rand.nextBoolean())
                {
                    d8 = this.posX - x * d5 * 0.8D + y * d6;
                    d9 = this.posZ - y * d5 * 0.8D - x * d6;
                    this.worldObj.spawnParticle("splash", d8, this.posY - 0.125D, d9, this.motionX, this.motionY, this.motionZ);
                }
                else
                {
                    d8 = this.posX + x + y * d5 * 0.7D;
                    d9 = this.posZ + y - x * d5 * 0.7D;
                    this.worldObj.spawnParticle("splash", d8, this.posY - 0.125D, d9, this.motionX, this.motionY, this.motionZ);
                }
            }
        }
        */
        double z;
        double d12;

        if (this.worldObj.isRemote && this.isBoatEmpty)
        {
            if (this.boatPosRotationIncrements > 0)
            {
                x = this.posX + (this.boatX - this.posX) / (double)this.boatPosRotationIncrements;
                y = this.posY + (this.boatY - this.posY) / (double)this.boatPosRotationIncrements;
                z = this.posZ + (this.boatZ - this.posZ) / (double)this.boatPosRotationIncrements;
                d12 = MathHelper.wrapAngleTo180_double(this.boatYaw - (double) this.rotationYaw);
                this.rotationYaw = (float)((double)this.rotationYaw + d12 / (double)this.boatPosRotationIncrements);
                this.rotationPitch = (float)((double)this.rotationPitch + (this.boatPitch - (double)this.rotationPitch) / (double)this.boatPosRotationIncrements);
                --this.boatPosRotationIncrements;

                this.setPosition(x, y, z);

                //Commented this out because all it does is modulo to 360;
                //this.setRotation(this.rotationYaw, this.rotationPitch);
            }
            else
            {
                x = this.posX + this.motionX;
                y = this.posY + this.motionY;
                z = this.posZ + this.motionZ;
                this.setPosition(x, y, z);

                if (this.onGround)
                {
                    this.motionX *= 0.5D;
                    this.motionY *= 0.5D;
                    this.motionZ *= 0.5D;
                }

                this.motionX *= 0.9900000095367432D;
                this.motionY *= 0.949999988079071D;
                this.motionZ *= 0.9900000095367432D;
            }
        }
        else
        {



            if (d0 < 1.0D)
            {
                x = d0 * 2.0D - 1.0D;
                this.motionY += 0.03999999910593033D * x;
            }
            else
            {
                if (this.motionY < 0.0D)
                {
                    this.motionY /= 2.0D;
                }

                this.motionY += 0.007000000216066837D;
            }

            if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase)
            {
                EntityLivingBase entitylivingbase = (EntityLivingBase)this.riddenByEntity;
                float f = this.riddenByEntity.rotationYaw;// + -entitylivingbase.moveStrafing * 90.0F;
                f = this.rotationYaw+90;


                this.motionX += -Math.sin((double) (f * (float) Math.PI / 180.0F)) * this.speedMultiplier * -leftSpeed/*(double)entitylivingbase.moveForward*/ * 0.05000000074505806D;
                this.motionZ += Math.cos((double) (f * (float) Math.PI / 180.0F)) * this.speedMultiplier * -leftSpeed/*(double)entitylivingbase.moveForward*/ * 0.05000000074505806D;



            }

            x = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (x > 0.35D)
            {
                y = 0.35D / x;
                this.motionX *= y;
                this.motionZ *= y;
                x = 0.35D;
            }

            if (x > d10 && this.speedMultiplier < 0.35D)
            {
                this.speedMultiplier += (0.35D - this.speedMultiplier) / 35.0D;

                if (this.speedMultiplier > 0.35D)
                {
                    this.speedMultiplier = 0.35D;
                }
            }
            else
            {
                this.speedMultiplier -= (this.speedMultiplier - 0.07D) / 35.0D;

                if (this.speedMultiplier < 0.07D)
                {
                    this.speedMultiplier = 0.07D;
                }
            }

            int l;

            for (l = 0; l < 4; ++l)
            {
                int i1 = MathHelper.floor_double(this.posX + ((double)(l % 2) - 0.5D) * 0.8D);
                j = MathHelper.floor_double(this.posZ + ((double)(l / 2) - 0.5D) * 0.8D);

                for (int j1 = 0; j1 < 2; ++j1)
                {
                    int k = MathHelper.floor_double(this.posY) + j1;
                    Block block = this.worldObj.getBlock(i1, k, j);

                    if (block == Blocks.snow_layer)
                    {
                        this.worldObj.setBlockToAir(i1, k, j);
                        this.isCollidedHorizontally = false;
                    }
                    else if (block == Blocks.waterlily)
                    {
                        this.worldObj.func_147480_a(i1, k, j, true);
                        this.isCollidedHorizontally = false;
                    }
                }
            }

            if (this.onGround)
            {
                this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
            }

            if(this.holdWater)
            {
                this.motionX *= 0.9D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.9D;
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);

            if (this.isCollidedHorizontally && d10 > 0.2D)
            {
                if (!this.worldObj.isRemote && !this.isDead)
                {

                    this.setDead();

                    for (l = 0; l < 3; ++l)
                    {
                        this.func_145778_a(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                    }

                    for (l = 0; l < 2; ++l)
                    {
                        this.func_145778_a(Items.stick, 1, 0.0F);
                    }
                }
            }
            else
            {
                this.motionX *= 0.9900000095367432D;
                this.motionY *= 0.949999988079071D;
                this.motionZ *= 0.9900000095367432D;
            }

            this.rotationPitch = 0.0F;
            y = (double)this.rotationYaw;
            z = this.prevPosX - this.posX;
            d12 = this.prevPosZ - this.posZ;

            if (z * z + d12 * d12 > 0.001D)
            {
                y = (double)((float)(Math.atan2(d12, z) * 180.0D / Math.PI));
            }

            double d7 = MathHelper.wrapAngleTo180_double(y - (double)this.rotationYaw);

            if (d7 > 20.0D)
            {
                d7 = 20.0D;
            }

            if (d7 < -20.0D)
            {
                d7 = -20.0D;
            }

            this.rotationYaw = (float)((double)this.rotationYaw + d7);

            EntityLivingBase rider = (EntityLivingBase)this.riddenByEntity;
            //Added this to keep boat aligned with rider
            if(this.riddenByEntity != null)
            {
                this.rotationYaw = this.riddenByEntity.rotationYaw-90.0f;
                // this.setRotation(rider.rotationYaw-90f, this.rotationPitch);
            }





            if (!this.worldObj.isRemote)
            {
                List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

                if (list != null && !list.isEmpty())
                {
                    for (int k1 = 0; k1 < list.size(); ++k1)
                    {
                        Entity entity = (Entity)list.get(k1);

                        if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityRowingShell)
                        {
                            entity.applyEntityCollision(this);
                        }
                    }
                }

                if (this.riddenByEntity != null && this.riddenByEntity.isDead)
                {
                    this.riddenByEntity = null;
                }
            }
        }

    }

    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
            float slideLength = Math.max(getLeftRotation(),getRightRotation());
            double d0 = (Math.cos((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D)*slideLength*-1.5; //added *slideLength*-1.5
            double d1 = (Math.sin((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D)*slideLength*-1.5; //-1.5 made the slide length longer
            this.riddenByEntity.setPosition(this.posX + d0, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d1);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {}

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {}

    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer p_130002_1_)
    {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != p_130002_1_)
        {
            return true;
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
                p_130002_1_.mountEntity(this);
            }

            return true;
        }
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground.  Args: distanceFallenThisTick, onGround
     */
    protected void updateFallState(double p_70064_1_, boolean p_70064_3_)
    {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.posY);
        int k = MathHelper.floor_double(this.posZ);

        if (p_70064_3_)
        {
            if (this.fallDistance > 3.0F)
            {
                this.fall(this.fallDistance);

                if (!this.worldObj.isRemote && !this.isDead)
                {
                    this.setDead();

                    int l;

                    for (l = 0; l < 3; ++l)
                    {
                        this.func_145778_a(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                    }

                    for (l = 0; l < 2; ++l)
                    {
                        this.func_145778_a(Items.stick, 1, 0.0F);
                    }
                }

                this.fallDistance = 0.0F;
            }
        }
        else if (this.worldObj.getBlock(i, j - 1, k).getMaterial() != Material.water && p_70064_1_ < 0.0D)
        {
            this.fallDistance = (float)((double)this.fallDistance - p_70064_1_);
        }
    }

    /**
     * Sets the damage taken from the last hit.
     */
    public void setDamageTaken(float p_70266_1_)
    {
        this.dataWatcher.updateObject(19, Float.valueOf(p_70266_1_));
    }

    /**
     * Gets the damage taken from the last hit.
     */
    public float getDamageTaken()
    {
        return this.dataWatcher.getWatchableObjectFloat(19);
    }

    /**
     * Sets the time to count down from since the last time entity was hit.
     */
    public void setTimeSinceHit(int p_70265_1_)
    {
        this.dataWatcher.updateObject(17, Integer.valueOf(p_70265_1_));
    }

    /**
     * Gets the time since the last hit.
     */
    public int getTimeSinceHit()
    {
        return this.dataWatcher.getWatchableObjectInt(17);
    }

    /**
     * Sets the forward direction of the entity.
     */
    public void setForwardDirection(int p_70269_1_)
    {
        this.dataWatcher.updateObject(18, Integer.valueOf(p_70269_1_));
    }

    /**
     * Gets the forward direction of the entity.
     */
    public int getForwardDirection()
    {
        return this.dataWatcher.getWatchableObjectInt(18);
    }

    public float getLeftRotation()
    {
        return this.dataWatcher.getWatchableObjectFloat(20);
    }

    public float getLeftHeight()
    {
        return this.dataWatcher.getWatchableObjectFloat(21);
    }

    public float getRightRotation()
    {
        return this.dataWatcher.getWatchableObjectFloat(22);
    }

    public float getRightHeight()
    {
        return this.dataWatcher.getWatchableObjectFloat(23);
    }

    /**
     * true if no player in boat
     */
    @SideOnly(Side.CLIENT)
    public void setIsBoatEmpty(boolean p_70270_1_)
    {
        this.isBoatEmpty = p_70270_1_;
    }

    @SideOnly(Side.CLIENT)
    public EntityLivingBase getCamera()
    {
        return camera;
    }

    public float getFacingDirection()
    {
        // return (this.rotationYaw%360 < 0 ? 360 + (this.rotationYaw) : this.rotationYaw)%360;
        //return this.rotationYaw+90;

        //This calculation maps Minecraft0 to Regular0, Minecraft-90 to Regular90, Minecraft90 to Regular270
        //The Minecraft plane uses Z+ at 0 and X+ at 90
        return (360 - (this.rotationYaw+90)) %360;
    }

    public float getTrueYaw()
    {
        return trueYaw;
    }

}
