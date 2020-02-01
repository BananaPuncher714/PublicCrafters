package io.github.bananapuncher714.crafters.events;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.crafters.display.CraftResultDisplay;

public class CraftResultDisplayCreateEvent extends CraftResultDisplayEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final Location location;
	boolean cancelled = false;
	
	public CraftResultDisplayCreateEvent( CraftResultDisplay display, Location location ) {
		super( display );
		this.location = location.clone();
	}
	
	public Location getLocation() {
		return location.clone();
	}
	
	public void setCraftResultDisplay( CraftResultDisplay display ) {
		this.display = display;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled( boolean cancel ) {
		cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
