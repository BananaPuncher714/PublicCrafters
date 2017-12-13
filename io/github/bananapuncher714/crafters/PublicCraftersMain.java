package io.github.bananapuncher714.crafters;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.github.bananapuncher714.crafters.command.CraftingCommand;
import io.github.bananapuncher714.crafters.command.CraftingTabCompleter;
import io.github.bananapuncher714.crafters.example.listeners.CakeListener;
import io.github.bananapuncher714.crafters.example.listeners.CraftingListener;
import io.github.bananapuncher714.crafters.file.CraftInventoryLoader;
import io.github.bananapuncher714.crafters.implementation.API.CraftInventoryManager;
import io.github.bananapuncher714.crafters.listeners.ChunkListener;
import io.github.bananapuncher714.crafters.listeners.CraftBlockListener;
import io.github.bananapuncher714.crafters.listeners.InventoryOpenListener;
import io.github.bananapuncher714.crafters.listeners.InventoryUpdateListener;
import io.github.bananapuncher714.crafters.util.ReflectionUtil;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class of all classes;
 * Created on 2017-12-07
 * 
 * @author BananaPuncher714
 */
public class PublicCraftersMain extends JavaPlugin {
	private static PublicCraftersMain instance;
	
	private Set< UUID > pPlayers = new HashSet< UUID >();
	private CraftInventoryManager manager;
	private double height = .7;
	private boolean marker = false;
	private int delay = 0;
	private File saveFolder = new File( getDataFolder() + "/" + "saves" );
	
//	private CakeListener cake;
	
	@Override
	public void onEnable() {
		instance = this;
		
		saveDefaultConfig();
		loadConfig();
		
		manager = ReflectionUtil.getManager();
		
		registerListeners();
		registerCommands();
		
		loadChunks();
	}
	
	@Override
	public void onDisable() {
//		cake.stopAll();
		
		// Stop the player from duplicating items on server reload
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			InventoryView view = player.getOpenInventory();
			if ( view == null || manager.getLocation( view.getTopInventory() ) == null ) {
				continue;
			}
			player.closeInventory();
		}
		// Save the inventories
		unloadChunks();
	}
	
	private void unloadChunks() {
		// The chunk unload event is currently not working, so we'll have to save all with the CraftInventoryManager#stopAll() method.
//		for ( World world : Bukkit.getWorlds() ) {
//			for ( Chunk chunk : world.getLoadedChunks() ) {
//				manager.unload( chunk );
//			}
//		}
		manager.stopAll();
	}
	
	private void loadChunks() {
		for ( World world : Bukkit.getWorlds() ) {
			for ( Chunk chunk : world.getLoadedChunks() ) {
				Map< Location, List< ItemStack > > itemMap = CraftInventoryLoader.loadChunk( PublicCraftersMain.getInstance().getSaveFolder(), chunk.getWorld(), chunk.getX(), chunk.getZ() );
				for ( Location location : itemMap.keySet() ) {
					manager.load( location, itemMap.get( location ) );
				}
			}
		}
	}
	
	public void reload() {
		loadConfig();
	}
	
	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents( new InventoryOpenListener( this ), this );
		Bukkit.getPluginManager().registerEvents( new InventoryUpdateListener( this ), this );
		Bukkit.getPluginManager().registerEvents( new CraftBlockListener( this ), this );
		Bukkit.getPluginManager().registerEvents( new ChunkListener( manager ), this );
//		Bukkit.getPluginManager().registerEvents( new CraftingListener(), this );
//		cake = new CakeListener();
//		Bukkit.getPluginManager().registerEvents( cake, this );
	}
	
	private void registerCommands() {
		getCommand( "craftingtable" ).setExecutor( new CraftingCommand( this ) );
		getCommand( "craftingtable" ).setTabCompleter( new CraftingTabCompleter() );
	}
	
	private void loadConfig() {
		FileConfiguration config = YamlConfiguration.loadConfiguration( new File( getDataFolder() + "/" + "config.yml" ) );
		height = config.getDouble( "item-height" );
		marker = config.getBoolean( "marker" );
		delay = config.getInt( "update-delay" );
	}
	
	public boolean isPrivate( UUID playerUUID ) {
		return pPlayers.contains( playerUUID );
	}
	
	public void setPrivate( UUID uuid, boolean pr ) {
		if ( pr ) {
			pPlayers.add( uuid );
		} else if ( pPlayers.contains( uuid ) ) {
			pPlayers.remove( uuid );
		}
	}
	
	public CraftInventoryManager getManager() {
		return manager;
	}
	
	public static PublicCraftersMain getInstance() {
		return instance;
	}
	
	public double getHeight() {
		return height;
	}
	
	public boolean isMarker() {
		return marker;
	}
	
	public int getUpdateDelay() {
		return delay;
	}
	
	public File getSaveFolder() {
		return saveFolder;
	}
}
