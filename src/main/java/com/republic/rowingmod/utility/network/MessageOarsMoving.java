package com.republic.rowingmod.utility.network;

import com.republic.rowingmod.utility.LogHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MessageOarsMoving implements IMessage, IMessageHandler<MessageOarsMoving, IMessage>
{
    int boatID;
    float lRot,lHeight,rRot,rHeight;

    public MessageOarsMoving()
    {
    }

    public MessageOarsMoving(int boatId, float lRot, float lHeight, float rRot, float rHeight)
    {
        this.boatID = boatId;
        this.lRot = lRot;
        this.lHeight = lHeight;
        this.rRot = rRot;
        this.rHeight = rHeight;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.boatID=buf.readInt();
        this.lRot=buf.readFloat();
        this.lHeight=buf.readFloat();
        this.rRot=buf.readFloat();
        this.rHeight=buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(boatID);
        buf.writeFloat(lRot);
        buf.writeFloat(lHeight);
        buf.writeFloat(rRot);
        buf.writeFloat(rHeight);
    }

    @Override
    public IMessage onMessage(MessageOarsMoving message, MessageContext ctx)
    {
        //Send update to all clients
        PacketHandler.INSTANCE.sendToAll(new MessageUpdateShell(message.boatID,message.lRot,message.lHeight,message.rRot,message.rHeight));

        return null;
    }
}
