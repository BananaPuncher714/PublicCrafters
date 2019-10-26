package io.github.bananapuncher714.crafters.events;

import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.crafters.display.ItemDisplay;

/**
 * Called whenever an {@link ItemDisplay} is going to be removed;
 * Created on 2017-12-09
 * 
 * @author daniel
 *
 */
public class ItemDisplayDestroyEvent extends ItemDisplayEvent {
	private static final HandlerList handlers = new HandlerList();
	
	public ItemDisplayDestroyEvent( ItemDisplay item ) {
		super( item );
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
