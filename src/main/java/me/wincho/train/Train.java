package me.wincho.train;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.Rail;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Train extends JavaPlugin {
    public static Map<UUID, Vector> trainTargetPos = new HashMap<>();
    public static Map<UUID, Integer> trainSpeed = new HashMap<>();
    public static Map<UUID, Integer> trainMaxUsers = new HashMap<>();

    private double asdf(double input) {
        if (input==0) {
            return 0.0;
        } else if (input>0) {
            return 0.1;
        }
        return -0.1;
    }
    @Override
    public void onEnable() {


        try {
            getConfig().load(new File(getDataFolder(), "config.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        if (getConfig().get("train_target_pos") != null)
            trainTargetPos = (HashMap<UUID, Vector>) getConfig().get("train_target_pos");
        if (getConfig().get("train_speed") != null)
            trainSpeed = (Map<UUID, Integer>) getConfig().get("train_speed");
        if (getConfig().get("train_max_users") != null)
            trainMaxUsers = (Map<UUID, Integer>) getConfig().get("train_max_users");
        Objects.requireNonNull(getCommand("create_train")).setExecutor(new me.wincho.train.Command(this));
        Objects.requireNonNull(getCommand("/station")).setExecutor(new Listener());
        Bukkit.getPluginManager().registerEvents(new Listener(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Entity entity : Objects.requireNonNull(Bukkit.getWorld("world")).getEntities()) {
                if (trainSpeed.get(entity.getUniqueId())!=null) {
                    for (int i = 0; i < trainSpeed.get(entity.getUniqueId()); i++) {
                        if (entity.getScoreboardTags().contains("train")) {
                            if (trainTargetPos.get(entity.getUniqueId()) == null)
                                trainTargetPos.put(entity.getUniqueId(), new Vector(0.1, 0, 0));

                            if (entity.getLocation().getBlock().getType().equals(Material.RAIL) || entity.getLocation().getBlock().getType().equals(Material.POWERED_RAIL)) {
                                selectPath((Rail) entity.getLocation().getBlock().getBlockData(), entity);
                            }

                            if (getConfig().get(entity.getLocation().getBlockX() + "_" + entity.getLocation().getBlockY() + "_" + entity.getLocation().getBlockZ()) != null) {
                                String data = (String) getConfig().get(entity.getLocation().getBlockX() + "_" + entity.getLocation().getBlockY() + "_" + entity.getLocation().getBlockZ());
                                if (data != null) {
                                    if (String.valueOf((float) Math.round(entity.getLocation().getX() * 10) / 10).endsWith("5") && String.valueOf((float) Math.round(entity.getLocation().getZ() * 10) / 10).endsWith("5")) {
                                        entity.teleport(entity.getLocation().clone().add(trainTargetPos.get(entity.getUniqueId())));
                                        int delay = 300;
                                        for (int j=1; j<=10; j++) {
                                            if (data.equals("STATION" + RailData.fromId(i)) && entity.getScoreboardTags().contains(Integer.toString(i))) {
                                                Vector org = trainTargetPos.get(entity.getUniqueId());
                                                trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, 0));
                                                Bukkit.getScheduler().runTaskLater(this, () -> trainTargetPos.put(entity.getUniqueId(), org), delay);
                                            }
                                        }
                                    }
                                }
                            }

                            entity.teleport(entity.getLocation().clone().add(trainTargetPos.get(entity.getUniqueId())));

                            ((Minecart) entity).setMaxSpeed(0);
                        }
                    }
                }
            }
        }, 0, 0);
    }
    private void selectPath(Rail rail, Entity entity) {
        Vector getTrainTargetPos = trainTargetPos.get(entity.getUniqueId());
        double pos = asdf(getTrainTargetPos.getX());
        if (Math.abs(getTrainTargetPos.getX()) == 0.1) {
            if (rail.getShape().equals(Rail.Shape.ASCENDING_EAST)) {
                trainTargetPos.put(entity.getUniqueId(), new Vector(pos, pos, 0));
            }
            if (rail.getShape().equals(Rail.Shape.EAST_WEST)) {
                trainTargetPos.put(entity.getUniqueId(), new Vector(pos, 0, 0));
            } else if (rail.getShape().equals(Rail.Shape.SOUTH_WEST)) {
                trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, pos));
            } else if (rail.getShape().equals(Rail.Shape.NORTH_WEST)) {
                trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, -pos));
            }

        } else if (Math.abs(getTrainTargetPos.getZ()) == 0.1) {
            if (rail.getShape().equals(Rail.Shape.NORTH_SOUTH)) {
                trainTargetPos.put(entity.getUniqueId(), new Vector(0, 0, pos));
            } else if (rail.getShape().equals(Rail.Shape.NORTH_WEST)) {
                trainTargetPos.put(entity.getUniqueId(), new Vector(-pos, 0, 0));
            } else if (rail.getShape().equals(Rail.Shape.NORTH_EAST)) {
                trainTargetPos.put(entity.getUniqueId(), new Vector(0.1, 0, 0));
            }
            if (rail.getShape().equals(Rail.Shape.ASCENDING_SOUTH)) {
                trainTargetPos.put(entity.getUniqueId(), new Vector(0, pos, pos));
            } else {
                trainTargetPos.put(entity.getUniqueId(), trainTargetPos.get(entity.getUniqueId()).setY(0));
            }
        }
    }

    @Override
    public void onDisable() {
        try {
            getConfig().save(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        getConfig().set("train_target_pos", trainTargetPos);
        getConfig().set("train_speed", trainSpeed);
        getConfig().set("train_max_users", trainMaxUsers);
    }
}
