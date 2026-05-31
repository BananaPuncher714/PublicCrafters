package io.github.bananapuncher714.crafters.events;

import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.crafters.display.AbstractItemDisplay;

public class ItemResultDisplayDestroyEvent extends ItemDisplayEvent {
	private static final HandlerList handlers = new HandlerList();
	
	public ItemResultDisplayDestroyEvent( AbstractItemDisplay display ) {
		super( display );
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
