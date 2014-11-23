package com.republic.rowingmod.client.handler;


import com.republic.rowingmod.client.settings.Keybindings;
import com.republic.rowingmod.reference.Key;
import com.republic.rowingmod.utility.LogHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.entity.player.EntityPlayer;

public class KeyInputEventHandler
{

    private static Key getPressedKeybinding()
    {
        if(Keybindings.boatperspective.isPressed())
        {
            return Key.BOAT_PERSPECTIVE;
        }

        return Key.UNKNOWN;

    }

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent event)
    {
        if (getPressedKeybinding() == Key.UNKNOWN) {
            return;
        }
        if (FMLClientHandler.instance().getClient().inGameHasFocus)
        {
            if (FMLClientHandler.instance().getClientPlayerEntity() != null) {
                EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();

                if(getPressedKeybinding() == Key.BOAT_PERSPECTIVE)
                {
                   LogHelper.info("My Key pressed");
                }

            }
        }
    }
}
