package com.republic.rowingmod.client.settings;

import com.republic.rowingmod.reference.Names;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class Keybindings {
    public static KeyBinding port = new KeyBinding(Names.Keys.PORT_OAR, Keyboard.KEY_A, Names.Keys.CATEGORY);
    public static KeyBinding starboard = new KeyBinding(Names.Keys.STAR_OAR, Keyboard.KEY_D, Names.Keys.CATEGORY);

}
