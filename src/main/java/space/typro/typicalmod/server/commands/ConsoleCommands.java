package space.typro.typicalmod.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import space.typro.typicalmod.server.commands.Config.AdminConstans;
import space.typro.typicalmod.server.commands.suggestions.ConsoleSuggestions;
import space.typro.typicalmod.server.handlers.playerHandlers.PlayerBalanceHandler;
import space.typro.typicalmod.server.handlers.playerHandlers.PlayerTimedPayoutHandler;
import space.typro.typicalmod.server.handlers.KitHandler;
import space.typro.typicalmod.server.kit.KitManager;
import space.typro.typicalmod.server.utils.MessageUtils;

import java.util.Arrays;
import java.util.Locale;

public class ConsoleCommands {

    public ConsoleCommands(CommandDispatcher<CommandSource> dispatcher){
        dispatcher.register(Commands.literal(AdminConstans.ROOT_CONSOLE).requires(commandSource -> commandSource.hasPermission(4))
                .then(Commands.literal("getHashMap")
                        .then(Commands.argument("hashmap", StringArgumentType.word()).suggests((ConsoleSuggestions::getHashMapSuggest))
                                .executes(ctx -> getHashMap(ctx.getSource(), StringArgumentType.getString(ctx, "hashmap")))
                        )
                )
        );
    }

    private int getHashMap(CommandSource source, String hashmap){
        switch (hashmap.toLowerCase(Locale.ROOT)){
            case "kitlvl":
                KitHandler.kitLevel.forEach((uuid, lvl) ->{
                    MessageUtils.sendCmdFeedback(source, new StringTextComponent("UUID " + uuid + " = " + lvl));
                });
                break;
            case "playerbal":
                PlayerBalanceHandler.playerBalance.forEach(((uuid, aFloat) -> {
                    MessageUtils.sendCmdFeedback(source, new StringTextComponent("UUID " + uuid + " bal = " + aFloat + " balCache = " + PlayerBalanceHandler.playerBalanceCache.get(uuid)));
                }));
                break;
            case "playerjointime":
                PlayerTimedPayoutHandler.playersJoinTime.forEach(((player, aLong) -> {
                    MessageUtils.sendCmdFeedback(source, new StringTextComponent("UUID " + player.getStringUUID() + " joiTime = " + aLong));
                }));
                break;
            case "serverkits":
                KitManager.serverKits.forEach(kit -> {
                    MessageUtils.sendCmdFeedback(source, new StringTextComponent("KIT " + kit.name + " is = " + Arrays.toString(kit.getItems())));
                });
                break;
            case "timetogiveplayers":
                KitManager.timeToGivePlayers.forEach((uuid, integerLongHashMap) -> {
                    MessageUtils.sendMessage(uuid, new StringTextComponent("uuid " + uuid));
                    integerLongHashMap.forEach((integer, aLong) -> {
                        MessageUtils.sendMessage(uuid, new StringTextComponent("kitId = " + integer + " remaining time = " + aLong));
                    });
                });
        }
        return 0;
    }
}
