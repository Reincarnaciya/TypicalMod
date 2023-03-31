package space.typro.typicalmod.server.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;

import java.util.concurrent.CompletableFuture;

public class ConsoleSuggestions {
    public static CompletableFuture<Suggestions> getHashMapSuggest(CommandContext<CommandSource> context, SuggestionsBuilder builder){
        String[] list = new String[]{
                "kitLvl",
                "playerBal",
                "playerJoinTime",
                "serverKits",
                "timeToGivePlayers"
        };
        return ISuggestionProvider.suggest(list, builder);
    }
}
