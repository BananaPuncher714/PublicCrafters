package io.github.bananapuncher714.crafters.events;

import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.crafters.display.CraftDisplay;

/**
 * Called whenever a {@link CraftDisplay} is going to be removed, or whenever its {@link CraftDisplay#stop()} method is called;
 * Created on 2017-12-11
 * 
 * @author BananaPuncher714
 */
public class CraftDisplayDestroyEvent extends CraftDisplayEvent {
	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public CraftDisplayDestroyEvent( CraftDisplay display ) {
		super( display );
	}
}
