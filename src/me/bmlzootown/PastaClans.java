package me.bmlzootown;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class PastaClans extends JavaPlugin implements Listener{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static PastaClans plugin;
		
	public String prefix = getConfig().getString("Config.Prefix");
	public String noperms = getConfig().getString("Config.NoPermissionsMessage");
	public String ccs = getConfig().getString("Config.ClanChat.ChatSymbol");
	public int cg = getConfig().getInt("Config.MaxClanNameSize");
	Map<Player, Player> invites = new HashMap<Player, Player>();
	final Map<Player, Boolean> teleports = new HashMap<Player, Boolean>();
	private int taskId;

	@Override
	public void onEnable() {
		plugin = this;
		getServer().getPluginManager().registerEvents(this, this);
	    //System.out.println("[PastaClans] enabled!");
		Methods.DefaultConfig();
	}
	
	@Override
	public void onDisable() {
		//System.out.println("[PastaClans] disabled!");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		 if (getConfig().getBoolean("Config.Enabled") == true) {
		  if (sender.hasPermission("pastaclans")) {
			if ((cmd.getName().equalsIgnoreCase("clans") || cmd.getName().equalsIgnoreCase("clan")) && (sender instanceof Player)) {
				final Player u = (Player)sender;
				 if (args.length == 0) {
					 u.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "------------ " + ChatColor.AQUA + "Pasta Clans" + ChatColor.BLUE + "" + ChatColor.BOLD + " ------------");
					 u.sendMessage(ChatColor.AQUA + "/clan create [name] §3 Create a clan.");
					 u.sendMessage(ChatColor.AQUA + "/clan invite [user] §3 Invite someone to your clan.");
					 u.sendMessage(ChatColor.AQUA + "/clan give [user] §3 Gives clan to another member.");					 
					 u.sendMessage(ChatColor.AQUA + "/clan accept §3 Accept a clan invitation.");
					 u.sendMessage(ChatColor.AQUA + "/clan deny §3 Deny a clan invitation.");
					 u.sendMessage(ChatColor.AQUA + "/clan kick [user] §3 Kick a user from the clan.");
					 u.sendMessage(ChatColor.AQUA + "/clan leave §3 Leave a clan.");
					 u.sendMessage(ChatColor.AQUA + "/clan home §3 Go to the clan home.");
					 u.sendMessage(ChatColor.AQUA + "/clan sethome §3 Set the clan home (leader only).");
					 u.sendMessage(ChatColor.AQUA + "/clan info §3 See information about the clan.");
					 u.sendMessage(ChatColor.AQUA + "/clan disband §3 Disband the clan (leader only).");
					 u.sendMessage(ChatColor.AQUA + "/clan list §3 List all clans.");
				 }
				 
				 if (args.length == 1) {
					 if (args[0].equalsIgnoreCase("create")) {
						 u.sendMessage(ChatColor.BLUE + "" + ChatColor.MAGIC + "- " + ChatColor.AQUA + "Please use /clan create [name]");
					 }
					 if (args[0].equalsIgnoreCase("invite")) {
						 u.sendMessage(ChatColor.BLUE + "" + ChatColor.MAGIC + "- " + ChatColor.AQUA + "Please use /clan invite [user]");
					 }
					 if (args[0].equalsIgnoreCase("kick")) {
						 u.sendMessage(ChatColor.BLUE + "" + ChatColor.MAGIC + "- " + ChatColor.AQUA + "Please use /clan kick [user]");
					 }
					 if (args[0].equalsIgnoreCase("give")) {
						 u.sendMessage(ChatColor.BLUE + "" + ChatColor.MAGIC + "- " + ChatColor.AQUA + "Please use /clan give [user]");
					 }
					 if (args[0].equalsIgnoreCase("accept")) {
						 if (!u.hasPermission("pastaclans.accept")) {
					            u.sendMessage(this.prefix + this.noperms);
					          } else if (!Methods.hasClan(u)) {
					            if (this.invites.containsKey(u)) {
					              String clan = Methods.getClan((Player)this.invites.get(u));
					              if (Methods.getPlayers(clan).size() < 10) {
					                Methods.addPlayer(clan, u);
					                this.invites.remove(u);
					                for (int i = 0; i < Methods.getPlayers(clan).size(); i++) {
					                  Player t = Bukkit.getPlayer((String)Methods.getPlayers(clan).get(i));
					                  if (t != null) {
					                    t.sendMessage(this.prefix + " §9" + u.getName() + " §bjoined the clan.");
					                  }
					                }
					              } else {
					                u.sendMessage(this.prefix + ChatColor.AQUA + " This clan is full.");
					              }
					            } else {
					              u.sendMessage(this.prefix + ChatColor.AQUA + " You were not invited by anyone.");
					            }
					          } else {
					            u.sendMessage(this.prefix + ChatColor.AQUA + " You are already in a clan.");
					          }
					        }
					 if (args[0].equalsIgnoreCase("deny")) {
						 if (!u.hasPermission("pastaclans.deny")) {
					            u.sendMessage(this.prefix + this.noperms);
					          } else if (!Methods.hasClan(u)) {
					            if (this.invites.containsKey(u)) {
					              u.sendMessage(this.prefix + ChatColor.AQUA + " You rejected the invitation.");
					              if (this.invites.get(u) != null) {
					                ((Player)this.invites.get(u)).sendMessage(this.prefix + " §9" + u.getName() + " §brejected the invitation.");
					              }
					              this.invites.remove(u);
					            } else {
					              u.sendMessage(this.prefix + ChatColor.AQUA + " You weren't invited by anyone.");
					            }
					          } else {
					            u.sendMessage(this.prefix + ChatColor.AQUA + " You are not in a clan.");
					          }
					 }
					 if (args[0].equalsIgnoreCase("leave")) {
						 if (!u.hasPermission("pastaclans.leave")) {
					            u.sendMessage(this.prefix + this.noperms);
					          } else if (Methods.hasClan(u)) {
					            if (!Methods.isOwner(u)) {
					              for (int i = 0; i < Methods.getPlayers(Methods.getClan(u)).size(); i++) {
					                Player t = Bukkit.getPlayer((String)Methods.getPlayers(Methods.getClan(u)).get(i));
					                if (t != null) {
					                  t.sendMessage(this.prefix + " §9" + u.getName() + " §bleft the clan.");
					                }
					              }
					              Methods.removePlayer(Methods.getClan(u), u);
					            } else {
					              u.sendMessage(this.prefix + ChatColor.AQUA + " Please use §9/clan disband" + ChatColor.AQUA + ".");
					            }
					          } else {
					            u.sendMessage(this.prefix + ChatColor.AQUA + " You are not in a clan.");
					          }
					 }
					 if (args[0].equalsIgnoreCase("sethome")) {
						 if (!u.hasPermission("pastaclans.sethome")) {
					            u.sendMessage(this.prefix + this.noperms);
					          } else if (Methods.hasClan(u)) {
					            if (Methods.isOwner(u)) {
					              Location l = u.getLocation();
					              getConfig().set("Clans." + Methods.getClan(u) + ".Base.X", Double.valueOf(l.getX()));
					              getConfig().set("Clans." + Methods.getClan(u) + ".Base.Y", Double.valueOf(l.getY()));
					              getConfig().set("Clans." + Methods.getClan(u) + ".Base.Z", Double.valueOf(l.getZ()));
					              getConfig().set("Clans." + Methods.getClan(u) + ".Base.Yaw", Float.valueOf(l.getYaw()));
					              getConfig().set("Clans." + Methods.getClan(u) + ".Base.Pitch", Float.valueOf(l.getPitch()));
					              getConfig().set("Clans." + Methods.getClan(u) + ".Base.World", l.getWorld().getName());
					              saveConfig();
					              u.sendMessage(this.prefix + ChatColor.AQUA + " A new clan home has been set.");
					            } else {
					              u.sendMessage(this.prefix + ChatColor.AQUA + " You aren't the leader of the clan.");
					            }
					          } else {
					            u.sendMessage(this.prefix + ChatColor.AQUA + " You are not in a clan.");
					          }
					 }
					 if (args[0].equalsIgnoreCase("home")) {
						 if (!u.hasPermission("pastaclans.home")) {
					            u.sendMessage(this.prefix + this.noperms);
					          } else if (Methods.hasClan(u)) {
					            if (getConfig().getString("Clans." + Methods.getClan(u) + ".Base.World") != null) {
					              if (!this.teleports.containsKey(u)) {
					                this.teleports.put(u, Boolean.valueOf(true));
					                u.sendMessage(this.prefix + ChatColor.AQUA + " Teleporting... Please don't move.");
					                this.taskId = getServer().getScheduler().runTaskLater(this, new Runnable() {
					                  public void run() {
					                    if (PastaClans.this.teleports.containsKey(u)) {
					                      double x = PastaClans.this.getConfig().getDouble("Clans." + Methods.getClan(u) + ".Base.X");
					                      double y = PastaClans.this.getConfig().getDouble("Clans." + Methods.getClan(u) + ".Base.Y");
					                      double z = PastaClans.this.getConfig().getDouble("Clans." + Methods.getClan(u) + ".Base.Z");
					                      float yaw = PastaClans.this.getConfig().getInt("Clans." + Methods.getClan(u) + ".Base.Yaw");
					                      float pitch = PastaClans.this.getConfig().getInt("Clans." + Methods.getClan(u) + ".Base.Pitch");
					                      World world = Bukkit.getWorld(PastaClans.this.getConfig().getString("Clans." + Methods.getClan(u) + ".Base.World"));
					                      Location loc = new Location(world, x, y, z, yaw, pitch);
					                      u.teleport(loc);
					                      u.sendMessage(PastaClans.this.prefix + ChatColor.AQUA + " Teleportation successful.");
					                      PastaClans.this.teleports.remove(u);
					                      PastaClans.this.getServer().getScheduler().cancelTask(PastaClans.this.taskId);
					                    }
					                  }
					                }, 60L).getTaskId();
					              } else {
					                u.sendMessage(this.prefix + ChatColor.AQUA + " Teleportation is already in progress.");
					              }
					            } else {
					              u.sendMessage(this.prefix + ChatColor.AQUA + " Your clan doesn't have any base.");
					            }
					          } else {
					            u.sendMessage(this.prefix + ChatColor.AQUA + " You are not in a clan.");
					          }
					        }
					 if (args[0].equalsIgnoreCase("info")) {
						 if (!u.hasPermission("pastaclans.info")) {
				            u.sendMessage(this.prefix + this.noperms);
				          } else if (Methods.hasClan(u)) {
				            List<String> members = Methods.getPlayers(Methods.getClan(u));
				            u.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "------------ " + ChatColor.AQUA + Methods.getClan(u) + ChatColor.BLUE + "" + ChatColor.BOLD + " ------------");
				            u.sendMessage("Leader: " + ChatColor.DARK_AQUA + "" + Methods.getOwner(Methods.getClan(u)));
				            for (int i = 0; i < members.size(); i++) {
				              Player t = Bukkit.getPlayer((String)members.get(i));
				              Player z = Bukkit.getServer().getPlayer(Methods.getOwner(Methods.getClan(u)));
				              if (t != z) {
				              if (t != null) {
				                u.sendMessage("§b- " + t.getName() + " / " + t.getUniqueId());
				              } else {
				            	String UUID = PastaClans.plugin.getConfig().getString("Name." + (String)members.get(i).toLowerCase() + ".UUID");
				                u.sendMessage("§b- §7" + (String)members.get(i) + " / " + UUID);
				              }
				             }
				            }
				            u.sendMessage(" ");
				          } else {
				            u.sendMessage(this.prefix + ChatColor.AQUA  + " You are not in a clan.");
				          }
					 }
					 if (args[0].equalsIgnoreCase("list")) {
						 if (getConfig().getConfigurationSection("Clans") != null){
					        	Set<String> keys = getConfig().getConfigurationSection("Clans").getKeys(false);
					        	String[] newString = keys.toArray( new String[0] );
					        	u.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "------------ " + ChatColor.AQUA + "Clans List" + ChatColor.BLUE + "" + ChatColor.BOLD + " ------------");
					        	for (int i = 0; i < newString.length; i++) {
					        			String names = getConfig().getString("Clans." + newString[i] + ".Name");
					        			u.sendMessage("§b- " + names);
					        	}   
					        	} else {
					        		u.sendMessage(this.prefix + ChatColor.AQUA +"  No clans currently exist.");
					        	}
					 }
					 if (args[0].equalsIgnoreCase("disband")) {
						 if (!u.hasPermission("pastaclans.disband")) {
					            u.sendMessage(this.prefix + this.noperms);
					          } else if (Methods.hasClan(u)) {
					            if (Methods.isOwner(u)) {
					              Methods.deleteClan(Methods.getClan(u));
					            } else {
					              u.sendMessage(this.prefix + ChatColor.AQUA + " You are not the leader of the clan.");
					            }
					          } else {
					            u.sendMessage(this.prefix + ChatColor.AQUA + " You are not in a clan.");
					          }
					 }
				 }
				 
				 if (args.length == 2) {
					 if (args[0].equalsIgnoreCase("create")) {
				          if (!u.hasPermission("pastaclans.create")) {
				            u.sendMessage(this.prefix + this.noperms);
				          } else if (!Methods.hasClan(u)) {
				            String tag = args[1];
				            if (tag.length() <= this.cg) {
				              if (!Methods.clanExists(tag)) {
				                if (Methods.isValid(tag)) {
				                	Methods.createClan(tag, u);
				                  u.sendMessage(this.prefix + ChatColor.AQUA + " You created the clan §9" + tag + " §b.");
				                } else {
				                  u.sendMessage(this.prefix + ChatColor.AQUA + " This clan name is not valid.");
				                }
				              } else {
				                u.sendMessage(this.prefix + ChatColor.AQUA + " This clan already exists.");
				              }
				            } else {
				              u.sendMessage(this.prefix + ChatColor.AQUA + " This clan name is too long.");
				            }
				          } else {
				            u.sendMessage(this.prefix + ChatColor.AQUA + " You are already in a clan.");
				          }
				        }
					 if (args[0].equalsIgnoreCase("give")) {
				          if (!u.hasPermission("pastaclans.give")) {
				            u.sendMessage(this.prefix + this.noperms);
				          } else if (Methods.hasClan(u)) {
				        	  if (Methods.isOwner(u)) {
				        		  if (Methods.sameClan(u, args[1])) {
				        			  Methods.changeOwner(u, args[1]);
				        		  } else {
				        			  u.sendMessage(this.prefix + ChatColor.AQUA + " This player is not in your clan.");
				        		  }
				        	  } else {
				        		  u.sendMessage(this.prefix + ChatColor.AQUA + " You are not the owner of this clan.");
				        	  }
				          } else {
				            u.sendMessage(this.prefix + ChatColor.AQUA + " You are not in a clan.");
				          }
				        }
				        if (args[0].equalsIgnoreCase("info")) {
				          if (!u.hasPermission("pastaclans.info")) {
				            u.sendMessage(this.prefix + this.noperms);
				          } else {
				            String clan = args[1].toLowerCase();
				            if (getConfig().getString("Clans." + clan + ".Owner") != null) {
				              List<String> members = Methods.getPlayers(clan);
				              u.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "------------ " + ChatColor.AQUA + clan + ChatColor.BLUE + "" + ChatColor.BOLD + " ------------");		     
					          u.sendMessage("Leader: " + ChatColor.DARK_AQUA + "" + Methods.getOwner(clan));
				              for (int i = 0; i < members.size(); i++) {
				                Player t = Bukkit.getPlayer((String)members.get(i));
				                Player z = Bukkit.getServer().getPlayer(Methods.getOwner(clan));
				                if (t != z) {
				                if (t != null) {
				                  u.sendMessage("§b- " + t.getName() + " / " + t.getUniqueId());
				                } else {
				                  String UUID = PastaClans.plugin.getConfig().getString("Name." + (String)members.get(i).toLowerCase() + ".UUID");
				                  u.sendMessage("§b- §7" + (String)members.get(i) + " / " + UUID);
				                }
				              }
				              }
				              u.sendMessage(" ");
				            } else {
				              u.sendMessage(this.prefix + ChatColor.AQUA + " Clan not found.");
				            }
				          }
				        }
				        if (args[0].equalsIgnoreCase("kick")) {
				          if (!u.hasPermission("pastaclans.kick")) {
				            u.sendMessage(this.prefix + this.noperms);
				          } else if (Methods.hasClan(u)) {
				            if (Methods.isOwner(u)) {
				              List<String> members = Methods.getPlayers(Methods.getClan(u));
				              Player t = Bukkit.getPlayer(args[1]);
				              String off = args[1];
				              String clanz = Methods.getClan(u);
				                if (t != null){
				                    if (members.contains(t.getName().toLowerCase())) {
				                    	Methods.removePlayer(Methods.getClan(u), t);
				                      
				                      if (Methods.getClan(u) != null) {
				                      
				                      for (int i = 0; i < Methods.getPlayers(Methods.getClan(u)).size(); i++) {
				                        Player tt = Bukkit.getPlayer((String)Methods.getPlayers(Methods.getClan(u)).get(i));
				                        if (tt != null) {
				                          tt.sendMessage(this.prefix + " §9 " + t.getName() + " §bwas kicked from the clan.");
				                        }
				                        t.sendMessage(this.prefix + ChatColor.AQUA + " You were kicked of the clan.");
				                      }  
				                     } else {
				                    	 Methods.addPlayer(clanz, u);
				                    	 u.sendMessage(this.prefix + ChatColor.AQUA + " You cannot kick yourself.");
				                    	 u.sendMessage(this.prefix + ChatColor.AQUA + " Please use §9/clan disband.");
				                     }  
				                    } else {
				                      u.sendMessage(this.prefix + ChatColor.AQUA + " This player is not in your clan.");
				                    }
				                  } else {
				                  	if (members.contains(off.toLowerCase())){
				                  		Methods.removeOfflinePlayer(Methods.getClan(u), off);
				                  		sender.sendMessage(this.prefix + " §9" + off + " §bwas kicked out of the clan.");
				                  	} else {
				                          u.sendMessage(this.prefix + ChatColor.AQUA + " This player is not in your clan.");
				                        }
				                  } 
				            } else {
				              u.sendMessage(this.prefix + ChatColor.AQUA + " Only the leader of the clan can kick players..");
				            }
				          } else {
				            u.sendMessage(this.prefix + ChatColor.AQUA + " You are not in a clan.");
				          }
				        }
				        if (args[0].equalsIgnoreCase("invite")) {
				          if (!u.hasPermission("pastaclans.invite")) {
				            u.sendMessage(this.prefix + this.noperms);
				          } else {
				            String name = args[1];
				            if (Methods.hasClan(u)) {
				              if (Methods.isOwner(u)) {
				                Player t = Bukkit.getPlayer(name);
				                if (t != null) {
				                  if (!Methods.hasClan(t)) {
				                    if (!this.invites.containsKey(t)) {
				                      if (t != u) {
				                        invitePlayer(t, u);
				                        u.sendMessage(this.prefix + ChatColor.AQUA + " You have invited §9" + t.getName() + " §bto the clan.");
				                        t.sendMessage(" ");
				                        t.sendMessage("§bYou have been invited to the clan §9" + Methods.getClanTag(u) + " §b.");
				                        t.sendMessage("§bTo join it, type §9/clan accept.");
				                        t.sendMessage("§bTo reject the invitation, type §9/clan deny.");
				                        t.sendMessage(" ");
				                      } else {
				                        u.sendMessage(this.prefix + ChatColor.AQUA + " You can't invite yourself.");
				                      }
				                    } else {
				                      u.sendMessage(this.prefix + ChatColor.AQUA + " This player was already invited by someone.");
				                    }
				                  } else {
				                    u.sendMessage(this.prefix + ChatColor.AQUA + " This player is already in a clan.");
				                  }
				                }  else {
				                  u.sendMessage(this.prefix + ChatColor.AQUA + " This player is not online.");
				                }
				              } else {
				                u.sendMessage(this.prefix + ChatColor.AQUA + " You are not the leader of the clan.");
				              }
				            } else {
				              u.sendMessage(this.prefix + ChatColor.AQUA + " You are not in a clan.");
				            }
				          }
				        } 
				 }
			  }
			}
			
		  }
		return false;
	}
	
	  public void invitePlayer(Player invited, Player inviter)
	  {
	    this.invites.put(invited, inviter);
	  }
	  
	  @EventHandler
	  public void onChat(AsyncPlayerChatEvent event)
	  {
	   // if (Methods.hasClan(event.getPlayer())) {
	   //   event.setFormat("§b" + Methods.getClanTag(event.getPlayer()) + "§b§f*" + event.getPlayer().getDisplayName() + ": §f" + event.getMessage());
	   // } else {
	   //   event.setFormat(" §f" + event.getPlayer().getDisplayName() + ": §f" + event.getMessage());
	   // }
	    if (event.getMessage().startsWith(this.ccs)) {
	      if (Methods.hasClan(event.getPlayer())) {
	        for (int i = 0; i < Methods.getPlayers(Methods.getClan(event.getPlayer())).size(); i++) {
	          Player t = Bukkit.getPlayer((String)Methods.getPlayers(Methods.getClan(event.getPlayer())).get(i));
	          if (t != null) {
	        	if (Methods.isOwner(event.getPlayer())) {  
	        	  	t.sendMessage(ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + PastaClans.plugin.getConfig().getString("Clans." + Methods.getClan(event.getPlayer()) + ".Name") + ChatColor.DARK_AQUA + "]" + ChatColor.DARK_PURPLE + " " + event.getPlayer().getName() + "§b: " + event.getMessage().substring(1, event.getMessage().length()));
	          	} else {
	          		t.sendMessage(ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + PastaClans.plugin.getConfig().getString("Clans." + Methods.getClan(event.getPlayer()) + ".Name") + ChatColor.DARK_AQUA + "]" + ChatColor.DARK_AQUA + " " + event.getPlayer().getName() + "§b: " + event.getMessage().substring(1, event.getMessage().length()));
	          	}
	          }
	        }
	        event.setCancelled(true);
	      } else {
	        event.getPlayer().sendMessage(this.prefix + ChatColor.AQUA + " You are not in a clan.");
	      }
	    }
	  }
	  
	  @EventHandler
	  public void onQuit(PlayerQuitEvent event) {
			if (getConfig().getString("Name." + event.getPlayer().getName().toLowerCase()) != null) {
				if (getConfig().getString("Name." + event.getPlayer().getName().toLowerCase() + ".Clan") == null) {
					getConfig().set("Name." + event.getPlayer().getName().toLowerCase(), null);
					saveConfig();
				}
			}
	    if (this.invites.containsKey(event.getPlayer())) {
	      this.invites.remove(event.getPlayer());
	    }
	  }
	  
	  @EventHandler
	  public void onMove(PlayerMoveEvent event) {
	    if ((this.teleports.containsKey(event.getPlayer())) && 
	      (event.getFrom().distance(event.getTo()) > 0.1D))
	    {
	      this.teleports.remove(event.getPlayer());
	      event.getPlayer().sendMessage(this.prefix + ChatColor.AQUA + " Teleportation cancelled.");
	      getServer().getScheduler().cancelTask(this.taskId);
	    }
	  }
	  
	  @EventHandler
	  public void onDamage(EntityDamageByEntityEvent event) {
	    if ((event.getEntity() instanceof Player)) {
	      Player p = (Player)event.getEntity();
	      if ((event.getDamager() instanceof Player)) {
	        Player t = (Player)event.getDamager();
	        if ((Methods.getClan(p) != null) && (Methods.getClan(t) != null) && (Methods.getClan(p).equalsIgnoreCase(Methods.getClan(t)))) {
	          event.setCancelled(true);
	        }
	      }
	      if ((event.getDamager() instanceof Arrow)) {
	        Player t = (Player)((Arrow)event.getDamager()).getShooter();
	        if ((Methods.getClan(p) != null) && (Methods.getClan(t) != null) && (Methods.getClan(p).equalsIgnoreCase(Methods.getClan(t)))) {
	          event.setCancelled(true);
	        }
	      }
	    }
	  }
	  
	  @EventHandler
	  public void onPotionThrow(PotionSplashEvent event) {
	    if ((event.getPotion().getShooter() instanceof Player)) {
	      Player p = (Player)event.getPotion().getShooter();
	      for (LivingEntity ent : event.getAffectedEntities()) {
	        if ((ent instanceof Player)) {
	          Player t = (Player)ent;
	          if ((t != p) && 
	            (Methods.getClan(p) != null) && (Methods.getClan(t) != null) && (Methods.getClan(p).equalsIgnoreCase(Methods.getClan(t)))) {
	            event.setCancelled(true);
	          }
	        }
	      }
	    }
	  }
	  
	  @EventHandler
	  public void onJoin(PlayerJoinEvent e) { 
		  Player b = e.getPlayer();
		  String UUIDz = b.getUniqueId().toString();
		  
		  if (getConfig().getConfigurationSection("Name") != null){
				Set<String> keysz = getConfig().getConfigurationSection("Name").getKeys(false);
		      	String[] newStringz = keysz.toArray( new String[0] );
		      	for (int i = 0; i < newStringz.length; i++) {
		      		String oldNamez = getConfig().getConfigurationSection("Name." + newStringz[i]).getName();
		      		if (b.getName().equalsIgnoreCase(oldNamez)) {
		      			if (getConfig().getString("Name." + oldNamez + ".UUID") == null) {
		      				getConfig().set("Name." + b.getName().toLowerCase() + ".UUID", b.getUniqueId().toString());
		      				getConfig().set("Name." + b.getName().toLowerCase() + ".Name", b.getName());
		      			}
		      		}
		      	}
		  }

		  if (getConfig().getConfigurationSection("Name") != null){
		      	Set<String> keys = getConfig().getConfigurationSection("Name").getKeys(false);
		      	String[] newString = keys.toArray( new String[0] );
		      	for (int i = 0; i < newString.length; i++) {
		      			String oldUUID = getConfig().getString("Name." + newString[i] + ".UUID");
		      			String oldName = getConfig().getString("Name." + newString[i] + ".Name");
		      			String clan = getConfig().getString("Name." + newString[i] + ".Clan");
		      		if (UUIDz.equalsIgnoreCase(oldUUID)) {
		      			if (b.getName() != oldName) {
		      				
		      				List<String> members = getConfig().getStringList("Clans." + clan.toLowerCase() + ".Members");
		      			    members.remove(oldName.toLowerCase());
		      			    members.add(b.getName().toLowerCase());
		      			    getConfig().set("Clans." + clan.toLowerCase() + ".Members", members);
		      			    String owner = getConfig().getString("Clans." + clan.toLowerCase() + ".Owner");
		      			    getConfig().set("Name." + oldName.toLowerCase(), null);
		      			    getConfig().set("Name." + b.getName().toLowerCase() + ".Clan", clan);
		      			    getConfig().set("Name." + b.getName().toLowerCase() + ".UUID", b.getUniqueId().toString());
		      			    getConfig().set("Name." + b.getName().toLowerCase() + ".Name", b.getName());
		      				
		      			    if (owner.equalsIgnoreCase(oldName)) {
		      			    	getConfig().set("Clans." + clan.toLowerCase() + ".Owner", b.getName().toLowerCase());
		      			    }
		      		  }
		      		} 
		      	  }
		      	saveConfig();
		      	}  
	  		}
}
