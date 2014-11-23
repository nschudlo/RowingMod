package com.republic.rowingmod.utility.network;

import com.republic.rowingmod.entity.EntityRowingShell;
import com.republic.rowingmod.utility.LogHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class MessageUpdateShell implements IMessage, IMessageHandler<MessageUpdateShell, IMessage>
{

    int boatID;
    boolean leftOar;
    boolean rightOar;

    public MessageUpdateShell()
    {
    }

    public MessageUpdateShell(int boatId, boolean leftOar, boolean rightOar)
    {
        this.boatID = boatId;
        this.leftOar = leftOar;
        this.rightOar = rightOar;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.boatID = buf.readInt();
        this.leftOar = buf.readBoolean();
        this.rightOar = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.boatID);
        buf.writeBoolean(this.leftOar);
        buf.writeBoolean(this.rightOar);
    }


    @Override
    public IMessage onMessage(MessageUpdateShell message, MessageContext ctx)
    {
        EntityRowingShell shell = (EntityRowingShell)Minecraft.getMinecraft().theWorld.getEntityByID(message.boatID);
        //shell.setOarData(message.leftOar, message.rightOar, message.);


        return null;
    }
}
