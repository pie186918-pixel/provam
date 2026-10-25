package com.example.realisticwater;

import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class RealisticWater extends JavaPlugin {

    // Insieme di tutti i blocchi d'acqua tracciati
    private final Set<Block> activeWaterBlocks = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void onEnable() {
        // Registra gli eventi
        getServer().getPluginManager().registerEvents(new WaterListener(this), this);

        // Avvia il loop di simulazione della fisica dell'acqua ogni 2 tick per ottimizzare le prestazioni
        new WaterPhysicsTask(this).runTaskTimer(this, 2L, 2L);

        getLogger().info("RealisticWater avviato con successo su Paper 1.21.4!");
    }

    @Override
    public void onDisable() {
        activeWaterBlocks.clear();
    }

    public Set<Block> getActiveWaterBlocks() {
        return activeWaterBlocks;
    }

    public void addWaterBlock(Block block) {
        if (block != null && block.getType() == org.bukkit.Material.WATER) {
            activeWaterBlocks.add(block);
        }
    }

    public void removeWaterBlock(Block block) {
        activeWaterBlocks.remove(block);
    }
}
