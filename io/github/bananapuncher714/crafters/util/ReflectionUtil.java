package io.github.bananapuncher714.crafters.util;

import io.github.bananapuncher714.crafters.PublicCraftersMain;
import io.github.bananapuncher714.crafters.implementation.API.CraftInventoryManager;

import org.bukkit.Bukkit;

/**
 * Yet another simplified reflection utility to load the proper {@link CraftInventoryManager} for each version;
 * Created on 2017-12-07
 * 
 * @author BananaPuncher714
 */
public final class ReflectionUtil {
	private static Class< CraftInventoryManager > containerManager = null;
	private static String version;

	static {
		version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			containerManager = ( Class< CraftInventoryManager > ) Class.forName( "io.github.bananapuncher714.crafters.implementation." + version + ".ContainerManager_" + version );
		} catch ( ClassNotFoundException e) {
			PublicCraftersMain.getInstance().getLogger().severe( "'" + version + "' is not implemented at the moment! Please contact BananaPuncher714 for future support!" );
		}
	}
	
	public static CraftInventoryManager getManager() {
		Object instance = null;
		try {
			instance = containerManager.newInstance();
		} catch ( Exception exception ) {
			Bukkit.getPluginManager().disablePlugin( PublicCraftersMain.getInstance() );
		}
		
		return ( CraftInventoryManager ) instance; 
	}
	
	public static String getVersion() {
		return version;
	}
}
