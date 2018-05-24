package io.github.bananapuncher714.crafters.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

/**
 * Something that is MUCH NEEDED!
 * Created on 2017-12-09
 * 
 * @author BananaPuncher714
 *
 */
public class CraftingTabCompleter implements TabCompleter {

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
		}
		StringUtil.copyPartialMatches( args[ args.length - 1 ], suggestions, completions);
		Collections.sort( completions );
		return completions;
	}

}
