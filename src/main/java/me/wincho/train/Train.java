package me.wincho.train;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.Rail;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Train extends JavaPlugin {
    public static final int SPEED = 5;
    public static Map<UUID, Vector> trainTargetPos = new HashMap<>();

    @Override
    public void onEnable() {
        try {
            getConfig().load(new File(getDataFolder(), "config.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        if (getConfig().get("train_target_pos") != null)
            trainTargetPos = (HashMap<UUID, Vector>) getConfig().get("train_target_pos");
        getCommand("create_train").setExecutor(this);
        getCommand("/station").setExecutor(new Listener());
        Bukkit.getPluginManager().registerEvents(new Listener(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Entity entity : Bukkit.getWorld("world").getEntities()) {
                for (int i = 0; i < SPEED; i++) {
                    if (entity.getScoreboardTags().contains("train")) {
                        if (trainTargetPos.get(entity.getUniqueId()) == null)
                            trainTargetPos.put(entity.getUniqueId(), new Vector(0.1, 0, 0));
                        if (entity.getLocation().getBlock().getType().equals(Material.RAIL) || entity.getLocation().getBlock().getType().equals(Material.POWERED_RAIL)) {
                            Rail rail = (Rail) entity.getLocation().getBlock().getBlockData();
                            if (trainTargetPos.get(entity.getUniqueId()).getX() == 0.1) { //EAST
                                if (rail.getShape().equals(Rail.Shape.ASCENDING_EAST)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(0.1, 0.1, 0));
                                }
                                if (rail.getShape().equals(Rail.Shape.EAST_WEST)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(0.1, 0, 0));
                                } else if (rail.getShape().equals(Rail.Shape.SOUTH_WEST)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, 0.1));
                                } else if (rail.getShape().equals(Rail.Shape.NORTH_WEST)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, -0.1));
                                }
                            } else if (trainTargetPos.get(entity.getUniqueId()).getZ() == 0.1) { //SOUTH
                                if (rail.getShape().equals(Rail.Shape.NORTH_SOUTH)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, 0.1));
                                } else if (rail.getShape().equals(Rail.Shape.NORTH_WEST)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(-0.1, 0, 0));
                                } else if (rail.getShape().equals(Rail.Shape.NORTH_EAST)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(0.1, 0, 0));
                                }
                                if (rail.getShape().equals(Rail.Shape.ASCENDING_SOUTH)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0.1, 0.1));
                                } else {
                                    trainTargetPos.put(entity.getUniqueId(), trainTargetPos.get(entity.getUniqueId()).setY(0));
                                }
                            } else if (trainTargetPos.get(entity.getUniqueId()).getX() == -0.1) { //WEST
                                if (rail.getShape().equals(Rail.Shape.EAST_WEST)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(-0.1, 0, 0));
                                } else if (rail.getShape().equals(Rail.Shape.NORTH_EAST)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, -0.1));
                                } else if (rail.getShape().equals(Rail.Shape.SOUTH_EAST)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, 0.1));
                                }
                                if (rail.getShape().equals(Rail.Shape.ASCENDING_WEST)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(-0.1, 0.1, 0));
                                } else {
                                    trainTargetPos.put(entity.getUniqueId(), trainTargetPos.get(entity.getUniqueId()).setY(0));
                                }
                            } else if (trainTargetPos.get(entity.getUniqueId()).getZ() == -0.1) {//NORTH
                                if (rail.getShape().equals(Rail.Shape.NORTH_SOUTH)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, -0.1));
                                } else if (rail.getShape().equals(Rail.Shape.SOUTH_EAST)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(0.1, 0, 0));
                                } else if (rail.getShape().equals(Rail.Shape.SOUTH_WEST)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(-0.1, 0, 0));
                                }
                                if (rail.getShape().equals(Rail.Shape.ASCENDING_NORTH)) {
                                    trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0.1, -0.1));
                                } else {
                                    trainTargetPos.put(entity.getUniqueId(), trainTargetPos.get(entity.getUniqueId()).setY(0));
                                }
                            }

                        }

                        if (getConfig().get(entity.getLocation().getBlockX() + "_" + entity.getLocation().getBlockY() + "_" + entity.getLocation().getBlockZ()) != null) {
                            String data = (String) getConfig().get(entity.getLocation().getBlockX() + "_" + entity.getLocation().getBlockY() + "_" + entity.getLocation().getBlockZ());
                            if (data != null) {
                                if (String.valueOf((float) Math.round(entity.getLocation().getX() * 10) / 10).endsWith("5") && String.valueOf((float) Math.round(entity.getLocation().getZ() * 10) / 10).endsWith("5")) {
                                    entity.teleport(entity.getLocation().clone().add(trainTargetPos.get(entity.getUniqueId())));
                                    int delay = 300;
                                    if (data.equals(RailData.STATION1.name()) && entity.getScoreboardTags().contains("1")) {
                                        Vector org = trainTargetPos.get(entity.getUniqueId());
                                        trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, 0));
                                        Bukkit.getScheduler().runTaskLater(this, () -> {
                                            trainTargetPos.put(entity.getUniqueId(), org);
                                        }, delay);
                                    } else if (data.equals(RailData.STATION2.name()) && entity.getScoreboardTags().contains("2")) {
                                        Vector org = trainTargetPos.get(entity.getUniqueId());
                                        trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, 0));
                                        Bukkit.getScheduler().runTaskLater(this, () -> {
                                            trainTargetPos.put(entity.getUniqueId(), org);
                                        }, delay);
                                    } else if (data.equals(RailData.STATION3.name()) && entity.getScoreboardTags().contains("3")) {
                                        Vector org = trainTargetPos.get(entity.getUniqueId());
                                        trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, 0));
                                        Bukkit.getScheduler().runTaskLater(this, () -> {
                                            trainTargetPos.put(entity.getUniqueId(), org);
                                        }, delay);
                                    } else if (data.equals(RailData.STATION4.name()) && entity.getScoreboardTags().contains("4")) {
                                        Vector org = trainTargetPos.get(entity.getUniqueId());
                                        trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, 0));
                                        Bukkit.getScheduler().runTaskLater(this, () -> {
                                            trainTargetPos.put(entity.getUniqueId(), org);
                                        }, delay);
                                    } else if (data.equals(RailData.STATION5.name()) && entity.getScoreboardTags().contains("5")) {
                                        Vector org = trainTargetPos.get(entity.getUniqueId());
                                        trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, 0));
                                        Bukkit.getScheduler().runTaskLater(this, () -> {
                                            trainTargetPos.put(entity.getUniqueId(), org);
                                        }, delay);
                                    } else if (data.equals(RailData.STATION6.name()) && entity.getScoreboardTags().contains("6")) {
                                        Vector org = trainTargetPos.get(entity.getUniqueId());
                                        trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, 0));
                                        Bukkit.getScheduler().runTaskLater(this, () -> {
                                            trainTargetPos.put(entity.getUniqueId(), org);
                                        }, delay);
                                    } else if (data.equals(RailData.STATION7.name()) && entity.getScoreboardTags().contains("7")) {
                                        Vector org = trainTargetPos.get(entity.getUniqueId());
                                        trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, 0));
                                        Bukkit.getScheduler().runTaskLater(this, () -> {
                                            trainTargetPos.put(entity.getUniqueId(), org);
                                        }, delay);
                                    } else if (data.equals(RailData.STATION8.name()) && entity.getScoreboardTags().contains("8")) {
                                        Vector org = trainTargetPos.get(entity.getUniqueId());
                                        trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, 0));
                                        Bukkit.getScheduler().runTaskLater(this, () -> {
                                            trainTargetPos.put(entity.getUniqueId(), org);
                                        }, delay);
                                    } else if (data.equals(RailData.STATION9.name()) && entity.getScoreboardTags().contains("9")) {
                                        Vector org = trainTargetPos.get(entity.getUniqueId());
                                        trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, 0));
                                        Bukkit.getScheduler().runTaskLater(this, () -> {
                                            trainTargetPos.put(entity.getUniqueId(), org);
                                        }, delay);
                                    } else if (data.equals(RailData.STATION10.name()) && entity.getScoreboardTags().contains("10")) {
                                        Vector org = trainTargetPos.get(entity.getUniqueId());
                                        trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, 0));
                                        Bukkit.getScheduler().runTaskLater(this, () -> {
                                            trainTargetPos.put(entity.getUniqueId(), org);
                                        }, delay);
                                    }
                                }
                            }
                        }

                        entity.teleport(entity.getLocation().clone().add(trainTargetPos.get(entity.getUniqueId())));

                        ((Minecart) entity).setMaxSpeed(0);
                    }
                }
            }
        }, 0, 0);
    }

    @Override
    public void onDisable() {
        try {
            getConfig().save(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        getConfig().set("train_target_pos", trainTargetPos);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (label.equals("create_train")) {
            if (args.length >= 4) {
                int x = Integer.parseInt(args[0]);
                int y = Integer.parseInt(args[1]);
                int z = Integer.parseInt(args[2]);
                int count = Integer.parseInt(args[3]);
                int wait = 3;
                if (count > 10) {
                    if (sender instanceof Player) {
                        ((Player) sender).kick(Component.text("10개가 최대야 ㅁㅊㄴ아"));
                    }
                    return false;
                }
                for (int i = 0; i < count; i++) {
                    int finalI = i + 1;
                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        World world = Bukkit.getWorld("world");
                        Location location = new Location(world, x, y, z);
                        Minecart cart = world.spawn(location.toCenterLocation(), Minecart.class);
                        cart.addScoreboardTag(String.valueOf(finalI));
                        cart.addScoreboardTag("train");
                    }, wait);
                    wait += 3;
                }
                return true;
            }
        }
        return false;
    }
}
