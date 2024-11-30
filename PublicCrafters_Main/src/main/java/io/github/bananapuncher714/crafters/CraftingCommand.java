package io.github.bananapuncher714.crafters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import io.github.bananapuncher714.crafters.util.Utils;

/**
 * Command handling and tab completion
 * Created on 2017-12-09
 * 
 * @author BananaPuncher714
 *
 */
public class CraftingCommand implements CommandExecutor, TabCompleter {
	private final PublicCrafters plugin;
	
	protected CraftingCommand( PublicCrafters main ) {
		plugin = main;
	}
	
	@Override
	public List< String > onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
		List< String > completions = new ArrayList< String >();
		if ( !( sender instanceof Player ) ) {
			completions.add( "reload" );
			return completions;
		}
		List< String > suggestions = new ArrayList< String >();
		Player player = ( Player ) sender;
		if ( args.length == 1 ) {
			if ( player.hasPermission( "publiccrafters.reload" ) ) {
				suggestions.add( "reload" );
			}
			if ( player.hasPermission( "publiccrafters.private" ) ) {
				suggestions.add( "toggle" );
			}
			if ( player.hasPermission( "publiccrafters.admin" ) ) {
				suggestions.add( "lock" );
			}
		}
		StringUtil.copyPartialMatches( args[ args.length - 1 ], suggestions, completions);
		Collections.sort( completions );
		return completions;
	}
	
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		try {
			if ( args.length == 0 ) {
				commandToggle( sender, args );
			} else if ( args.length > 0 ) {
				String option = args[ 0 ];
				args = pop( args );
				if ( option.equalsIgnoreCase( "toggle" ) ) {
					commandToggle( sender, args );
				} else if ( option.equalsIgnoreCase( "reload" ) ) {
					commandReload( sender, args );
				} else if ( option.equalsIgnoreCase( "lock" ) ) {
					commandLock( sender, args );
				}
			}
		} catch ( IllegalArgumentException exception ) {
			sender.sendMessage( exception.getMessage() );
		}
		return false;
	}

	private void commandToggle( CommandSender sender, String[] args ) {
		Validate.isTrue( sender instanceof Player, plugin.getMessageFor( "must-be-player" ) );
		Validate.isTrue( sender.hasPermission( "publiccrafters.private" ), plugin.getMessageFor( "no-permission" ) );
		
		Player player = ( Player ) sender;
		plugin.setPrivate( player.getUniqueId(), !plugin.isPrivate( player.getUniqueId() ) );
		player.sendMessage( String.format( plugin.getMessageFor( "set-crafting" ), ( plugin.isPrivateByDefault() ^ plugin.isPrivate( player.getUniqueId() ) ) ) );
	}
	
	private void commandReload( CommandSender sender, String[] args ) {
		Validate.isTrue( sender.hasPermission( "publiccrafters.reload" ), plugin.getMessageFor( "no-permission" ) );
		
		sender.sendMessage( plugin.getMessageFor( "reloading-config" ) );
		PublicCrafters.getInstance().reload();
		sender.sendMessage( plugin.getMessageFor( "done-reloading-config" ) );
	}
	
	private void commandLock( CommandSender sender, String args[] ) {
		Validate.isTrue( sender.hasPermission( "publiccrafters.admin" ), plugin.getMessageFor( "no-permission" ) );
		Validate.isTrue( sender instanceof Player, plugin.getMessageFor( "must-be-player" ) );
		Player player = ( Player ) sender;
		
		Block block = player.getTargetBlock( ( Set< Material > ) null, 10 );
		Validate.notNull( block, plugin.getMessageFor( "must-look-at-table" ) );
		Validate.isTrue( block.getType() == Utils.getWorkbenchMaterial(), plugin.getMessageFor( "must-look-at-table" ) );
		
		Location location = block.getLocation();
		if ( plugin.getAdminTables().remove( location ) ) {
			player.sendMessage( plugin.getMessageFor( "unlocked-crafting-table" ) );
		} else {
			plugin.getAdminTables().add( location );
			player.sendMessage( plugin.getMessageFor( "locked-crafting-table" ) );
		}
		
	}
	
	private String[] pop( String[] array ) {
		String[] array2 = new String[ Math.max( 0, array.length - 1 ) ];
		for ( int i = 1; i < array.length; i++ ) {
			array2[ i - 1 ] = array[ i ];
		}
		return array2;
	}
}
