package io.github.bananapuncher714.crafters.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import io.github.bananapuncher714.crafters.display.CraftDisplay;
import io.github.bananapuncher714.crafters.display.ItemDisplay;

/**
 * Called whenever a {@link CraftDisplay} has to update its {@link ItemDisplay};
 * Created on 2017-12-07
 * 
 * @author BananaPuncher714
 */
public class CraftDisplayUpdateEvent extends CraftDisplayEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	boolean cancelled = false;
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}

	public CraftDisplayUpdateEvent( CraftDisplay display ) {
		super( display );
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
