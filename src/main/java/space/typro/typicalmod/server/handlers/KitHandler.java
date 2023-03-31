package space.typro.typicalmod.server.handlers;


import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import space.typro.typicalmod.TypicalMod;
import space.typro.typicalmod.server.database.ConstantsDatabase;
import space.typro.typicalmod.server.database.Database;
import space.typro.typicalmod.server.database.Table;
import space.typro.typicalmod.server.utils.PlayerUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import static space.typro.typicalmod.TypicalMod.LOGGER;

@Mod.EventBusSubscriber(modid = TypicalMod.MOD_ID, value = Dist.DEDICATED_SERVER, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KitHandler {
    public static HashMap<UUID, Integer> kitLevel = new HashMap<>();


    @SubscribeEvent
    public static void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event){
        ServerPlayerEntity player = PlayerUtils.getServerPlayerFromUUID(event.getPlayer().getUUID());
        if (player == null) return;
        String uuid = event.getPlayer().getStringUUID();
        TypicalMod.executorService.submit(()->{
            try(Connection connection = Database.getConnection(ConstantsDatabase.TY_PRIVILEGE_DB)) {
                Table table = new Table(ConstantsDatabase.TY_PRIVILEGE_TABLE, connection);

                int lvl;
                try (ResultSet set = table.request(new HashMap<String[], String[]>() {{
                    put(new String[]{"username"}, new String[]{event.getPlayer().getName().getString()});
                }})) {
                    if (set.next()) {
                        lvl = set.getInt("donate");
                    } else {
                        return;
                    }
                }
                kitLevel.put(UUID.fromString(uuid), lvl);
            }catch (SQLException e){
                LOGGER.error("Error when trying to create a record in the database " + e.getMessage());
            }
        });
    }
    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event){
        kitLevel.remove(event.getPlayer().getUUID());
    }

}
