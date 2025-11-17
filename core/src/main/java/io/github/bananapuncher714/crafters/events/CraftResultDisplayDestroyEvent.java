package io.github.bananapuncher714.crafters.events;

import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.crafters.display.CraftResultDisplay;

public class CraftResultDisplayDestroyEvent extends CraftResultDisplayEvent {
	private static final HandlerList handlers = new HandlerList();
	
	public CraftResultDisplayDestroyEvent( CraftResultDisplay display ) {
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
