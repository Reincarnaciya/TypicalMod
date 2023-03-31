package space.typro.typicalmod.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import space.typro.typicalmod.TypicalMod;
import space.typro.typicalmod.server.commands.Config.BaseConstants;
import space.typro.typicalmod.server.kit.KitManager;

import java.util.Locale;

import static space.typro.typicalmod.TypicalMod.LOGGER;
import static space.typro.typicalmod.server.commands.suggestions.KitSuggestions.getKitSuggest;


public class KitCommand {
    public KitCommand(CommandDispatcher<CommandSource> dispatcher){
        dispatcher.register(Commands.literal(BaseConstants.ROOT_CMD)
                .then(Commands.literal("kit")
                        .then(Commands.argument("kit", StringArgumentType.word())
                                .suggests((context, builder) -> getKitSuggest(context.getSource(), builder))
                                .executes(ctx -> kitGive(ctx.getSource(), StringArgumentType.getString(ctx, "kit").toLowerCase(Locale.ROOT)))
                        )
                )
        );
    }
    private int kitGive(CommandSource commandSource, String kit){
        ServerPlayerEntity player;
        try {
             player = commandSource.getPlayerOrException();
        }catch (CommandSyntaxException e){
            LOGGER.error("Ошибки при попытке выдачи кита: " + e.getMessage());
            return -1;
        }
        TypicalMod.executorService.submit(()-> KitManager.serverKits.forEach(kit1 -> {
            if (kit1.name.toLowerCase(Locale.ROOT).equals(kit)) KitManager.give(player, kit1);
        }));

        return 0;
    }
}
