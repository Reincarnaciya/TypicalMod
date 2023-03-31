package space.typro.typicalmod.server.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.*;

import java.util.UUID;

import static space.typro.typicalmod.TypicalMod.LOGGER;

public class MessageUtils {
    public static void sendCmdFeedback(CommandSource src, IFormattableTextComponent text) {
        try {
            if (src.getEntity() == null) {
                src.sendSuccess(text, true);
            } else {
                sendMessage(src.getPlayerOrException(), text);
            }
        } catch (CommandSyntaxException e) {
            LOGGER.error(e);
        }
    }
    public static void sendMessage(ServerPlayerEntity player, String translationKey) {
        player.sendMessage(new TranslationTextComponent(translationKey), player.getUUID());
    }
    public static void sendMessage(UUID playerUUID, String translationKey) {
        ServerPlayerEntity player = PlayerUtils.getServerPlayerFromUUID(playerUUID);
        if (player == null) return;
        player.sendMessage(new TranslationTextComponent(translationKey), player.getUUID());
    }
    public static void sendMessage(PlayerEntity player, String translationKey) {
        player.sendMessage(new TranslationTextComponent(translationKey), player.getUUID());
    }
    public static void sendMessage(ServerPlayerEntity player, IFormattableTextComponent msg) {
        player.sendMessage(msg, player.getUUID());
    }
    public static void sendMessage(UUID playerUUID, IFormattableTextComponent msg) {
        ServerPlayerEntity player = PlayerUtils.getServerPlayerFromUUID(playerUUID);
        if (player == null) return;
        player.sendMessage(msg, player.getUUID());
    }

    /**
     *
     * @param prefix текст в []
     * @param components остальные компоненты
     * @return форматтированный текстовый компонент
     */
    public static IFormattableTextComponent generateCommandMsg(String prefix, ITextComponent... components){
        IFormattableTextComponent finalComponent = new StringTextComponent("["+prefix+"] ").withStyle(TextFormatting.GRAY);
        for (ITextComponent component : components){
            finalComponent.append(component);
        }
        return finalComponent;
    }
}
