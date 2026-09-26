package com.kayji.giangsinh;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class GiangSinhPlugin extends JavaPlugin implements CommandExecutor, TabCompleter {

    private SnowIntensity intensity;
    private BukkitTask snowTask;
    private BukkitTask snowCoverTask;
    private final Map<String, Long> storedWorldTime = new HashMap<>();
    private final Map<String, Boolean> storedDaylight = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        intensity = SnowIntensity.fromString(getConfig().getString("default-intensity", "off"));
        if (intensity == null) {
            intensity = SnowIntensity.OFF;
            getConfig().set("default-intensity", intensity.getKey());
            saveConfig();
            getLogger().warning("default-intensity khong hop le, dat ve OFF.");
        }
        startSnowTask();
        PluginCommand command = getCommand("giangsinh");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        } else {
            getLogger().warning("Command giangsinh missing in plugin.yml");
        }
    }

    @Override
    public void onDisable() {
        stopSnowTask();
        stopSnowCoverTask();
        restoreDimWorld();
        if (getConfig().getBoolean("force-snowy-weather", true)) {
            clearWeather();
        }
    }

    private void startSnowTask() {
        stopSnowTask();
        stopSnowCoverTask();
        restoreDimWorld();
        if (intensity == null) {
            intensity = SnowIntensity.OFF;
        }
        if (intensity == SnowIntensity.OFF) {
            if (getConfig().getBoolean("force-snowy-weather", true)) {
                clearWeather();
            }
            return;
        }

        if (getConfig().getBoolean("force-snowy-weather", true)) {
            applyWeatherForSnowMode();
        }

        long period = getConfig().getLong("tick-period", 10L);
        snowTask = Bukkit.getScheduler().runTaskTimer(this, this::spawnSnow, 0L, period);
        startSnowCoverTask();
        applyDimWorld();
        getLogger().info("Snow mode set to " + intensity.getKey());
    }

    private void stopSnowTask() {
        if (snowTask != null) {
            snowTask.cancel();
            snowTask = null;
        }
    }

    private void startSnowCoverTask() {
        stopSnowCoverTask();
        if (!getConfig().getBoolean("cover-world-with-snow", true)) {
            return;
        }
        long period = getConfig().getLong("cover.tick-period", 40L);
        snowCoverTask = Bukkit.getScheduler().runTaskTimer(this, this::coverWorldWithSnow, 20L, period);
    }

    private void stopSnowCoverTask() {
        if (snowCoverTask != null) {
            snowCoverTask.cancel();
            snowCoverTask = null;
        }
    }

    private void spawnSnow() {
        var players = Bukkit.getOnlinePlayers();
        if (players.isEmpty()) {
            return;
        }

        double radius = getConfig().getDouble("spawn-radius", 24.0);
        double minHeight = getConfig().getDouble("spawn-height.min", 3.0);
        double maxHeight = getConfig().getDouble("spawn-height.max", Math.max(minHeight + 2.0, minHeight));
        if (maxHeight < minHeight) {
            maxHeight = minHeight;
        }
        int count = getParticleCount(intensity);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (Player player : players) {
            Location base = player.getLocation();
            World world = player.getWorld();
            for (int i = 0; i < count; i++) {
                double x = base.getX() + randomOffset(radius, random);
                double height = minHeight + random.nextDouble(0, Math.max(0.1, maxHeight - minHeight));
                double y = base.getY() + height;
                double z = base.getZ() + randomOffset(radius, random);
                world.spawnParticle(Particle.SNOWFLAKE, x, y, z, 1, 0, 0, 0, 0.0);
            }
        }
    }

    private double randomOffset(double radius, ThreadLocalRandom random) {
        return (random.nextDouble() * 2 - 1) * radius;
    }

    private int getParticleCount(SnowIntensity level) {
        return switch (level) {
            case HEAVY -> getConfig().getInt("heavy.particles-per-player", 48);
            case MEDIUM -> getConfig().getInt("medium.particles-per-player", 28);
            default -> getConfig().getInt("light.particles-per-player", 12);
        };
    }

    private void applyWeatherForSnowMode() {
        String mode = getConfig().getString("weather-mode", "snow").trim().toLowerCase(Locale.ROOT);
        if (mode.equals("clear") || mode.equals("sunny")) {
            clearWeather();
        } else {
            setSnowyWeather();
        }
    }

    private void coverWorldWithSnow() {
        int chunksPerWorld = getConfig().getInt("cover.chunks-per-world", 3);
        int placementsPerChunk = getConfig().getInt("cover.placements-per-chunk", 24);
        boolean freezeWater = getConfig().getBoolean("cover.freeze-water", false);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NETHER
                    || world.getEnvironment() == World.Environment.THE_END) {
                continue;
            }
            Chunk[] loaded = world.getLoadedChunks();
            if (loaded.length == 0) {
                continue;
            }
            int iterations = Math.min(chunksPerWorld, loaded.length);
            for (int i = 0; i < iterations; i++) {
                Chunk chunk = loaded[random.nextInt(loaded.length)];
                placeSnowInChunk(chunk, placementsPerChunk, freezeWater, random);
            }
        }
    }

    private void placeSnowInChunk(Chunk chunk, int placements, boolean freezeWater, ThreadLocalRandom random) {
        World world = chunk.getWorld();
        for (int i = 0; i < placements; i++) {
            int x = (chunk.getX() << 4) + random.nextInt(16);
            int z = (chunk.getZ() << 4) + random.nextInt(16);

            Block highest = world.getHighestBlockAt(x, z);
            Material topType = highest.getType();

            if (topType == Material.WATER || topType == Material.KELP || topType == Material.SEAGRASS) {
                if (freezeWater) {
                    highest.setType(Material.ICE, false);
                }
                continue;
            }

            Block target = highest;
            Block ground = highest.getRelative(BlockFace.DOWN);

            if (!target.isPassable()) {
                target = target.getRelative(BlockFace.UP);
                ground = highest;
            }

            if (!ground.getType().isSolid() || ground.isLiquid()) {
                continue;
            }

            if (!target.isEmpty() && target.getType() != Material.AIR && target.getType() != Material.CAVE_AIR) {
                continue;
            }

            target.setType(Material.SNOW, false);
        }
    }

    private void applyDimWorld() {
        if (!getConfig().getBoolean("dim-world.enabled", true)) {
            return;
        }
        long targetTime = getConfig().getLong("dim-world.time", 12000L);
        boolean freeze = getConfig().getBoolean("dim-world.freeze-time", true);
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NETHER
                    || world.getEnvironment() == World.Environment.THE_END) {
                continue;
            }
            storedWorldTime.putIfAbsent(world.getName(), world.getTime());
            Boolean currentCycle = world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);
            if (currentCycle != null) {
                storedDaylight.putIfAbsent(world.getName(), currentCycle);
            }
            world.setTime(targetTime);
            if (freeze) {
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            }
        }
    }

    private void restoreDimWorld() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NETHER
                    || world.getEnvironment() == World.Environment.THE_END) {
                continue;
            }
            if (storedWorldTime.containsKey(world.getName())) {
                world.setTime(storedWorldTime.remove(world.getName()));
            }
            if (storedDaylight.containsKey(world.getName())) {
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, storedDaylight.remove(world.getName()));
            }
        }
    }

    private void setSnowyWeather() {
        int duration = getConfig().getInt("weather-duration-ticks", 6000);
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NETHER
                    || world.getEnvironment() == World.Environment.THE_END) {
                continue;
            }
            world.setStorm(true);
            world.setThundering(false);
            world.setWeatherDuration(duration);
            world.setThunderDuration(duration);
        }
    }

    private void clearWeather() {
        int duration = getConfig().getInt("clear-weather-ticks", 6000);
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NETHER
                    || world.getEnvironment() == World.Environment.THE_END) {
                continue;
            }
            world.setStorm(false);
            world.setThundering(false);
            world.setWeatherDuration(duration);
            world.setThunderDuration(duration);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("giangsinh.toggle")) {
            sender.sendMessage(ChatColor.RED + "Ban khong co quyen dung lenh nay.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Dung: /" + label + " <nhe|vua|day|off|reload>");
            return true;
        }

        SnowIntensity chosen = SnowIntensity.fromString(args[0]);
        if ("reload".equalsIgnoreCase(args[0])) {
            if (!sender.hasPermission("giangsinh.reload")) {
                sender.sendMessage(ChatColor.RED + "Ban khong co quyen reload plugin nay.");
                return true;
            }
            reloadConfig();
            intensity = SnowIntensity.fromString(getConfig().getString("default-intensity", "off"));
            if (intensity == null) {
                intensity = SnowIntensity.OFF;
                getConfig().set("default-intensity", intensity.getKey());
                saveConfig();
                getLogger().warning("default-intensity khong hop le sau khi reload, dat ve OFF.");
            }
            startSnowTask();
            sender.sendMessage(ChatColor.GREEN + "Da reload GiangSinhSnow va ap dung cau hinh moi.");
            return true;
        }

        if (chosen == null) {
            sender.sendMessage(ChatColor.YELLOW + "Chon cuong do: nhe, vua, day, off.");
            return true;
        }

        intensity = chosen;
        getConfig().set("default-intensity", intensity.getKey());
        saveConfig();
        startSnowTask();

        switch (intensity) {
            case LIGHT -> sender.sendMessage(ChatColor.GREEN + "Da bat tuyet roi nhe.");
            case MEDIUM -> sender.sendMessage(ChatColor.GREEN + "Da bat tuyet roi vua vua.");
            case HEAVY -> sender.sendMessage(ChatColor.GREEN + "Da bat tuyet roi day, cuong do cao.");
            case OFF -> sender.sendMessage(ChatColor.YELLOW + "Da tat che do Giang Sinh, thoi tiet tro ve binh thuong.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String current = args[0].toLowerCase(Locale.ROOT);
            List<String> options = Arrays.asList("nhe", "vua", "day", "off", "reload", "light", "medium", "heavy");
            return options.stream()
                    .filter(opt -> opt.startsWith(current))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
