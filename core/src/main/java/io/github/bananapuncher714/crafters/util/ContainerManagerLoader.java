package io.github.bananapuncher714.crafters.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.implementation.api.CraftInventoryManager;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import io.github.bananapuncher714.nbteditor.NBTEditor.MinecraftVersion;

public final class ContainerManagerLoader {
    private static Class< CraftInventoryManager > containerManager = null;
    private static CraftInventoryManager manager;
    private static String version;
    private static String mcVersion;

    static {
        version = NBTEditor.getMinecraftVersion().toString();
        
        try {
            Method getServerMethod = Bukkit.getServer().getClass().getMethod( "getServer" );
            Object dedicated = getServerMethod.invoke( Bukkit.getServer() );
            Method getVersionMethod = null;
            try {
                if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v26_1 ) ) {
                    getVersionMethod = dedicated.getClass().getMethod( "getServerVersion" );
                } else if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_21_R7 ) ) {
                    getVersionMethod = dedicated.getClass().getMethod( "Q" );
                } else if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_21_R6 ) ) {
                    getVersionMethod = dedicated.getClass().getMethod( "R" );
                } else if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_21_R2 ) ) {
                    getVersionMethod = dedicated.getClass().getMethod( "M" );
                } else if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_20_R4 ) ) {
                    getVersionMethod = dedicated.getClass().getMethod( "L" );
                } else if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_20_R3 ) ) {
                    getVersionMethod = dedicated.getClass().getMethod( "I" );
                } else if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_19_R3 ) ) {
                    getVersionMethod = dedicated.getClass().getMethod( "G" );
                } else if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_19_R1 ) ) {
                    getVersionMethod = dedicated.getClass().getMethod( "F" );
                } else if ( NBTEditor.getMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v1_18_R1 ) ) {
                    getVersionMethod = dedicated.getClass().getMethod( "G" );
                } else {
                    getVersionMethod = dedicated.getClass().getMethod( "getVersion" );
                }
            } catch ( NoSuchMethodException e ) {
                e.printStackTrace();
            }
            mcVersion = ( String ) getVersionMethod.invoke( dedicated );
        } catch ( NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1 ) {
            e1.printStackTrace();
        }
        
        try {
            String underscored = mcVersion.replace( '.', '_' );
            containerManager = ( Class< CraftInventoryManager > ) Class.forName( "io.github.bananapuncher714.crafters.implementation.v" + underscored + ".ContainerManager_v" + underscored );
        }  catch ( ClassNotFoundException e ) {
            try {
                containerManager = ( Class< CraftInventoryManager > ) Class.forName( "io.github.bananapuncher714.crafters.implementation." + version + ".ContainerManager_" + version );
            } catch ( ClassNotFoundException e1 ) {
                PublicCrafters.getInstance().getLogger().severe( "'" + version + "' is not implemented at the moment! Please contact BananaPuncher714 for future support!" );
            }
        }
    }

    public static CraftInventoryManager getManager() {
        if ( manager == null ) {
            try {
                manager = ( CraftInventoryManager ) containerManager.newInstance();
            } catch ( Exception exception ) {
                exception.printStackTrace();
                Bukkit.getPluginManager().disablePlugin( PublicCrafters.getInstance() );
            }
        }
        return manager;
    }

    public static String getVersion() {
        return version;
    }
}