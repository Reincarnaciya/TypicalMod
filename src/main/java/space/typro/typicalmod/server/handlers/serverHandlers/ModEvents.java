package space.typro.typicalmod.server.handlers.serverHandlers;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;
import space.typro.typicalmod.server.commands.ConsoleCommands;
import space.typro.typicalmod.server.commands.KitCommand;
import space.typro.typicalmod.TypicalMod;
import space.typro.typicalmod.server.commands.BalanceCommand;
import space.typro.typicalmod.server.commands.HelpCommand;

@Mod.EventBusSubscriber(modid = TypicalMod.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event){
        new HelpCommand(event.getDispatcher());
        new BalanceCommand(event.getDispatcher());
        new KitCommand(event.getDispatcher());
        new ConsoleCommands(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }
}
