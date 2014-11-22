package com.republic.rowingmod.init;

import com.republic.rowingmod.RowingMod;
import com.republic.rowingmod.entity.EntityRowingShell;
import com.republic.rowingmod.reference.Names;
import com.republic.rowingmod.reference.Reference;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;


@GameRegistry.ObjectHolder(Reference.MOD_ID)
public class ModEntities
{

    public static void init()
    {
        registerEntity(EntityRowingShell.class, Names.Items.ROWING_SHELL);
    }

    public static void registerEntity(Class entityClass, String name)
    {
        int entityID = EntityRegistry.findGlobalUniqueEntityId();

        EntityRegistry.registerGlobalEntityID(entityClass, name, entityID);
        EntityRegistry.registerModEntity(entityClass, name, entityID, RowingMod.instance, 64, 1, true);
    }

}
