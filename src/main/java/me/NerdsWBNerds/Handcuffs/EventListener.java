package me.NerdsWBNerds.Handcuffs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Random;

import static org.bukkit.ChatColor.*;

public class EventListener implements Listener {
    private static final Random RANDOM = new Random();
    private final Handcuffs plugin;

    EventListener() {
        plugin = Handcuffs.get();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!cuffed(player)) return;

        e.setCancelled(true);
        tell(player, RED + "Du kannst das nicht weil du Handschellen anhast!");
    }

    @EventHandler
    public void onRun(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();
        if (to == null) return;
        if (!cuffed(player)) return;
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onHurt(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();

            if (e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.LAVA && plugin.burnCuffs) {
                if (RANDOM.nextInt(20) == 0) {
                    if (cuffed(player)) {
                        tell(player, GREEN + "Feuer hat deine Handschellen geschmolzen, du bist frei!");
                        free(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player player = (Player) e.getDamager();
            if (cuffed(player) && plugin.nerfDamage) e.setDamage(e.getDamage() / 2);
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();
        if (cuffed(player) && !plugin.canPickup) e.setCancelled(true);
    }

    @EventHandler
    public void onChangeInv(InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();
        if (cuffed(player) && !plugin.canChangeInv) e.setCancelled(true);
    }

    @EventHandler
    public void onDismount(EntityDismountEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (cuffed(player)) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        if (cuffed(player)) return;

        if (e.getRightClicked() instanceof Player) {
            Player target = (Player) e.getRightClicked();
            if (!player.isOp() && plugin.reqOP) return;

            if (inHand(player, Handcuffs.cuffID)) {
                if (cuffed(target)) {
                    tell(player, RED + "Dieser Spieler hat bereits Handschellen an!");
                } else {
                    if (plugin.usePerms && !player.hasPermission("hc.cuff")) {
                        tell(player, RED + "Du hast nicht die Berechtigung das zu machen!");
                        return;
                    }

                    if (target.hasPermission("hc.immune")) {
                        tell(player, ChatColor.RED + "Du kannst das bei diesem Spieler nicht machen!");
                        return;
                    }

                    if (inHandAmount(player) >= Handcuffs.cuffAmount) {
                        tell(target, AQUA + player.getName() + GREEN + " hat dir Handschellen angelegt!");
                        tell(player, GREEN + "Du hast " + AQUA + target.getName() + GREEN + " Handschellen angelegt!");
                        cuff(target);

                        if (plugin.cuffTake) {
                            if (player.getItemInHand().getAmount() == Handcuffs.cuffAmount)
                                player.getItemInHand().setType(Material.AIR);
                            else
                                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - Handcuffs.cuffAmount);
                        }
                    } else {
                        tell(player, RED + "Du musst 7 Faden in der Hand halten um jemanden die Handschellen anzuglegen!");
                    }
                }
            }

            if (inHand(player, Handcuffs.keyID)) {
                if (!cuffed(target)) {
                    tell(player, RED + "Dieser Spieler ist schon befreit!");

                    if (plugin.keyTake) {
                        if (player.getItemInHand().getAmount() == 1)
                            player.getItemInHand().setType(Material.AIR);
                        else
                            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                    }
                } else {
                    if (plugin.usePerms && !player.hasPermission("hc.free")) {
                        tell(player, RED + "Du hast nicht die Berechtigung das zu machen!");
                        return;
                    }

                    tell(target, AQUA + player.getName() + GREEN + " hat deine Handschellen gelöst!");
                    tell(player, GREEN + "Du hast " + AQUA + target.getName() + GREEN + " die Handschellen abgenommen!");
                    free(target);
                }
            }
        }
    }

    public boolean cuffed(Player player) {
        return plugin.cuffed.contains(player);
    }

    public void cuff(Player player) {
        if (!cuffed(player)) plugin.cuffed.add(player);
    }

    public void free(Player player) {
        if (cuffed(player)) plugin.cuffed.remove(player);
    }

    public boolean inHand(Player player, Material m) {
        return player.getItemInHand().getType() == m;
    }

    public int inHandAmount(Player p) {
        return p.getItemInHand().getAmount();
    }

    public void tell(Player p, String m) {
        p.sendMessage(m);
    }
}
