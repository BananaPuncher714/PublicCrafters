package io.github.bananapuncher714.crafters.events;

import org.bukkit.event.Event;

import io.github.bananapuncher714.crafters.display.AbstractItemDisplay;

public abstract class ItemDisplayEvent extends Event {
	protected AbstractItemDisplay item;
	
	public ItemDisplayEvent( AbstractItemDisplay item ) {
		this.item = item;
	}
	
	public AbstractItemDisplay getItemDisplay() {
		return item;
	}
}
