package com.griefcraft.listeners;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

public class LWCBreakListener implements Listener {
    public LWCBreakListener(LWCPlugin plugin) {
        this.plugin = plugin;
    }

    private final LWCPlugin plugin;

    @EventHandler
    public void onPistonExpand(BlockPistonExtendEvent event) {
        BlockFace direction = event.getDirection();
        onPiston(event, direction, false);
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        BlockFace direction = event.getDirection();

        onPiston(event, direction, true);
    }

    private void onPiston(BlockPistonEvent event, BlockFace direction, boolean isRetract) {
        // 粘着ピストン && おす -> 処理する
        // 粘着ピストン && もどす -> 処理する
        // ピストン && おす -> 処理する
        // ピストン && 戻す -> 処理しない
        if(isRetract && !event.isSticky()) return;
        LWC lwc = plugin.getLWC();

        for(int i = 0; i < 12; i++) {
            Block targetBlock = event.getBlock()
                    .getLocation()
                    .add(
                            direction.getModX() * i,
                            direction.getModY() * i,
                            direction.getModZ() * i
                    ).getBlock();

            if(targetBlock.getType() == Material.AIR) {
                break;
            }

            if (lwc.isProtectable(targetBlock)) {
                Protection protection = lwc.findProtection(targetBlock);
                if (protection != null) {
                    event.setCancelled(true);
                }
            }

            // for redstone-wire, comparator, etc
            Block topBlock = event.getBlock()
                    .getLocation()
                    .add(
                            direction.getModX() * i,
                            direction.getModY() * i + 1,
                            direction.getModZ() * i
                    ).getBlock();

            if (lwc.isProtectable(topBlock)) {
                Protection protection = lwc.findProtection(topBlock);
                if(protection != null) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onFromTo(BlockFromToEvent event) {
        LWC lwc = plugin.getLWC();

        if(lwc.isProtectable(event.getToBlock())) {
            Protection protection = lwc.findProtection(event.getToBlock());
            if(protection != null) {
                event.setCancelled(true);
            }
        }
    }
}
