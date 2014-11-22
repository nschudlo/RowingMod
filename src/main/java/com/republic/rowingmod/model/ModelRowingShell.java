package com.republic.rowingmod.model;

        import com.republic.rowingmod.utility.LogHelper;
        import net.minecraft.client.Minecraft;
        import net.minecraft.client.model.ModelBase;
        import net.minecraft.client.model.ModelRenderer;
        import net.minecraft.entity.Entity;
        import org.lwjgl.input.Keyboard;
        import org.lwjgl.opengl.GL11;

public class ModelRowingShell extends ModelBase
{
    //fields
    ModelRenderer Hull;
    ModelRenderer Bow;
    ModelRenderer Stern;
    ModelRenderer RiggerMiddle;
    ModelRenderer RiggerPort;
    ModelRenderer RiggerStar;
    ModelRenderer OarPort;
    ModelRenderer BladePort;
    ModelRenderer OarStarboard;
    ModelRenderer BladeStarboard;

    float leftRotation = 0.0f;
    float leftHeight = -0.1396263F;
    float rightRotation = 0.0f;
    float rightHeight = -0.1396263F;


    public ModelRowingShell()
    {
        textureWidth = 128;
        textureHeight = 128;


        Hull = new ModelRenderer(this, 0, 17);
        Hull.addBox(0F, 0F, 0F, 40, 6, 16);
        Hull.setRotationPoint(0F, 0F, 0F);
        Hull.setTextureSize(128, 128);
        Hull.mirror = true;
        setRotation(Hull, 0F, 0F, 0F);
        Bow = new ModelRenderer(this, 0, 0);
        Bow.addBox(0F, 0F, 0F, 11, 6, 11);
        Bow.setRotationPoint(0F, 0F, 0F);
        Bow.setTextureSize(128, 128);
        Bow.mirror = true;
        setRotation(Bow, 0F, -0.7853982F, 0F);
        Stern = new ModelRenderer(this, 0, 0);
        Stern.addBox(0F, 0F, 0F, 11, 6, 11);
        Stern.setRotationPoint(40F, 0F, 0F);
        Stern.setTextureSize(128, 128);
        Stern.mirror = true;
        setRotation(Stern, 0F, -0.7853982F, 0F);

        //Riggers
        RiggerMiddle = new ModelRenderer(this, 0, 41);
        RiggerMiddle.addBox(0F, 0F, 0F, 3, 2, 16);
        RiggerMiddle.setRotationPoint(34F, -2F, 0F);
        RiggerMiddle.setTextureSize(128, 128);
        RiggerMiddle.mirror = true;
        setRotation(RiggerMiddle, 0F, 0F, 0F);
        RiggerPort = new ModelRenderer(this, 0, 39);
        RiggerPort.addBox(0F, 0F, 0F, 3, 2, 18);
        RiggerPort.setRotationPoint(37F, -2F, 0F);
        RiggerPort.setTextureSize(128, 128);
        RiggerPort.mirror = true;
        setRotation(RiggerPort, 0F, -2.399879F, 0F);
        RiggerStar = new ModelRenderer(this, 0, 39);
        RiggerStar.addBox(0F, 0F, 0F, 3, 2, 18);
        RiggerStar.setRotationPoint(35F, -2F, 14F);
        RiggerStar.setTextureSize(128, 128);
        RiggerStar.mirror = true;
        setRotation(RiggerStar, 0F, -0.8116305F, 0F);

        //Oars
//        OarPort = new ModelRenderer(this, 0, 39);
//        OarPort.addBox(0F, 0F, 0F, 1, 1, 51);
//        OarPort.setRotationPoint(15F, -6F, 0F);
//        OarPort.setTextureSize(128, 128);
//        OarPort.mirror = true;
//        setRotation(OarPort, -0.1396263F, 2.617994F, 0F);
//        BladePort = new ModelRenderer(this, 44, 0);
//        BladePort.addBox(0F, 0F, 0F, 1, 6, 10);
//        BladePort.setRotationPoint(39F, 0F, -40F);
//        BladePort.setTextureSize(128, 128);
//        BladePort.mirror = true;
//        setRotation(BladePort, -0.1396263F, 2.617994F, 0F);

        OarPort = new ModelRenderer(this, 0, 39);
        OarPort.addBox(0F, 0F, -14F, 1, 1, 51);
        OarPort.setRotationPoint(22F, -4F, -12F);
        OarPort.setTextureSize(128, 128);
        OarPort.mirror = true;
        setRotation(OarPort, -0.1396263F, 2.617994F, 0F);
        BladePort = new ModelRenderer(this, 44, 0);
        BladePort.addBox(0F, -1F, 36F, 1, 6, 10);
        BladePort.setRotationPoint(22F, -4F, -12F);
        BladePort.setTextureSize(128, 128);
        BladePort.mirror = true;
        setRotation(BladePort, -0.1396263F, 2.617994F, 0F);

        OarStarboard = new ModelRenderer(this, 0, 39);
        OarStarboard.addBox(0F, 0F, -14F, 1, 1, 51);
        OarStarboard.setRotationPoint(22F, -4F, 28F);
        OarStarboard.setTextureSize(128, 128);
        OarStarboard.mirror = true;
        setRotation(OarStarboard, -0.1396263F, 0.5201081F, 0F);
        BladeStarboard = new ModelRenderer(this, 44, 0);
        BladeStarboard.addBox(0F, -1F, 34F, 1, 6, 10);
        BladeStarboard.setRotationPoint(22F, -4F, 28F);
        BladeStarboard.setTextureSize(128, 128);
        BladeStarboard.mirror = true;
        setRotation(BladeStarboard, -0.1396263F, 0.5235988F, 0F);

    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.7f, 0.1f, -0.5f);
        Hull.render(f5);

        GL11.glPushMatrix();
        GL11.glScalef(5.0f,.97f,1.03f);
        GL11.glTranslatef(0f,.01f,0f);
        Bow.render(f5);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glScalef(5.0f,.97f,1.03f);
        GL11.glTranslatef(-2f,.01f,0f);
        Stern.render(f5);
        GL11.glPopMatrix();

        RiggerMiddle.render(f5);
        RiggerPort.render(f5);
        RiggerStar.render(f5);



        setRotation(OarPort, leftHeight, 2.617994F+leftRotation, 0F);
        setRotation(BladePort, leftHeight, 2.617994F+leftRotation, 0f);

        setRotation(OarStarboard, rightHeight, 0.5201081F-rightRotation, 0F);
        setRotation(BladeStarboard, rightHeight, 0.5235988F-rightRotation, 0F);

        OarPort.render(f5);
        BladePort.render(f5);

        OarStarboard.render(f5);
        BladeStarboard.render(f5);

        GL11.glPopMatrix();



    }


    public void setOars(float lRot, float lHeight, float rRot, float rHeight)
    {
        leftRotation = lRot;
        leftHeight = lHeight;
        rightRotation = rRot;
        rightHeight = rHeight;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);



    }

}