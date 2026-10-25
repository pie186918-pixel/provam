package com.example.realisticwater;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class WaterListener implements Listener {

    private final RealisticWater plugin;

    public WaterListener(RealisticWater plugin) {
        this.plugin = plugin;
    }

    // 1. BLOCCHIAMO la fisica nativa di Minecraft (niente sorgenti infinite vanilla o espansioni automatiche)
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVanillaWaterFlow(BlockFromToEvent event) {
        if (event.getBlock().getType() == Material.WATER) {
            event.setCancelled(true);
            plugin.addWaterBlock(event.getBlock());
        }
    }

    // 2. Aggiungiamo al tracciamento l'acqua svuotata da un secchio
    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.getBucket() == Material.WATER_BUCKET) {
            Block target = event.getBlock();
            plugin.getServer().getScheduler().runTask(plugin, () -> plugin.addWaterBlock(target));
        }
    }

    // 3. Rimuoviamo il blocco se viene posizionato qualcosa al suo posto o se viene rotto
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        plugin.removeWaterBlock(event.getBlock());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        plugin.removeWaterBlock(event.getBlockPlaced());
    }
}
