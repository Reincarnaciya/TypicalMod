package space.typro.typicalmod.server.economy;

import net.minecraft.entity.player.PlayerEntity;
import space.typro.typicalmod.server.database.ConstantsDatabase;
import space.typro.typicalmod.server.database.Database;
import space.typro.typicalmod.server.database.Table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import static space.typro.typicalmod.TypicalMod.LOGGER;

public class EconomyDatabaseActions {
    public static void addBal(PlayerEntity player, float balance){
        new Thread(()->{
            UUID uuid = player.getUUID();
            float currentBalance = getBal(uuid);
            if (Float.isNaN(currentBalance)) return;
            try (Connection connection = Database.getConnection(ConstantsDatabase.TYSCI_BALANCE_DATABASE)) {
                Table table = new Table(ConstantsDatabase.TYSCI_BALANCE_TABLE, connection);
                if (table.check(uuid.toString())){
                    double balToSet;
                    try {
                        balToSet = currentBalance + balance;
                    }catch (NumberFormatException e){
                        throw new SQLException(e);
                    }
                    LOGGER.info("Add balance to player with UUID = " + uuid + " - " + balance);
                    table.updateTable("balance", String.valueOf(balToSet), uuid.toString());
                }else {
                    LOGGER.error("uuid "+uuid+" not found");
                }
            } catch (SQLException e) {
                LOGGER.error("Error adding balance: " + e.getMessage());
            }
        }).start();
    }

    public static float getBal(UUID uuid){
        try (Connection connection = Database.getConnection(ConstantsDatabase.TYSCI_BALANCE_DATABASE)) {
            Table table = new Table(ConstantsDatabase.TYSCI_BALANCE_TABLE, connection);
            if (table.check(uuid.toString())){
                try (ResultSet set = table.request(new HashMap<String[], String[]>() {{
                    put(new String[]{"UUID"}, new String[]{uuid.toString()});
                }})) {
                    if (set.next()) {
                        return set.getFloat("balance");
                    }else {
                        table.addToTable(new HashMap<String[], String[]>(){{
                            put(new String[]{"UUID"}, new String[]{uuid.toString()});
                        }});
                    }
                }
            }else {
                LOGGER.error("uuid "+uuid+" not found");
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting balance: " + e.getMessage());
        }
        return Float.NaN;
    }
}
