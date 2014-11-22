package com.republic.rowingmod.init;

import com.republic.rowingmod.item.ItemRowMod;
import com.republic.rowingmod.item.ItemRowingShell;
import com.republic.rowingmod.reference.Names;
import com.republic.rowingmod.reference.Reference;
import cpw.mods.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Reference.MOD_ID)
public class ModItems
{
    public static final ItemRowMod rowingShell = new ItemRowingShell();

    public static void init()
    {
        GameRegistry.registerItem(rowingShell, Names.Items.ROWING_SHELL);
    }

}
