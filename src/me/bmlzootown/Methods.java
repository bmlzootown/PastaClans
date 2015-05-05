package me.bmlzootown;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Methods {
	
	public static void DefaultConfig(){
	    PastaClans.plugin.getConfig().options().header("PastaClans v1.0 by bmlzootown");
	    PastaClans.plugin.getConfig().addDefault("Config.Enabled", Boolean.valueOf(true));
	    PastaClans.plugin.getConfig().addDefault("Config.Prefix", ChatColor.BLUE + "[" + ChatColor.AQUA + "PastaClans" + ChatColor.BLUE + "]");
	    PastaClans.plugin.getConfig().addDefault("Config.NoPermissionsMessage", ChatColor.RED + " You cannot do this!");
	    PastaClans.plugin.getConfig().addDefault("Config.MaxClanNameSize", Integer.valueOf(10));
	    PastaClans.plugin.getConfig().addDefault("Config.ClanChat.ChatSymbol", "*");
	    PastaClans.plugin.getConfig().options().copyDefaults(true);
	    PastaClans.plugin.saveConfig();
	}
	
	  public static boolean isOwner(Player p) {
	    String owner = PastaClans.plugin.getConfig().getString("Clans." + getClan(p).toLowerCase() + ".Owner");
	    return p.getName().equalsIgnoreCase(owner);
	  }
	  
	  public static boolean sameClan(Player o, String n) {
		  String nClan = PastaClans.plugin.getConfig().getString("Name." + n.toLowerCase() + ".Clan");
		  return getClan(o).equalsIgnoreCase(nClan); 
	  }
	  
	  public static void changeOwner(Player o, String n) {
		    PastaClans.plugin.getConfig().set("Clans." + getClan(o) + ".Owner", n);
	  }
	  
	  public static boolean isValid(String code) {
	    return code.matches("[aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789_-]*");
	  }
	  
	  public static boolean clanExists(String clan) {
	    return PastaClans.plugin.getConfig().getString("Clans." + clan.toLowerCase() + ".Owner") != null;
	  }
	  
	  public static void addPlayer(String clan, Player p) {
	    List<String> members = PastaClans.plugin.getConfig().getStringList("Clans." + clan.toLowerCase() + ".Members");
	    members.add(p.getName().toLowerCase());
	    PastaClans.plugin.getConfig().set("Clans." + clan.toLowerCase() + ".Members", members);
	    PastaClans.plugin.getConfig().set("Name." + p.getName().toLowerCase() + ".Clan", clan.toLowerCase());
	    PastaClans.plugin.getConfig().set("Name." + p.getName().toLowerCase() + ".UUID", p.getUniqueId().toString());
	    PastaClans.plugin.getConfig().set("Name." + p.getName().toLowerCase() + ".Name", p.getName());
	    PastaClans.plugin.saveConfig();
	  }
	  
	  public static void removePlayer(String clan, Player p) {
	    List<String> members = PastaClans.plugin.getConfig().getStringList("Clans." + clan.toLowerCase() + ".Members");
	    members.remove(p.getName().toLowerCase());
	    PastaClans.plugin.getConfig().set("Clans." + clan.toLowerCase() + ".Members", members);
	    PastaClans.plugin.getConfig().set("Name." + p.getName().toLowerCase() + ".Clan", null);
	    PastaClans.plugin.getConfig().set("Name." + p.getName().toLowerCase() + ".UUID", null);
	    PastaClans.plugin.getConfig().set("Name." + p.getName().toLowerCase() + ".Name", null);
	    PastaClans.plugin.getConfig().set("Name." + p.getName().toLowerCase(), null);
	    PastaClans.plugin.saveConfig();
	  }
	  
	  public static void removeOfflinePlayer(String clan, String p) {
	    List<String> members = PastaClans.plugin.getConfig().getStringList("Clans." + clan.toLowerCase() + ".Members");
	    members.remove(p.toLowerCase());
	    PastaClans.plugin.getConfig().set("Clans." + clan.toLowerCase() + ".Members", members);
	    PastaClans.plugin.getConfig().set("Name." + p.toLowerCase() + ".Clan", null);
	    PastaClans.plugin.getConfig().set("Name." + p.toLowerCase() + ".UUID", null);
	    PastaClans.plugin.getConfig().set("Name." + p.toLowerCase() + ".Name", null);
	    PastaClans.plugin.getConfig().set("Name." + p.toLowerCase(), null);
	    PastaClans.plugin.saveConfig();
	  }
	  
	  public static void deleteClan(String clan)
	  {
	    List<String> members = PastaClans.plugin.getConfig().getStringList("Clans." + clan.toLowerCase() + ".Members");
	    for (int i = 0; i < members.size(); i++) {
	      Player u = Bukkit.getPlayer((String)members.get(i));
	      if (u != null) {
	        u.sendMessage(PastaClans.plugin.getConfig().getString("Config.Prefix") + ChatColor.AQUA + " The clan was disbanded.");
	      }
	    }
	    for (int i = 0; i < members.size(); i++) {
	    	PastaClans.plugin.getConfig().set("Name." + (String)members.get(i) + ".Clan", null);
	    	PastaClans.plugin.getConfig().set("Name." + (String)members.get(i) + ".Name", null);
	    	PastaClans.plugin.getConfig().set("Name." + (String)members.get(i) + ".UUID", null);
	    }
	    PastaClans.plugin.getConfig().set("Clans." + clan, null);
	    PastaClans.plugin.saveConfig();
	  }
	  
	  public static List<String> getPlayers(String clan) {
	    List<String> list = PastaClans.plugin.getConfig().getStringList("Clans." + clan.toLowerCase() + ".Members");
	    return list;
	  }
	  
	  public static String getClanTag(Player p) {
	    String tag = PastaClans.plugin.getConfig().getString("Clans." + getClan(p) + ".Name");
	    return tag;
	  }
	  
	  public static String getClan(Player p) {
	    String clan = PastaClans.plugin.getConfig().getString("Name." + p.getName().toLowerCase() + ".Clan");
	    return clan;
	  }
	  
	  public static String getOwner(String c) {
		String ownerz = PastaClans.plugin.getConfig().getString("Clans." + c + ".Owner");
		return ownerz;
	  }
	  
	  public static boolean hasClan(Player p) {
	    return PastaClans.plugin.getConfig().getString("Name." + p.getName().toLowerCase() + ".Clan") != null;
	  }
	  
	  public static void createClan(String clanName, Player p)
	  {
		  PastaClans.plugin.getConfig().set("Clans." + clanName.toLowerCase() + ".Name", clanName);
		  PastaClans.plugin.getConfig().set("Clans." + clanName.toLowerCase() + ".Owner", p.getName().toLowerCase());
		  PastaClans.plugin.getConfig().set("Name." + p.getName().toLowerCase() + ".Clan", clanName.toLowerCase());
		  PastaClans.plugin.getConfig().set("Name." + p.getName().toLowerCase() + ".UUID", p.getUniqueId().toString());
		  PastaClans.plugin.getConfig().set("Name." + p.getName().toLowerCase() + ".Name", p.getName());
	    
	    List<String> m = PastaClans.plugin.getConfig().getStringList("Clans." + clanName.toLowerCase() + ".Members");
	    m.add(p.getName().toLowerCase());
	    PastaClans.plugin.getConfig().set("Clans." + clanName.toLowerCase() + ".Members", m);
	    PastaClans.plugin.saveConfig();
	  }

}
