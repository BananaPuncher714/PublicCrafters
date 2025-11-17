package io.github.bananapuncher714.crafters.events;

import org.bukkit.event.Event;

import io.github.bananapuncher714.crafters.display.CraftDisplay;

public abstract class CraftDisplayEvent extends Event {
	protected final CraftDisplay display;
	
	public CraftDisplayEvent( CraftDisplay display ) {
		this.display = display;
	}
	
	public CraftDisplay getDisplay() {
		return display;
	}
}
