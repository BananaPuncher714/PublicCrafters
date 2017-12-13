package io.github.bananapuncher714.crafters.command;

import io.github.bananapuncher714.crafters.PublicCraftersMain;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handling almost at its finest :p
 * Created on 2017-12-09
 * 
 * @author BananaPuncher714
 *
 */
public class CraftingCommand implements CommandExecutor {
	private PublicCraftersMain plugin;
	
	public CraftingCommand( PublicCraftersMain main ) {
		plugin = main;
	}
	
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] a ) {
		if ( a.length == 0 ) {
			cmd_toggle( sender, a );
		} else if ( a.length == 1 ) {
			if ( a[ 0 ].equalsIgnoreCase( "toggle" ) ) {
				cmd_toggle( sender, a );
			} else if ( a[ 0 ].equalsIgnoreCase( "reload" ) ) {
				cmd_reload( sender, a ); 
			}
		}
		return false;
	}

	private void cmd_toggle( CommandSender sender, String[] args ) {
		if ( sender instanceof Player ) {
			Player player = ( Player ) sender;
			if ( player.hasPermission( "publiccrafters.private" ) ) {
				plugin.setPrivate( player.getUniqueId(), !plugin.isPrivate( player.getUniqueId() ) );
				player.sendMessage( ChatColor.AQUA + "You have set private crafting to " + plugin.isPrivate( player.getUniqueId() ) );
			} else {
				player.sendMessage( ChatColor.RED + "You do not have permission to run this command!" );
			}
		} else {
			sender.sendMessage( "You must be a player to run this command!" );
		}
	}
	
	private void cmd_reload( CommandSender sender, String[] args ) {
		if ( sender.hasPermission( "publiccrafters.reload" ) ) {
			sender.sendMessage( ChatColor.AQUA + "Reloading the config..." );
			PublicCraftersMain.getInstance().reload();
			sender.sendMessage( ChatColor.GREEN + "Done!" );
		} else {
			sender.sendMessage( ChatColor.RED + "You do not have permission to run this command!" );
		}
	}
}
