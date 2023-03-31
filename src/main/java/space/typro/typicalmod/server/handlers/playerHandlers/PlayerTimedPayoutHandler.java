package space.typro.typicalmod.server.handlers.playerHandlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import space.typro.typicalmod.TypicalMod;
import space.typro.typicalmod.server.economy.ConstantsEconomy;
import space.typro.typicalmod.server.economy.EconomyLocalActions;
import space.typro.typicalmod.server.utils.MessageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE;
import static space.typro.typicalmod.TypicalMod.LOGGER;
@Mod.EventBusSubscriber(modid = TypicalMod.MOD_ID, value = Dist.DEDICATED_SERVER, bus = FORGE)
public class PlayerTimedPayoutHandler {
    private static long ticks = 0;
    private static long seconds = 0;
    public static final HashMap<PlayerEntity, Long> playersJoinTime = new HashMap<>();

    //Раз во сколько секунд будет проверка на то, пора ли платить
    private static final long CheckPayoutInterval = 10;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ticks++;
            if (ticks % 20 == 0) {
                seconds++;
            }
            // check the player timers every ~10 seconds
            // this is only to reduce load on the server
            if (seconds % CheckPayoutInterval == 0 && ticks % 20 == 0) {
                // check time interval for each player individually
                List<Map.Entry<PlayerEntity, Long>> playersToPay = playersJoinTime.entrySet()
                        .stream()
                        .filter(playerJoinEntry -> seconds - playerJoinEntry.getValue() >= ConstantsEconomy.PayoutSecondsInterval)
                        .collect(Collectors.toList());

                playersToPay.forEach(playerEntry -> {
                    EconomyLocalActions.addBalance(playerEntry.getKey().getUUID(), ConstantsEconomy.PayoutValue);
                    playersJoinTime.put(playerEntry.getKey(), seconds);
                    MessageUtils.sendMessage(playerEntry.getKey().getUUID(),
                            MessageUtils.generateCommandMsg(
                                    "PAYDAY",
                                    new TranslationTextComponent("events.payTime",  new StringTextComponent(String.valueOf(ConstantsEconomy.PayoutValue))
                                            .withStyle(TextFormatting.GREEN)
                                    ).withStyle(TextFormatting.BLUE)
                            )
                    );
                    LOGGER.info("Payment to " + playerEntry.getKey().getUUID());
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        LOGGER.info("Player " + event.getPlayer().getUUID() + " joined. Add to payment");
        playersJoinTime.put(event.getPlayer(), seconds);
    }

    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        LOGGER.info("Player " + event.getPlayer().getUUID() + " leaved. Remove from payment");
        playersJoinTime.remove(event.getPlayer());
    }

}
