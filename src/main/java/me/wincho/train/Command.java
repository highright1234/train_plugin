package me.wincho.train;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Command implements CommandExecutor {

    private final Plugin plugin;

    public Command(Plugin plugin) {
        this.plugin=plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
        if (label.equals("create_train")) {
            if (args.length >= 4) {
                int x = Integer.parseInt(args[0]);
                int y = Integer.parseInt(args[1]);
                int z = Integer.parseInt(args[2]);
                int count = Integer.parseInt(args[3]);
                int speed = Integer.parseInt(args[4]);
                int max_user = Integer.parseInt(args[5]);
                int wait = 3;
                if (count > 10) {
                    if (sender instanceof Player) {
                        ((Player) sender).kick(Component.text("10개가 최대야 ㅁㅊㄴ아"));
                    }
                    return false;
                }
                for (int i = 0; i < count; i++) {
                    int finalI = i + 1;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        World world = Bukkit.getWorld("world");
                        Location location = new Location(world, x, y, z);
                        Minecart cart = Objects.requireNonNull(world).spawn(location.toCenterLocation(), Minecart.class);
                        Train.trainSpeed.put(cart.getUniqueId(), speed);
                        Train.trainMaxUsers.put(cart.getUniqueId(), max_user);
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
