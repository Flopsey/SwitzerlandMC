package io.github.flopsey.switzerlandmc;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class SwitzerlandMC extends JavaPlugin {

    private HeightDataProvider heightDataProvider;

    @Override
    public @Nullable ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, @Nullable String id) {
        heightDataProvider = new XYZHeightDataProvider(this);
        return new SwitzerlandChunkGenerator(heightDataProvider);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("alti")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command currently only works for players");
                return false;
            }
            Location playerLocation = player.getLocation();
            float realAlti = heightDataProvider.getHeightAt(new Coordinates.MapCoords(new Coordinates.MinecraftCoords(playerLocation)));
            int mcAlti = SwitzerlandChunkGenerator.minecraftHeight(player.getWorld(), realAlti);
            sender.sendMessage("Real altitude: " + realAlti, "Minecraft altitude: " + mcAlti);
            return true;
        }
        return false;
    }

}
