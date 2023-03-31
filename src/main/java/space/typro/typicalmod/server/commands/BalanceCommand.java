package space.typro.typicalmod.server.commands;



import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import space.typro.typicalmod.server.commands.Config.BaseConstants;
import space.typro.typicalmod.server.economy.EconomyLocalActions;
import space.typro.typicalmod.server.utils.MessageConstants;
import space.typro.typicalmod.server.utils.MessageUtils;

import static space.typro.typicalmod.TypicalMod.LOGGER;
import static space.typro.typicalmod.server.economy.EconomyLocalActions.getBalance;

public class BalanceCommand {
    public BalanceCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal(BaseConstants.ROOT_CMD)
                        .then(Commands.literal("pay")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("count", FloatArgumentType.floatArg(0))
                                                .executes(
                                                        ctx -> pay(
                                                                EntityArgument.getPlayer(ctx, "player"),
                                                                FloatArgumentType.getFloat(ctx, "count"),
                                                                ctx.getSource())
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("money")
                                .executes(ctx -> money(ctx.getSource()))
                        )
        );
    }
    private int pay(ServerPlayerEntity player, float sum, CommandSource source) {
        ServerPlayerEntity from;

        try {
            from = source.getPlayerOrException();
        }catch (CommandSyntaxException e){
            LOGGER.error("Failed to get player when using command /typical pay: " + e.getMessage());
            return -1;
        }
        if (from.getUUID().toString().equals(player.getUUID().toString())) {
            MessageUtils.sendMessage(from.getUUID(), MessageUtils.generateCommandMsg(MessageConstants.COMMAND_ERROR,
                    new TranslationTextComponent( "commands.pay.tryToItself").withStyle(TextFormatting.RED))
            );
            return 0;
        }
        float fromBal = getBalance(from.getUUID());
        if (sum < 1){
            MessageUtils.sendMessage(from, MessageUtils.generateCommandMsg(MessageConstants.COMMAND_ERROR,
                    new TranslationTextComponent("commands.pay.numberFormatException").withStyle(TextFormatting.RED))
            );
            return 0;
        }
        if (fromBal < 0) {
            MessageUtils.sendMessage(from, MessageUtils.generateCommandMsg(MessageConstants.COMMAND_ERROR,
                    new TranslationTextComponent("commands.pay.lowBal").withStyle(TextFormatting.RED))
            );
            return 0;
        }
        if ((fromBal - sum) <= 0) {
            MessageUtils.sendMessage(from, MessageUtils.generateCommandMsg(MessageConstants.COMMAND_ERROR,
                    new TranslationTextComponent("commands.pay.noMoney").withStyle(TextFormatting.RED))
            );
            return 0;
        }

        EconomyLocalActions.MoneyTransfer(from.getUUID(), player.getUUID(), sum);
        return 0;
    }
    /*
    new StringTextComponent("(test) " +
                    new TranslationTextComponent("commands.money",
                    new StringTextComponent(String.valueOf(getBalance(source.getPlayerOrException().getUUID()))).withStyle(TextFormatting.GREEN)))
     */
    private int money(CommandSource source){
        try {
            MessageUtils.sendCmdFeedback(source, MessageUtils.generateCommandMsg(MessageConstants.COMMAND_MONEY,
                    new TranslationTextComponent("commands.money",
                            new StringTextComponent(
                                    String.valueOf(
                                            getBalance(
                                                    source.getPlayerOrException().getUUID()
                                            )
                                    )
                            ).withStyle(TextFormatting.GREEN))
                    )
            );
        }catch (CommandSyntaxException e){
            LOGGER.error("Failed to execute /ty money command " + e);
            return 0;
        }
        return 0;
    }



}
