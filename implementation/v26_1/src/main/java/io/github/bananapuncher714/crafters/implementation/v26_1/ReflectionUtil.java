package io.github.bananapuncher714.crafters.implementation.v26_1;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import io.github.bananapuncher714.nbteditor.NBTEditor.MinecraftVersion;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerEntity.Synchronizer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class ReflectionUtil {
    private static Constructor< ServerEntity > CONSTRUCTOR;
    private static Object UPDATE_INTERVAL_NEVER;
    private static Method SET_INVULNERABLE;
    
    static {
        try {
            if ( NBTEditor.getRawMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v26_3 ) ) {
                Class< ? > updateIntervalClazz = Class.forName( "net.minecraft.world.entity.UpdateInterval" );
                CONSTRUCTOR = ServerEntity.class.getConstructor( ServerLevel.class, Entity.class, updateIntervalClazz, boolean.class, Synchronizer.class, Set.class );
                UPDATE_INTERVAL_NEVER = updateIntervalClazz.getDeclaredField( "NEVER" ).get( null );
                SET_INVULNERABLE = Entity.class.getDeclaredMethod( "setPermanentlyInvulnerable", boolean.class );
            } else {
                CONSTRUCTOR = ServerEntity.class.getConstructor( ServerLevel.class, Entity.class, int.class, boolean.class, Synchronizer.class, Set.class );
                SET_INVULNERABLE = Entity.class.getDeclaredMethod( "setInvulnerable", boolean.class );
            }
        } catch ( ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e ) {
            e.printStackTrace();
        }
    }
    
    protected static ServerEntity getServerEntityFor( Entity entity ) {
        try {
            if ( NBTEditor.getRawMinecraftVersion().greaterThanOrEqualTo( MinecraftVersion.v26_3 ) ) {
                return CONSTRUCTOR.newInstance( null, entity, UPDATE_INTERVAL_NEVER, false, null, null );
            } else {
                return CONSTRUCTOR.newInstance( null, entity, 0, false, null, null );
            }
        } catch ( InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
            e.printStackTrace();
            
            return null;
        }
    }
    
    protected static void setInvulnerable( Entity entity, boolean invulnerable ) {
        try {
            SET_INVULNERABLE.invoke( entity, invulnerable );
        } catch ( IllegalAccessException | InvocationTargetException e ) {
            e.printStackTrace();
        }
    }
}
