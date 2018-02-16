package io.github.bananapuncher714.crafters.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import io.github.bananapuncher714.crafters.implementation.API.PublicCraftingInventory;

/**
 * A simple itemstack serializer and deserializer with world/chunk organization;
 * Created on 2017-12-09
 * 
 * @author BananaPuncher714
 *
 */
public final class CraftInventoryLoader {

	public static void save( File directory, PublicCraftingInventory inventory ){
		directory.mkdirs();
		Location location = inventory.getLocation();
		Chunk chunk = location.getChunk();
		int x = chunk.getX();
		int z = chunk.getZ();
		World world = location.getWorld();
		
		File saveLoc = new File( directory + "/" + world.getName() + "/" + x + "_" + z + "/" );
		saveLoc.mkdirs();
		
		File saveFile = new File( saveLoc + "/" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ() );
		
		try {
			saveFile.createNewFile();
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
		
		FileConfiguration config = YamlConfiguration.loadConfiguration( saveFile );
		
		int index = 0;
		for ( ItemStack item : inventory.getBukkitItems() ) {
			config.set( "items." + index++, item );
		}
	
		try {
			config.save( saveFile );
		} catch ( Exception exception ) {
			exception.printStackTrace();
		}
	}
	
	public static List< ItemStack > getItems( File baseDir, Location location ) {
		Chunk chunk = location.getChunk();
		int x = chunk.getX();
		int z = chunk.getZ();
		World world = location.getWorld();
		
		File saveLoc = new File( baseDir + "/" + world.getName() + "/" + x + "_" + z + "/" );

		File saveFile = new File( saveLoc + "/" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ() );
		
		return getItems( saveFile );
	}
	
	public static List< ItemStack > getItems( File file ) {
		List< ItemStack > items = new ArrayList< ItemStack >();
		
		if ( !file.exists() ) {
			while ( items.size() < 9 ) {
				items.add( new ItemStack( Material.AIR ) );
			}
			return items;
		}
		
		FileConfiguration config = YamlConfiguration.loadConfiguration( file );
		
		for ( int index = 0; index < 9; index++ ) {
			items.add( config.getItemStack( "items." + index ) );
		}
		file.delete();
		
		return items;
	}
	
	public static Map< Location, List< ItemStack > > loadChunk( File baseDir, World world, int x, int z ) {
		Map< Location, List< ItemStack > > itemMap = new HashMap< Location, List< ItemStack > >();
		
		File saveLoc = new File( baseDir + "/" + world.getName() + "/" + x + "_" + z + "/" );
		
		if ( !saveLoc.exists() ) {
			return itemMap;
		}
		
		for ( File file : saveLoc.listFiles() ) {
			String[] locArray = file.getName().split( "_" );
			Location location = new Location( world, Integer.parseInt( locArray[ 0 ] ), Integer.parseInt( locArray[ 1 ] ), Integer.parseInt( locArray[ 2 ] ) );
			itemMap.put( location, getItems( file ) );
			file.delete();
		}
		
		saveLoc.delete();
		
		return itemMap;
	}
}
