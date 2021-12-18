package me.NerdsWBNerds.Handcuffs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.RED;

public class Handcuffs extends JavaPlugin implements CommandExecutor {
    public static Material cuffID = Material.STRING, keyID = Material.SHEARS;
    public static int cuffAmount = 7;
    private static Handcuffs instance;
    public boolean
            cuffTake = true,
            burnCuffs = true,
            canPickup = false,
            nerfDamage = true,
            canChangeInv = false,
            reqOP = false,
            keyTake = false,
            usePerms = false;

    public EventListener eventListener;
    public List<Player> cuffed = new ArrayList<>();

    public static Handcuffs get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        loadConfig();

        eventListener = new EventListener();
        getServer().getPluginManager().registerEvents(eventListener, this);
    }

    @Override
    public void onDisable() {

    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("hc") && sender instanceof Player) {
            Player player = (Player) sender;

            if (args[0].equalsIgnoreCase("carry")) {
                if ((!reqOP || player.isOp()) && (!usePerms || player.hasPermission("hc.cmd.carry"))) {
                    Player target = getServer().getPlayer(args[1]);

                    if (target == null || !target.isOnline()) {
                        eventListener.tell(player, ChatColor.RED + "Spieler nicht gefunden!");
                        return true;
                    }

                    if (target.hasPermission("hc.immune")) {
                        eventListener.tell(player, ChatColor.RED + "Du hast nicht die Berechtigung das zu machen!");
                    }

                    if (eventListener.cuffed(target) && !eventListener.cuffed(player) && player.getLocation().distance(target.getLocation()) <= 10) {
                        player.setPassenger(target);

                        if (player.getPassenger() == null)
                            eventListener.tell(player, ChatColor.GREEN + "You have put down " + ChatColor.AQUA + target.getName());
                        else
                            eventListener.tell(player, ChatColor.GREEN + "You have picked up " + ChatColor.AQUA + target.getName());

                        return true;
                    }
                } else {
                    eventListener.tell(player, RED + "Du hast nicht die Berechtigung das zu machen!");
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("cuff")) {
                if ((!reqOP || player.isOp()) && (!usePerms || player.hasPermission("hc.cmd.cuff"))) {
                    Player target = getServer().getPlayer(args[1]);

                    if (target == null || !target.isOnline()) {
                        eventListener.tell(player, ChatColor.RED + "Spieler nicht gefunden!");
                        return true;
                    }

                    if (target.hasPermission("hc.immune")) {
                        eventListener.tell(player, ChatColor.RED + "Du kannst das bei diesem Spieler nicht machen!");
                    }

                    if (!eventListener.cuffed(target)) {
                        eventListener.cuff(target);
                        eventListener.tell(player, ChatColor.GREEN + "Du hast " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " Handschellen angelegt");
                        return true;
                    } else {
                        eventListener.tell(player, RED + "Dieser Spieler ist schon befreit!");
                        return true;
                    }
                } else {
                    eventListener.tell(player, RED + "Du hast nicht die Berechtigung das zu machen!");
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("free")) {
                if ((!reqOP || player.isOp()) && (!usePerms || player.hasPermission("hc.cmd.free"))) {
                    Player target = getServer().getPlayer(args[1]);

                    if (target == null || !target.isOnline()) {
                        eventListener.tell(player, ChatColor.RED + "Spieler nicht gefunden!");
                        return true;
                    }

                    if (eventListener.cuffed(target)) {
                        eventListener.free(target);
                        eventListener.tell(player, ChatColor.GREEN + "Du hast " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " die Handschellen abgenommen!");
                        return true;
                    } else {
                        eventListener.tell(player, RED + "Dieser Spieler ist schon befreit!");
                    }
                } else {
                    eventListener.tell(player, RED + "Du hast nicht die Berechtigung das zu machen!");
                    return true;
                }
            }
        }

        return false;
    }

    private void loadConfig() {
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);

        String path = "cuffID";
        if (this.getConfig().contains(path)) {

            try {
                cuffID = Material.valueOf(getConfig().getString(path));
            } catch (Exception e) {
                cuffID = Material.STRING;
            }
        }

        path = "cuffAmount";
        if (this.getConfig().contains(path)) {
            try {
                cuffAmount = getConfig().getInt(path);
            } catch (Exception e) {
                cuffAmount = 7;
            }
        }

        path = "cuffTake";
        if (this.getConfig().contains(path)) {
            try {
                cuffTake = this.getConfig().getBoolean(path);
            } catch (Exception e) {
                cuffTake = true;
            }
        }

        path = "nerfDamage";
        if (this.getConfig().contains(path)) {
            try {
                nerfDamage = this.getConfig().getBoolean(path);
            } catch (Exception e) {
                nerfDamage = true;
            }
        }

        path = "burnCuffs";
        if (this.getConfig().contains(path)) {
            try {
                burnCuffs = this.getConfig().getBoolean(path);
            } catch (Exception e) {
                burnCuffs = true;
            }
        }

        path = "canPickup";
        if (this.getConfig().contains(path)) {
            try {
                canPickup = this.getConfig().getBoolean(path);
            } catch (Exception e) {
                canPickup = false;
            }
        }

        path = "canChangeInv";
        if (this.getConfig().contains(path)) {
            try {
                canChangeInv = this.getConfig().getBoolean(path);
            } catch (Exception e) {
                canChangeInv = false;
            }
        }

        path = "reqOP";
        if (this.getConfig().contains(path)) {
            try {
                reqOP = this.getConfig().getBoolean(path);
            } catch (Exception e) {
                reqOP = false;
            }
        }

        path = "keyID";
        if (this.getConfig().contains(path)) {
            try {
                keyID = Material.valueOf(getConfig().getString(path));
            } catch (Exception e) {
                keyID = Material.SHEARS;
            }
        }

        path = "keyTake";
        if (this.getConfig().contains(path)) {
            try {
                keyTake = this.getConfig().getBoolean(path);
            } catch (Exception e) {
                keyTake = false;
            }
        }

        path = "usePerms";
        if (this.getConfig().contains(path)) {
            try {
                usePerms = this.getConfig().getBoolean(path);
            } catch (Exception e) {
                usePerms = false;
            }
        }

        this.saveConfig();
    }
}
