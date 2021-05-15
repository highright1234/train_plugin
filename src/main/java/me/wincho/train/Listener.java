package me.wincho.train;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Listener implements org.bukkit.event.Listener, CommandExecutor {
    public static Map<UUID, Integer> trainUsers = new HashMap<>();
    private static Block selectedBlock;

    @EventHandler
    public void playerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if (entity.getScoreboardTags().contains("train")) {
            if (trainUsers.get(entity.getUniqueId()) == null) trainUsers.put(entity.getUniqueId(), 1);
            else {
                if (trainUsers.get(entity.getUniqueId()) < Train.trainMaxUsers.get(entity.getUniqueId())) {
                    trainUsers.put(entity.getUniqueId(), trainUsers.get(entity.getUniqueId()) + 1);
                    player.setGameMode(GameMode.SPECTATOR);
                    player.setSpectatorTarget(entity);
                } else {
                    trainUsers.put(entity.getUniqueId(), trainUsers.get(entity.getUniqueId()));
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            if (player.getSpectatorTarget() != null) {
                if (player.getSpectatorTarget().getType().equals(EntityType.MINECART)) {
                    trainUsers.put(player.getSpectatorTarget().getUniqueId(), trainUsers.get(player.getSpectatorTarget().getUniqueId()) - 1);
                    player.setGameMode(GameMode.SURVIVAL);
                }
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getType().equals(Material.RAIL) || block.getType().equals(Material.POWERED_RAIL)) {
            if (player.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_HOE)) {
                selectedBlock = block;
                event.setCancelled(true);
                player.sendMessage(Component.text(ChatColor.GREEN + "블록 지정 성공!"));
            }
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (selectedBlock == null) sender.sendMessage(Component.text(ChatColor.RED + "블록을 먼저 선택해주세요."));
        Plugin plugin = Train.getPlugin(Train.class);
        if (label.equals("/station")) {
            String loc = selectedBlock.getLocation().getBlockX() + "_" + selectedBlock.getLocation().getBlockY() + "_" + selectedBlock.getLocation().getBlockZ();
            if (args.length >= 1) {
                if (args[0].equals("rm")) {
                    plugin.getConfig().set(loc, null);
                    sender.sendMessage(Component.text(ChatColor.GREEN + "정차구간 제거 성공."));
                    return true;
                }
                for (int i = 1; i <= 10; i++) {
                    if (args[0].equals(Integer.toString(i))) {
                        plugin.getConfig().set(loc, RailData.fromId(i));
                        sender.sendMessage(Component.text(ChatColor.GREEN + args[0] + "번째 칸의 정차구간으로 지정되었습니다!"));
                    }
                }
            }
        }
        return false;
    }
}
