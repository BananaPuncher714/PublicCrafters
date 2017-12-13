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
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		List< String > completions = new ArrayList< String >();
		if ( !( arg0 instanceof Player ) ) {
			completions.add( "reload" );
			return completions;
		}
		List< String > aos = new ArrayList< String >();
		Player player = ( Player ) arg0;
		if ( arg3.length == 1 ) {
			if ( player.hasPermission( "publiccrafters.reload" ) ) {
				aos.add( "reload" );
			}
			if ( player.hasPermission( "publiccrafters.private" ) ) {
				aos.add( "toggle" );
			}
		}
		StringUtil.copyPartialMatches( arg3[ arg3.length - 1 ], aos, completions);
		Collections.sort( completions );
		return completions;
	}

}
