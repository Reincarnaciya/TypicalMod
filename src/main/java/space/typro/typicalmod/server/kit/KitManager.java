package space.typro.typicalmod.server.kit;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import space.typro.typicalmod.TypicalMod;
import space.typro.typicalmod.server.database.ConstantsDatabase;
import space.typro.typicalmod.server.database.Database;
import space.typro.typicalmod.server.database.Table;
import space.typro.typicalmod.server.utils.MessageConstants;
import space.typro.typicalmod.server.utils.MessageUtils;
import space.typro.typicalmod.server.utils.PlayerUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static space.typro.typicalmod.TypicalMod.LOGGER;
import static space.typro.typicalmod.server.kit.Kit.*;

@Mod.EventBusSubscriber(modid = TypicalMod.MOD_ID, value = Dist.DEDICATED_SERVER, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KitManager {

    public static final HashMap<UUID, HashMap<Integer, Long>> timeToGivePlayers = new HashMap<>();
    public static final List<Kit> serverKits = new ArrayList<Kit>(){{
        add(START);
        add(VIP);
        add(PREMIUM);
        add(Kit.DELUX);
        add(Kit.LUX);
    }};


    public static void give(ServerPlayerEntity player, Kit kitToGive){
        if (kitToGive.getCost() != 0f) {
            giveWithCost(player, kitToGive);
            return;
        }
        if (timeToGive(player.getStringUUID(), kitToGive)){
            MessageUtils.sendMessage(player, new TranslationTextComponent("kit.gotKit", kitToGive.name));
            for (ItemStack item : kitToGive.getItems()){
                player.addItem(item.copy());
            }
            updateLastTime(player.getStringUUID(), kitToGive.id);

        }else {
            if (timeToGivePlayers.containsKey(player.getUUID()) && timeToGivePlayers.get(player.getUUID()).containsKey(kitToGive.id)){
                long timeLastGet = timeToGivePlayers.get(player.getUUID()).get(kitToGive.id);
                long timePassed = Calendar.getInstance().getTime().getTime() - timeLastGet;
                long timeRemain = kitToGive.getDelay() - timePassed;
                MessageUtils.sendMessage(PlayerUtils.getServerPlayerFromUUID(player.getUUID()),
                        MessageUtils.generateCommandMsg(
                                MessageConstants.COMMAND_KIT,
                                new TranslationTextComponent("kit.timeNeed", kitToGive.name, timeRemain/1000)
                        )
                );
            }
        }
    }
    private static void updateLastTime(String uuid, int id){
        try (Connection connection = Database.getConnection(ConstantsDatabase.TY_KITS_DB)){
            Table table = new Table(ConstantsDatabase.TYSCI_KITS_TABLE, connection);
            table.updateTableMass(new HashMap<String[], String[]>(){{
                put(new String[]{"UUID", "kitID"}, new String[]{uuid, String.valueOf(id)});
            }}, "last", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        }catch (SQLException e){
            LOGGER.error("Ошибка при попытке изменить последнее время выдачи кита: " + e);
        }
    }
    private static boolean timeToGive(String uuid, Kit kit){
        UUID puuid = UUID.fromString(uuid);
        if (timeToGivePlayers.containsKey(puuid) && timeToGivePlayers.get(puuid).containsKey(kit.id)){
            long timeLastGet = timeToGivePlayers.get(puuid).get(kit.id);
            long timePassed = Calendar.getInstance().getTime().getTime() - timeLastGet;
            long timeRemain = kit.getDelay() - timePassed;
            if (timeRemain <= 0) return true;
            if (timeToGivePlayers.containsKey(puuid)){
                if (timeToGivePlayers.get(puuid).containsKey(kit.id)){
                    timeToGivePlayers.get(puuid).replace(kit.id, timeLastGet);
                }else {
                    timeToGivePlayers.get(puuid).put(kit.id, timeLastGet);
                }
                return false;
            }else {
                timeToGivePlayers.put(puuid, new HashMap<Integer, Long>(){{
                    put(kit.id, timeLastGet);
                }});
            }
            return false;
        }
        try (Connection connection = Database.getConnection(ConstantsDatabase.TY_KITS_DB)){
            Table table = new Table(ConstantsDatabase.TYSCI_KITS_TABLE, connection);
            try (ResultSet set = table.request(new HashMap<String[], String[]>(){{
                put(new String[]{"uuid", "kitID"}, new String[]{uuid, String.valueOf(kit.id)});
            }})){
                Date date;
                if (set.next()){
                    date = set.getDate("last");
                }else{
                    table.addToTable(new HashMap<String[], String[]>(){{
                        put(new String[]{"uuid", "kitID"}, new String[]{uuid, String.valueOf(kit.id)});
                    }});
                    return true;
                }

                if (date == null) return true;
                long timePassed = Calendar.getInstance().getTime().getTime() - date.getTime();
                long timeRemain = kit.getDelay() - timePassed;
                if (timeRemain <= 0) return true;
                if (timeToGivePlayers.containsKey(puuid)){
                    if (timeToGivePlayers.get(puuid).containsKey(kit.id)){
                        timeToGivePlayers.get(puuid).replace(kit.id, date.getTime());
                    }else {
                        timeToGivePlayers.get(puuid).put(kit.id, date.getTime());
                    }
                    return false;
                }else {
                    timeToGivePlayers.put(puuid, new HashMap<Integer, Long>(){{
                        put(kit.id, date.getTime());
                    }});
                }
            }
        }catch (SQLException e){
            LOGGER.error("Ошибка порт попытке проверки времени последней выдачи кита: " + e.getMessage());
        }
        return false;
    }

    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event){
        timeToGivePlayers.remove(event.getPlayer().getUUID());
    }


    public static void unregisterKit(Kit kit){

    }
    private static void giveWithCost(ServerPlayerEntity player, Kit kit){
        //implement
    }

}
