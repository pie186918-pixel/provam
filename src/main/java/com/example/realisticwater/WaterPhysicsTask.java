package com.example.realisticwater;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class WaterPhysicsTask extends BukkitRunnable {

    private final RealisticWater plugin;

    public WaterPhysicsTask(RealisticWater plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        List<Block> currentBlocks = new ArrayList<>(plugin.getActiveWaterBlocks());

        for (Block block : currentBlocks) {
            if (block.getType() != Material.WATER) {
                plugin.removeWaterBlock(block);
                continue;
            }

            if (!(block.getBlockData() instanceof Levelled waterData)) {
                continue;
            }

            int currentLevel = waterData.getLevel(); // 0 = Blocco pieno, 7 = Liquido quasi prosciugato

            // 1. GRAVITÀ: Controlla prima il blocco sottostante
            Block down = block.getRelative(BlockFace.DOWN);
            if (isPassable(down)) {
                // Sposta l'acqua verso il basso
                down.setType(Material.WATER);
                if (down.getBlockData() instanceof Levelled downData) {
                    downData.setLevel(currentLevel);
                    down.setBlockData(downData);
                }
                
                block.setType(Material.AIR);
                plugin.removeWaterBlock(block);
                plugin.addWaterBlock(down);
                continue;
            }

            // 2. ESPANSIONE LATERALE & SVUOTAMENTO: Se sotto è solido, prova a scivolare di lato
            List<Block> validSides = new ArrayList<>();
            for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}) {
                Block side = block.getRelative(face);
                if (isPassable(side)) {
                    validSides.add(side);
                }
            }

            if (!validSides.isEmpty() && currentLevel < 7) {
                // Ripartisce il livello e lo abbassa man mano che si espande
                int newLevel = currentLevel + 1; // Un livello numerico più alto corrisponde a meno acqua
                Block targetSide = validSides.get((int) (Math.random() * validSides.size()));

                targetSide.setType(Material.WATER);
                if (targetSide.getBlockData() instanceof Levelled sideData) {
                    sideData.setLevel(newLevel);
                    targetSide.setBlockData(sideData);
                    plugin.addWaterBlock(targetSide);
                }

                // Se l'acqua si è espansa troppo, il blocco di partenza si prosciuga gradualmente
                if (currentLevel >= 6) {
                    block.setType(Material.AIR);
                    plugin.removeWaterBlock(block);
                } else {
                    waterData.setLevel(newLevel);
                    block.setBlockData(waterData);
                }
            }
        }
    }

    private boolean isPassable(Block block) {
        return block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR || block.getType() == Material.WATER_CAULDRON;
    }
}
