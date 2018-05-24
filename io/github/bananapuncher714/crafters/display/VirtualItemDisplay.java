package io.github.bananapuncher714.crafters.display;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VirtualItemDisplay extends ItemDisplay {

	public VirtualItemDisplay( CraftDisplay container, Location loc, ItemStack item, int slot ) {
		super( container, loc.clone().add( -.5, .5, -.5 ), item, slot );
	}
	
	@Override
	public void init() {
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			PacketManager.spawn( location, player, handPose );
			PacketManager.update( location, player, item );
		}
	}
	
	@Override
	public void remove() {
		for ( Player player : Bukkit.getOnlinePlayers() ) {
			PacketManager.kill( location, player );
		}
	}
}
