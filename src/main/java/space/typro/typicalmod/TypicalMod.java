package space.typro.typicalmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import space.typro.typicalmod.server.config.DatabaseConfig;
import space.typro.typicalmod.server.database.ConstantsDatabase;
import space.typro.typicalmod.server.database.Database;
import space.typro.typicalmod.server.kit.Kit;
import space.typro.typicalmod.server.kit.KitItems;

import javax.xml.crypto.Data;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static space.typro.typicalmod.server.kit.Kit.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TypicalMod.MOD_ID)
public class TypicalMod {
    public static final ExecutorService executorService = Executors.newCachedThreadPool();
    public static final String MOD_ID = "typicalmod";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public TypicalMod() {
        ModLoadingContext.get().registerConfig(
                ModConfig.Type.SERVER,
                DatabaseConfig.CONFIG_SPEC,
                DatabaseConfig.CONFIG_NAME
        );

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigLoading);

        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void onConfigLoading(ModConfig.Loading event) {
        if (event.getConfig().getModId().equals(MOD_ID)) {
            switch (event.getConfig().getFileName()) {
                case DatabaseConfig.CONFIG_NAME:

                    ConstantsDatabase.TYSCI_BALANCE_DATABASE = new Database(
                            DatabaseConfig.SERVER_BALANCE_DATABASE_HOST.get(),
                            DatabaseConfig.SERVER_BALANCE_DATABASE_USERNAME.get(),
                            DatabaseConfig.SERVER_BALANCE_DATABASE_PASSWORD.get()
                    );
                    ConstantsDatabase.TYSCI_BALANCE_TABLE = DatabaseConfig.SERVER_BALANCE_DATABASE_TABLE.get();

                    ConstantsDatabase.TY_KITS_DB = new Database(
                            DatabaseConfig.SERVER_KIT_DATABASE_HOST.get(),
                            DatabaseConfig.SERVER_KIT_DATABASE_USERNAME.get(),
                            DatabaseConfig.SERVER_KIT_DATABASE_PASSWORD.get()
                    );
                    ConstantsDatabase.TYSCI_KITS_TABLE = DatabaseConfig.SERVER_KIT_DATABASE_TABLE.get();

                    ConstantsDatabase.TY_PRIVILEGE_DB = new Database(
                            DatabaseConfig.SERVER_PRIVILEGES_DATABASE_HOST.get(),
                            DatabaseConfig.SERVER_PRIVILEGES_DATABASE_USERNAME.get(),
                            DatabaseConfig.SERVER_PRIVILEGES_DATABASE_PASSWORD.get()
                    );
                    ConstantsDatabase.TY_PRIVILEGE_TABLE = DatabaseConfig.SERVER_PRIVILEGES_DATABASE_TABLE.get();
            }
        }
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("TypicalMod start ^-^");
        START.setItemsToGive(KitItems.startItemStack());
        VIP.setItemsToGive(KitItems.vipItemStack());
        PREMIUM.setItemsToGive(KitItems.premiumItemStack());
        DELUX.setItemsToGive(KitItems.deluxItemStack());
        LUX.setItemsToGive(KitItems.luxItemStack());
    }



}
