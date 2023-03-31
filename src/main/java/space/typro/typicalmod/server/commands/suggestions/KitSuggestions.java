package space.typro.typicalmod.server.commands.suggestions;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import space.typro.typicalmod.server.handlers.KitHandler;
import space.typro.typicalmod.server.kit.KitManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static space.typro.typicalmod.TypicalMod.LOGGER;

public class KitSuggestions {
    public static CompletableFuture<Suggestions> getKitSuggest(CommandSource ctx, SuggestionsBuilder builder){
        ServerPlayerEntity player;
        try {
            player = ctx.getPlayerOrException();
        }catch (CommandSyntaxException e){
            LOGGER.error("Error when trying to create suggestions list: " + e.getMessage());
            return Suggestions.empty();
        }
        int userKitLvl = KitHandler.kitLevel.get(player.getUUID());
        /*
        KitManager.serverKits.forEach(kit -> {
            if (kit.getRequiredLvl() <= userKitLvl) kits.add(kit.name);
        });
         */

        List<String> list = new ArrayList<>();
        KitManager.serverKits.stream().filter(kit -> kit.getRequiredLvl() <= userKitLvl).forEach(kit -> list.add(kit.name));

        return ISuggestionProvider.suggest(list, builder);
    }
}
