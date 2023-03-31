package space.typro.typicalmod.server.handlers;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import space.typro.typicalmod.TypicalMod;
import space.typro.typicalmod.server.commands.Config.AdminConstans;
import space.typro.typicalmod.server.utils.MessageConstants;
import space.typro.typicalmod.server.utils.MessageUtils;

import java.util.List;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE;
import static space.typro.typicalmod.TypicalMod.LOGGER;

@Mod.EventBusSubscriber(modid = TypicalMod.MOD_ID, value = Dist.DEDICATED_SERVER, bus = FORGE)
public class CommandHandler {




    @SubscribeEvent
    public static void onCommand(CommandEvent event){
        CommandContextBuilder<CommandSource> cmdContext = event.getParseResults().getContext();
        CommandSource src = cmdContext.getSource();
        List<ParsedCommandNode<CommandSource>> cmdNodes = cmdContext.getNodes();
        ServerPlayerEntity player;
        try {
            player = src.getPlayerOrException();
        } catch (CommandSyntaxException e) {
            LOGGER.error(e);
            return;
        }
        if (cmdNodes.size() > 2){
            String baseCmd = cmdNodes.get(0).getNode().getName();
            if (baseCmd.equals(AdminConstans.ROOT_CONSOLE) && !src.hasPermission(4)){
                MessageUtils.sendMessage(player,
                        MessageUtils.generateCommandMsg(
                                MessageConstants.COMMAND_ERROR,
                                new TranslationTextComponent("commands.error.noPermissions",
                                        event.getParseResults().getReader().getString())
                                        .withStyle(TextFormatting.RED)
                        )
                );
                event.setCanceled(true);
            }
        }
    }
}
