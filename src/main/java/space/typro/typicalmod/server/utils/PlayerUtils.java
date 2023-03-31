package space.typro.typicalmod.server.utils;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

public class PlayerUtils {

    public static ServerPlayerEntity getServerPlayerFromUUID(UUID playerUUID){
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerUUID);
    }

    public static ServerPlayerEntity getServerPlayerEntityFromUsername(String name){
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(name);
    }
}
