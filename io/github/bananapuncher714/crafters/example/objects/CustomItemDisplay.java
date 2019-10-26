package io.github.bananapuncher714.crafters.example.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.bananapuncher714.crafters.PublicCrafters;
import io.github.bananapuncher714.crafters.display.CraftDisplay;
import io.github.bananapuncher714.crafters.display.ItemDisplay;
import io.github.bananapuncher714.crafters.example.util.RivenMath;

/**
 * This CustomItemDisplay makes 6 jack'o'lanterns spin in a circle around the location of the table;
 * Created on 2017-12-10
 * 
 * @author BananaPuncher714
 */
public class CustomItemDisplay extends ItemDisplay {
	List< ArmorStand > stands = new ArrayList< ArmorStand >();
	Location tableLoc;
	
	BukkitRunnable runnable = new BukkitRunnable() {
		int degree = 0;
		@Override
		public void run() {
			int curDeg = 0;
			for ( ArmorStand stand : stands ) {
				Location ringLoc = tableLoc.clone();
				int finalDeg = curDeg++ * 60 + degree;
				ringLoc.add( 2 * RivenMath.cos( ( float ) Math.toRadians( finalDeg ) ), .1, 2 * RivenMath.sin( ( float ) Math.toRadians( finalDeg ) ) );
				ringLoc.setYaw( finalDeg + 90 );
				stand.teleport( ringLoc );
			}
			degree = ( degree + 10 ) % 360;
		}
	};
	
	public CustomItemDisplay( CraftDisplay container, Location loc, ItemStack item, int slot ) {
		super( container, loc, item, slot );
		tableLoc = container.getLocation().clone().add( .5, .7, .5 );
	}
	
	@Override
	public void init() {
		super.init();
		for ( int i = 0; i < 6; i++ ) {
			Location ringLoc = tableLoc.clone();
			int degree = i * 60;
			ringLoc.add( 2 * RivenMath.cos( ( float ) Math.toRadians( degree ) ), .1, 2 * RivenMath.sin( ( float ) Math.toRadians( degree ) ) );
			ringLoc.setYaw( degree + 90 );
			ArmorStand stand = getModelStand( ringLoc );
			stand.setHelmet( new ItemStack( Material.JACK_O_LANTERN ) );
			stand.setMarker( true );
			stands.add( stand );
		}
		runnable.runTaskTimer( PublicCrafters.getInstance(), 0, 1 );
	}
	
	@Override
	public void remove() {
		super.remove();
		runnable.cancel();
		for ( ArmorStand stand : stands ) {
			stand.remove();
		}
	}
}
