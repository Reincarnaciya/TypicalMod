package space.typro.typicalmod.server.economy;

import net.minecraft.util.text.TranslationTextComponent;
import space.typro.typicalmod.server.utils.MessageUtils;
import space.typro.typicalmod.server.utils.PlayerUtils;

import java.util.UUID;

import static space.typro.typicalmod.server.handlers.playerHandlers.PlayerBalanceHandler.playerBalance;
import static space.typro.typicalmod.server.handlers.playerHandlers.PlayerBalanceHandler.playerBalanceCache;

public class EconomyLocalActions {
    public static void addBalance(UUID uuid, float balToAdd){
        if (balToAdd == 0f) return;
        playerBalance.replace(uuid, playerBalance.get(uuid) + balToAdd);
        playerBalanceCache.replace(uuid, playerBalanceCache.get(uuid) + balToAdd);
    }
    public static float getBalance(UUID uuid){
        return playerBalance.get(uuid);
    }
    public static void setBalance(UUID uuid, float balToSet){
        playerBalance.replace(uuid, balToSet);
        playerBalanceCache.replace(uuid, balToSet);
    }
    public static void withdraw(UUID uuid, float sum){
        setBalance(uuid, EconomyLocalActions.getBalance(uuid) - sum);
    }
    public static void MoneyTransfer(UUID from, UUID to, float sum){

        MessageUtils.sendMessage(from, new TranslationTextComponent("commands.moneyTransfer",
                PlayerUtils.getServerPlayerFromUUID(from).getName(), sum));
        EconomyLocalActions.withdraw(from, sum);
        EconomyLocalActions.addBalance(to, sum);
    }
}
