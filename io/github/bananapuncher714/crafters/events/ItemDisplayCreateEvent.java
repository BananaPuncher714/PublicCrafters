package io.github.bananapuncher714.crafters.events;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.crafters.display.CraftDisplay;
import io.github.bananapuncher714.crafters.display.ItemDisplay;

/**
 * Called whenever a {@link CraftDisplay} is about to create a new {@link ItemDisplay};
 * This is where you can use a custom ItemDisplay instead;
 * Created on 2017-12-10
 * 
 * @author BananaPuncher714
 */
public class ItemDisplayCreateEvent extends ItemDisplayDestroyEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final Location location;
	boolean cancelled = false;
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public ItemDisplayCreateEvent( Location location, ItemDisplay item ) {
		super( item );
		this.location = location;
	}
	
	public Location getLocation() {
		return location.clone();
	}
	
	public void setItemDisplay( ItemDisplay display ) {
		item = display;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled( boolean cancel ) {
		cancelled = cancel;
	}
}
