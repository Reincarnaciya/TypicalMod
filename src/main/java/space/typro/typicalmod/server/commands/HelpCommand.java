package space.typro.typicalmod.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import space.typro.typicalmod.server.commands.Config.BaseConstants;
import space.typro.typicalmod.server.utils.MessageConstants;
import space.typro.typicalmod.server.utils.MessageUtils;

public class HelpCommand {
    public HelpCommand(CommandDispatcher<CommandSource> dispatcher){
        dispatcher.register(Commands.literal(BaseConstants.ROOT_CMD)
                .then(Commands.literal("help")
                        .executes((command) -> Help(command.getSource()))
                )
                .executes((command) -> Help(command.getSource()))
        );
    }

    private int Help(CommandSource source) throws CommandSyntaxException {
        MessageUtils.sendMessage(source.getPlayerOrException(),
                MessageUtils.generateCommandMsg(
                        MessageConstants.COMMAND_HELP,
                        new TranslationTextComponent("commands.help")
                )
        );
        return 0;
    }

}
