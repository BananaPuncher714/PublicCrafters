package io.github.bananapuncher714.crafters.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.google.common.primitives.Primitives;

/**
 * Sets/Gets NBT tags from ItemStacks;
 * Created on 2017-09-04
 * 
 * @author BananaPuncher714
 */
public class NBTEditor {
	private static HashMap< String, Class<?> > classCache;
	private static HashMap< String, Method > methodCache;
	private static HashMap< Class< ? >, Constructor< ? > > constructorCache;
	private static HashMap< Class< ? >, Class< ? > > NBTClasses;
	private static HashMap< Class< ? >, Field > NBTTagFieldCache;
	private static Field NBTListData;
	private static String version;
	
	static {
		version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		
		classCache = new HashMap< String, Class<?> >();
		try {
			classCache.put( "NBTBase", Class.forName("net.minecraft.server." + version + "." + "NBTBase" ) );
			classCache.put( "NBTTagCompound", Class.forName("net.minecraft.server." + version + "." + "NBTTagCompound" ) );
			classCache.put( "NBTTagList", Class.forName( "net.minecraft.server." + version + "." + "NBTTagList" ) );
			classCache.put( "NBTBase", Class.forName( "net.minecraft.server." + version + "." + "NBTBase" ) );
			
			classCache.put( "ItemStack", Class.forName( "net.minecraft.server." + version + "." + "ItemStack" ) );
			classCache.put( "CraftItemStack", Class.forName( "org.bukkit.craftbukkit." + version + ".inventory." + "CraftItemStack" ) );
			
			classCache.put( "Entity", Class.forName( "net.minecraft.server." + version + "." + "Entity" ) );
			classCache.put( "CraftEntity", Class.forName( "org.bukkit.craftbukkit." + version + ".entity." + "CraftEntity" ) );
			classCache.put( "EntityLiving", Class.forName( "net.minecraft.server." + version + "." + "EntityLiving" ) );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		NBTClasses = new HashMap< Class< ? >, Class< ? > >();
		try {
			NBTClasses.put( Byte.class, Class.forName( "net.minecraft.server." + version + "." + "NBTTagByte" ) );
			NBTClasses.put( String.class, Class.forName( "net.minecraft.server." + version + "." + "NBTTagString" ) );
			NBTClasses.put( Double.class, Class.forName( "net.minecraft.server." + version + "." + "NBTTagDouble" ) );
			NBTClasses.put( Integer.class, Class.forName( "net.minecraft.server." + version + "." + "NBTTagInt" ) );
			NBTClasses.put( Long.class, Class.forName( "net.minecraft.server." + version + "." + "NBTTagLong" ) );
			NBTClasses.put( Short.class, Class.forName( "net.minecraft.server." + version + "." + "NBTTagShort" ) );
			NBTClasses.put( Float.class, Class.forName( "net.minecraft.server." + version + "." + "NBTTagFloat" ) );
			NBTClasses.put( Class.forName( "[B" ), Class.forName( "net.minecraft.server." + version + "." + "NBTTagByteArray" ) );
			NBTClasses.put( Class.forName( "[I" ), Class.forName( "net.minecraft.server." + version + "." + "NBTTagIntArray" ) );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		methodCache = new HashMap< String, Method >();
		try {
			methodCache.put( "get", getNMSClass( "NBTTagCompound" ).getMethod( "get", String.class ) );
			methodCache.put( "set", getNMSClass( "NBTTagCompound" ).getMethod( "set", String.class, getNMSClass( "NBTBase" ) ) );
			methodCache.put( "hasKey", getNMSClass( "NBTTagCompound" ).getMethod( "hasKey", String.class ) );
			methodCache.put( "setIndex", getNMSClass( "NBTTagList" ).getMethod( "a", int.class, getNMSClass( "NBTBase" ) ) );
			methodCache.put( "add", getNMSClass( "NBTTagList" ).getMethod( "add", getNMSClass( "NBTBase" ) ) );
			
			methodCache.put( "hasTag", getNMSClass( "ItemStack" ).getMethod( "hasTag" ) );
			methodCache.put( "getTag", getNMSClass( "ItemStack" ).getMethod( "getTag" ) );
			methodCache.put( "setTag", getNMSClass( "ItemStack" ).getMethod( "setTag", getNMSClass( "NBTTagCompound" ) ) );
			methodCache.put( "asNMSCopy", getNMSClass( "CraftItemStack" ).getMethod( "asNMSCopy", ItemStack.class ) );
			methodCache.put( "asBukkitCopy", getNMSClass( "CraftItemStack" ).getMethod( "asBukkitCopy", getNMSClass( "ItemStack" ) ) );
			
			methodCache.put( "getEntityHandle", getNMSClass( "CraftEntity" ).getMethod( "getHandle" ) );
			methodCache.put( "getEntityTag", getNMSClass( "Entity" ).getMethod( "c", getNMSClass( "NBTTagCompound" ) ) );
			methodCache.put( "setEntityTag", getNMSClass( "Entity" ).getMethod( "f", getNMSClass( "NBTTagCompound" ) ) );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		try {
			methodCache.put( "getTileTag", getNMSClass( "TileEntity" ).getMethod( "save", getNMSClass( "NBTTagCompound" ) ) );
		} catch( NoSuchMethodException exception ) {
			try {
				methodCache.put( "getTileTag", getNMSClass( "TileEntity" ).getMethod( "b", getNMSClass( "NBTTagCompound" ) ) );
			} catch ( Exception exception2 ) {
				exception2.printStackTrace();
			}
		} catch( Exception exception ) {
			exception.printStackTrace();
		}
		
		constructorCache = new HashMap< Class< ? >, Constructor< ? > >();
		try {
			constructorCache.put( getNBTTag( Byte.class ), getNBTTag( Byte.class ).getConstructor( byte.class ) );
			constructorCache.put( getNBTTag( String.class ), getNBTTag( String.class ).getConstructor( String.class ) );
			constructorCache.put( getNBTTag( Double.class ), getNBTTag( Double.class ).getConstructor( double.class ) );
			constructorCache.put( getNBTTag( Integer.class ), getNBTTag( Integer.class ).getConstructor( int.class ) );
			constructorCache.put( getNBTTag( Long.class ), getNBTTag( Long.class ).getConstructor( long.class ) );
			constructorCache.put( getNBTTag( Float.class ), getNBTTag( Float.class ).getConstructor( float.class ) );
			constructorCache.put( getNBTTag( Short.class ), getNBTTag( Short.class ).getConstructor( short.class ) );
			constructorCache.put( getNBTTag( Class.forName( "[B" ) ), getNBTTag( Class.forName( "[B" ) ).getConstructor( Class.forName( "[B" ) ) );
			constructorCache.put( getNBTTag( Class.forName( "[I" ) ), getNBTTag( Class.forName( "[I" ) ).getConstructor( Class.forName( "[I" ) ) );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		NBTTagFieldCache = new HashMap< Class< ? >, Field >();
		try {
			for ( Class< ? > clazz : NBTClasses.values() ) {
				Field data = clazz.getDeclaredField( "data" );
				data.setAccessible( true );
				NBTTagFieldCache.put( clazz, data );
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		try {
			NBTListData = getNMSClass( "NBTTagList" ).getDeclaredField( "list" );
			NBTListData.setAccessible( true );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public static Class<?> getPrimitiveClass( Class<?> clazz ) {
	    return Primitives.unwrap( clazz );
    }
	
	public static Class< ? > getNBTTag( Class< ? > primitiveType ) {
		if ( NBTClasses.containsKey( primitiveType ) )
			return NBTClasses.get( primitiveType );
		return primitiveType;
	}
	
	public static Object getNBTVar( Object object ) {
		if ( object == null ) return null;
		Class< ? > clazz = object.getClass();
		try {
			if ( NBTTagFieldCache.containsKey( clazz ) ) {
				return NBTTagFieldCache.get( clazz ).get( object );
			}
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
		return null;
	}
	
	public static Method getMethod( String name ) {
		return methodCache.containsKey( name ) ? methodCache.get( name ) : null;
	}
	
	public static Constructor< ? > getConstructor( Class< ? > clazz ) {
		return constructorCache.containsKey( clazz ) ? constructorCache.get( clazz ) : null;
	}
	
	public static Class<?> getNMSClass(String name) {
		if ( classCache.containsKey( name ) ) {
			return classCache.get( name );
		}

		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Gets an NBT tag in a given item with the specified keys
	 * 
	 * @param item
	 * The itemstack to get the keys from
	 * @param keys
	 * The keys to fetch; an integer after a key value indicates that it should get the nth place of
	 * the previous compound because it is a list;
	 * @return
	 * The item represented by the keys, and an integer if it is showing how long a list is.
	 */
	public static Object getItemTag( ItemStack item, Object... keys ) {
		try {
			Object stack = null;
			stack = getMethod( "asNMSCopy" ).invoke( null, item );
			
			Object tag = null;
			
			if ( getMethod( "hasTag" ).invoke( stack ).equals( true ) ) {
				tag = getMethod( "getTag" ).invoke( stack );
			} else {
				tag = getNMSClass( "NBTTagCompound" ).newInstance();
			}
			
			return getTag( tag, keys );
		} catch ( Exception exception ) {
			exception.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Sets an NBT tag in an item with the provided keys and value
	 * 
	 * @param item
	 * The itemstack to set
	 * @param keys
	 * The keys to set, String for NBTCompound, int or null for an NBTTagList
	 * @param value
	 * The value to set
	 * @return
	 * A new ItemStack with the updated NBT tags
	 */
	public static ItemStack setItemTag( ItemStack item, Object value, Object... keys ) {
		try {
			Object stack = getMethod( "asNMSCopy" ).invoke( null, item );
			
			Object tag = null;

			if ( getMethod( "hasTag" ).invoke( stack ).equals( true ) ) {
				tag = getMethod( "getTag" ).invoke( stack );
			} else {
				tag = getNMSClass( "NBTTagCompound" ).newInstance();
			}
			
			setTag( tag, value, keys );
			getMethod( "setTag" ).invoke( stack, tag );
			return ( ItemStack ) getMethod( "asBukkitCopy" ).invoke( null, stack );
		} catch ( Exception exception ) {
			exception.printStackTrace();
			return null;
		}
	}
	
	public static Object getEntityTag( Entity entity, Object... keys ) {
		try {
			Object NMSEntity = getMethod( "getEntityHandle" ).invoke( entity );
			
			Object tag = getNMSClass( "NBTTagCompound" ).newInstance();
			
			getMethod( "getEntityTag" ).invoke( NMSEntity, tag );
			
			return getTag( tag, keys );
		} catch ( Exception exception ) {
			exception.printStackTrace();
			return null;
		}
	}
	
	public static void setEntityTag( Entity entity, Object value, Object... keys ) {
		try {
			Object NMSEntity = getMethod( "getEntityHandle" ).invoke( entity );
			
			Object tag = getNMSClass( "NBTTagCompound" ).newInstance() ;
			
			getMethod( "getEntityTag" ).invoke( NMSEntity, tag );
			
			setTag( tag, value, keys );
			
			getMethod( "setEntityTag" ).invoke( NMSEntity, tag );
		} catch ( Exception exception ) {
			exception.printStackTrace();
			return;
		}
	}
	
	private static void setTag( Object tag, Object value, Object... keys ) throws Exception {
		Object notCompound = getConstructor( getNBTTag( value.getClass() ) ).newInstance( value );
		
		Object compound = tag;
		for ( int index = 0; index < keys.length; index++ ) {
			Object key = keys[ index ];
			if ( index + 1 == keys.length ) {
				if ( key == null ) {
					getMethod( "add" ).invoke( compound, notCompound );
				} else if ( key instanceof Integer ) {
					getMethod( "setIndex" ).invoke( compound, ( int ) key, notCompound );
				} else {
					getMethod( "set" ).invoke( compound, ( String ) key, notCompound );
				}
				break;
			}
			Object oldCompound = compound;
			if ( key instanceof Integer ) {
				compound = ( ( List< ? > ) NBTListData.get( compound ) ).get( ( int ) key );
			} else if ( key != null ) {
				compound = getMethod( "get" ).invoke( compound, ( String ) key );
			}
			if ( compound == null || key == null ) {
				if ( keys[ index + 1 ] == null || keys[ index + 1 ] instanceof Integer ) {
					compound = getNMSClass( "NBTTagList" ).newInstance();
				} else {
					compound = getNMSClass( "NBTTagCompound" ).newInstance();
				}
				if ( oldCompound.getClass().getSimpleName().equals( "NBTTagList" ) ) {
					getMethod( "add" ).invoke( oldCompound, compound );
				} else {
					getMethod( "set" ).invoke( oldCompound, ( String ) key, compound );
				}
			}
		}
	}
	
	private static Object getTag( Object tag, Object... keys ) throws Exception {
		Object notCompound = tag;
		for ( Object key : keys ) {
			if ( notCompound == null ) return null;
			if ( notCompound.getClass().getSimpleName().equals( "NBTTagCompound" ) ) {
				notCompound = getMethod( "get" ).invoke( notCompound, ( String ) key );
			} else if ( notCompound.getClass().getSimpleName().equals( "NBTTagList" ) ) {
				notCompound = ( ( List< ? > ) NBTListData.get( notCompound ) ).get( ( int ) key );
			} else {
				return getNBTVar( notCompound );
			}
		}
		if ( notCompound == null ) return null;
		if ( notCompound.getClass().getSimpleName().equals( "NBTTagList" ) ) {
			return ( ( List< ? > ) NBTListData.get( notCompound ) ).size();
		} else {
			return getNBTVar( notCompound );
		}
	}
}
