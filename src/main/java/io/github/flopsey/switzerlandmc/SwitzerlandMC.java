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

    private HeightDataProvider swissALTI3D;
    private HeightDataProvider swissSURFACE3D;

    @Override
    public @Nullable ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, @Nullable String id) {
        swissALTI3D = new XYZHeightDataProvider(this, "SWISSALTI3D_0.5_XYZ_CHLV95_LN02_%d_%d.xyz");
        swissSURFACE3D = new XYZHeightDataProvider(this, "swissSURFACE3D_Raster_0.5_xyz_CHLV95_LN02_%d_%d.xyz");
        return new SwitzerlandChunkGenerator(swissALTI3D, swissSURFACE3D);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("alti")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command currently only works for players");
                return false;
            }
            Location playerLocation = player.getLocation();
            float realTerrainAlti = swissALTI3D.getHeightAt(new Coordinates.MapCoords(new Coordinates.MinecraftCoords(playerLocation)));
            int mcTerrainAlti = SwitzerlandChunkGenerator.minecraftHeight(realTerrainAlti);
            sender.sendMessage("Terrain:", "Real altitude: " + realTerrainAlti, "Minecraft altitude: " + mcTerrainAlti);
            float realSurfaceAlti = swissSURFACE3D.getHeightAt(new Coordinates.MapCoords(new Coordinates.MinecraftCoords(playerLocation)));
            int mcSurfaceAlti = SwitzerlandChunkGenerator.minecraftHeight(realSurfaceAlti);
            sender.sendMessage("Surface:", "Real altitude: " + realSurfaceAlti, "Minecraft altitude: " + mcSurfaceAlti);
            return true;
        }
        return false;
    }

}
