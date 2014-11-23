package com.republic.rowingmod.utility.network;

import com.republic.rowingmod.entity.EntityRowingShell;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public class MessageOarKeyPressed implements IMessage, IMessageHandler<MessageOarKeyPressed, IMessage>
{
    int boatID;
    boolean leftOar;
    boolean rightOar;
    boolean holdWater;

    public MessageOarKeyPressed(){}

    public MessageOarKeyPressed(int boatId, boolean leftOar, boolean rightOar, boolean holdWater)
    {
        this.boatID = boatId;
        this.leftOar = leftOar;
        this.rightOar = rightOar;
        this.holdWater = holdWater;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.boatID = buf.readInt();
        this.leftOar = buf.readBoolean();
        this.rightOar = buf.readBoolean();
        this.holdWater = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.boatID);
        buf.writeBoolean(this.leftOar);
        buf.writeBoolean(this.rightOar);
        buf.writeBoolean(this.holdWater);
    }

    @Override
    public IMessage onMessage(MessageOarKeyPressed message, MessageContext ctx) {

        //When the server receives a message that an oar is being moved...

        //PacketHandler.INSTANCE.sendToAll(new MessageUpdateShell(message.boatID ,message.leftOar, message.rightOar));

        //Update server side version of entity
        EntityRowingShell shell = (EntityRowingShell)  MinecraftServer.getServer().getEntityWorld().getEntityByID(message.boatID);
        if(shell != null)
            shell.setOarData(message.leftOar, message.rightOar, message.holdWater);

        return null;
    }
}
