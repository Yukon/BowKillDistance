package me.yukonapplegeek.bowkilldistance;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class BowKillDistance extends JavaPlugin implements Listener {
    public void onDisable() {
        // TODO: Place any custom disable code here.
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getLastDamageCause().getCause() == DamageCause.PROJECTILE && player.getKiller() instanceof Player) {
            this.getLogger().info(player.getMetadata("ShotLocationX").get(0).asString());
            //Checks to make sure the player has the arrow metadata
            if (player.hasMetadata("ShotLocationX") && player.hasMetadata("ShotLocationY") && player.hasMetadata("ShotLocationZ")) {
                Location shotLocation = new Location(player.getWorld(), player.getMetadata("ShotLocationX").get(0).asDouble(), player.getMetadata("ShotLocationY").get(0).asDouble(), player.getMetadata("ShotLocationZ").get(0).asDouble());
                int distance = (int) player.getLocation().distance(shotLocation);
                event.setDeathMessage(player.getDisplayName()+" was shot by "+player.getKiller().getDisplayName()+" ("+distance+" blocks)");
            }
        }
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            //Saves the players location to the arrow using metadata
            Location shotLocation = event.getEntity().getLocation();
            event.getProjectile().setMetadata("ShotLocationX", new FixedMetadataValue(this, shotLocation.getX()));
            event.getProjectile().setMetadata("ShotLocationY", new FixedMetadataValue(this, shotLocation.getY()));
            event.getProjectile().setMetadata("ShotLocationZ", new FixedMetadataValue(this, shotLocation.getZ()));
            this.getLogger().info(event.getProjectile().getMetadata("ShotLocationX").get(0).asString());
        }
    }

    @EventHandler
    public void onArrowHitPlayer(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Entity damageEntity = event.getDamager();
            Entity player = event.getEntity();

            if (damageEntity.hasMetadata("ShotLocationX") && damageEntity.hasMetadata("ShotLocationY") && damageEntity.hasMetadata("ShotLocationZ")) {
                if (player.hasMetadata("ShotLocationX") &&  player.hasMetadata("ShotLocationY") &&  player.hasMetadata("ShotLocationZ")) {

                    // Reset the current metadata to prevent huge list
                    player.removeMetadata("ShotLocationX", this);
                    player.removeMetadata("ShotLocationY", this);
                    player.removeMetadata("ShotLocationZ", this);
                }
                //Saves the new metadata to the player
                Location shotLocation = new Location(event.getEntity().getWorld(), damageEntity.getMetadata("ShotLocationX").get(0).asDouble(), damageEntity.getMetadata("ShotLocationY").get(0).asDouble(), damageEntity.getMetadata("ShotLocationZ").get(0).asDouble());
                player.setMetadata("ShotLocationX", new FixedMetadataValue(this, shotLocation.getX()));
                player.setMetadata("ShotLocationY", new FixedMetadataValue(this, shotLocation.getY()));
                player.setMetadata("ShotLocationZ", new FixedMetadataValue(this, shotLocation.getZ()));
            }
        }
    }
}