package io.github.bananapuncher714.crafters.events;

import org.bukkit.event.Event;

import io.github.bananapuncher714.crafters.display.CraftResultDisplay;

public abstract class CraftResultDisplayEvent extends Event {
	protected CraftResultDisplay display;
	
	public CraftResultDisplayEvent( CraftResultDisplay display ) {
		this.display = display;
	}

	public CraftResultDisplay getDisplay() {
		return display;
	}
}
