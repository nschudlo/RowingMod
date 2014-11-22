package com.republic.rowingmod.client.handler;


import com.republic.rowingmod.client.settings.Keybindings;
import com.republic.rowingmod.reference.Key;
import com.republic.rowingmod.utility.LogHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

public class KeyInputEventHandler
{

    private static Key getPressedKeybinding()
    {
        if(Keybindings.port.isPressed())
        {
            return Key.PORT;
        }
        else if(Keybindings.starboard.isPressed())
        {
            return Key.STARBOARD;
        }

        return Key.UNKNOWN;

    }

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent event)
    {
        LogHelper.info(getPressedKeybinding());
    }
}
