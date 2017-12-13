package io.github.bananapuncher714.crafters.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * A simple class to serialize and deserialize locations into a string that works as YAML values;
 * Created on 2017-12-10
 * 
 * @author BananaPuncher714
 */
public final class Utils {
	
	public static Location getLocationFromString( String string ) {
		String[] ll = string.replace( ',', '.' ).split( "_" );
		Location l = new Location( Bukkit.getWorld( ll[ 0 ] ), Double.parseDouble( ll[ 1 ] ), Double.parseDouble( ll[ 2 ] ), Double.parseDouble( ll[ 3 ] ), Float.parseFloat( ll[ 4 ] ), Float.parseFloat( ll[ 5 ] ) );
		return l;
	}
	
	public static String getStringFromLocation( Location location ) {
		String newLoc = location.getWorld().getName() + "_" + String.valueOf( location.getX() ) + "_" + String.valueOf( location.getY() ) + "_" + String.valueOf( location.getZ() ) + "_" + String.valueOf( location.getYaw() ) + "_" + String.valueOf( location.getPitch() );
		return newLoc.replace( '.', ',' );
	}
}
