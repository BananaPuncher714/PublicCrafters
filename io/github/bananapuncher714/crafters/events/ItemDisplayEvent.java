package io.github.bananapuncher714.crafters.events;

import org.bukkit.event.Event;

import io.github.bananapuncher714.crafters.display.ItemDisplay;

public abstract class ItemDisplayEvent extends Event {
	protected ItemDisplay item;
	
	public ItemDisplayEvent( ItemDisplay item ) {
		this.item = item;
	}
	
	public ItemDisplay getItemDisplay() {
		return item;
	}
}
