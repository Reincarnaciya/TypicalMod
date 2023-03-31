package space.typro.typicalmod.server.handlers.playerHandlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import space.typro.typicalmod.TypicalMod;
import space.typro.typicalmod.server.database.ConstantsDatabase;
import space.typro.typicalmod.server.database.Database;
import space.typro.typicalmod.server.database.Table;
import space.typro.typicalmod.server.economy.EconomyDatabaseActions;
import space.typro.typicalmod.server.utils.PlayerUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE;
import static space.typro.typicalmod.TypicalMod.LOGGER;
import static space.typro.typicalmod.TypicalMod.executorService;

@Mod.EventBusSubscriber(modid = TypicalMod.MOD_ID, value = Dist.DEDICATED_SERVER, bus = FORGE)
public class PlayerBalanceHandler {
    public static final HashMap<UUID, Float> playerBalance = new HashMap<>();
    public static final HashMap<UUID, Float> playerBalanceCache = new HashMap<>();
    @SubscribeEvent
    public static void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event){
        UUID playerUUID = event.getPlayer().getUUID();
        executorService.submit(()-> {
            playerBalance.put(playerUUID, EconomyDatabaseActions.getBal(playerUUID));
            playerBalanceCache.put(playerUUID, 0f);
            LOGGER.info("Player with UUID " + playerUUID + " added to playersBalanceMap");
        });
    }

    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event){
        UUID playerUUID = event.getPlayer().getUUID();
        saveBalanceData(playerUUID);
        playerBalance.remove(playerUUID);
        playerBalanceCache.remove(playerUUID);
        LOGGER.info("Player with UUID " + playerUUID + " removed from playersBalanceMap");
    }
    public static void saveBalanceData(UUID player){
        float balToSet = playerBalanceCache.get(player);
        if (balToSet == 0f) return;
        executorService.submit(()->{
            EconomyDatabaseActions.addBal(PlayerUtils.getServerPlayerFromUUID(player), balToSet);
            playerBalanceCache.replace(player, 0f);
        });
    }
    @SubscribeEvent
    public static void saveBalanceData(WorldEvent.Save event){
        RegistryKey<World> dimension = ((World) event.getWorld()).dimension();
        if (!dimension.equals(World.OVERWORLD)) return;
        executorService.submit(()-> playerBalanceCache.forEach((player, money) ->{
            EconomyDatabaseActions.addBal(PlayerUtils.getServerPlayerFromUUID(player), money);
            playerBalanceCache.replace(player, 0f);

        }));

    }

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event){
        executorService.submit(()->{
            PlayerEntity player = event.getPlayer();
            if (player == null) {
                LOGGER.error("player not exist");
                return;
            }
            String uuid = player.getStringUUID();
            try (Connection connection = Database.getConnection(ConstantsDatabase.TYSCI_BALANCE_DATABASE)) {
                Table table = new Table(ConstantsDatabase.TYSCI_BALANCE_TABLE, connection);
                ResultSet result = table.request(new HashMap<String[], String[]>() {{
                    put(new String[]{"uuid"}, new String[]{uuid});
                }});
                if (result.next()) {
                    String balance = result.getString("balance");
                    if (result.next()) {
                        throw new SQLException("Users with uuid = " + uuid + " > 1");
                    }
                    // Если юзер есть в БД и при том только 1
                    LOGGER.info("Player balance with UUID = " + uuid + " equals " + balance);
                } else {
                    // Если юзера нет в БД
                    LOGGER.info("Player with uuid " + uuid + " not found. Creating..");
                    table.addToTable(new HashMap<String[], String[]>()
                    {
                        {
                            put(new String[]{"uuid"}, new String[]{uuid});
                        }
                    });
                }
                result.close();
            } catch (SQLException e) {
                LOGGER.error("Player join error: " + e.getMessage());
            }
        });


    }
}
