package space.typro.typicalmod.server.config;

import net.minecraftforge.common.ForgeConfigSpec;
import space.typro.typicalmod.TypicalMod;

import java.util.List;

public class DatabaseConfig {
    public static final ForgeConfigSpec CONFIG_SPEC;

    public static final String CONFIG_NAME = TypicalMod.MOD_ID + "-TypicalMod.toml";
    public static final ForgeConfigSpec.ConfigValue<String> SERVER_BALANCE_DATABASE_HOST;
    public static final ForgeConfigSpec.ConfigValue<String> SERVER_BALANCE_DATABASE_USERNAME;
    public static final ForgeConfigSpec.ConfigValue<String> SERVER_BALANCE_DATABASE_PASSWORD;
    public static final ForgeConfigSpec.ConfigValue<String> SERVER_BALANCE_DATABASE_TABLE;


    public static final ForgeConfigSpec.ConfigValue<String> SERVER_KIT_DATABASE_HOST;
    public static final ForgeConfigSpec.ConfigValue<String> SERVER_KIT_DATABASE_USERNAME;
    public static final ForgeConfigSpec.ConfigValue<String> SERVER_KIT_DATABASE_PASSWORD;
    public static final ForgeConfigSpec.ConfigValue<String> SERVER_KIT_DATABASE_TABLE;
    
    
    
    public static final ForgeConfigSpec.ConfigValue<String> SERVER_PRIVILEGES_DATABASE_HOST;
    public static final ForgeConfigSpec.ConfigValue<String> SERVER_PRIVILEGES_DATABASE_USERNAME;
    public static final ForgeConfigSpec.ConfigValue<String> SERVER_PRIVILEGES_DATABASE_PASSWORD;
    public static final ForgeConfigSpec.ConfigValue<String> SERVER_PRIVILEGES_DATABASE_TABLE;


    

    static {
        final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.push("TypicalMod Database Configuration").build();
        BUILDER.pop();
        BUILDER.push("BalanceDatabase");

        //БД с балансами игроков
        SERVER_BALANCE_DATABASE_HOST = BUILDER.comment("Хост базы данных с балансами игроков (Пример: jdbc:mysql://web.some-host.com:3306/databaseName)")
                .define("server_balance_database_host", "jdbc:mysql://web.host-example.com:3306/exampleDatabaseName");
        SERVER_BALANCE_DATABASE_USERNAME = BUILDER.comment("Имя пользователя базы данных")
                .define("server_balance_database_username", "exampleName");
        SERVER_BALANCE_DATABASE_PASSWORD = BUILDER.comment("Пароль от пользователя базы данных")
                .define("server_balance_database_password", "examplePassword");
        SERVER_BALANCE_DATABASE_TABLE = BUILDER.comment("Таблица с которой работаем в БД")
                .define("server_balance_database_table", "exampleTable");
        BUILDER.pop();
        BUILDER.push("KitDatabase");
        // БД с китами игроков
        SERVER_KIT_DATABASE_HOST = BUILDER.comment("Хост базы данных с китами игроков (Пример: jdbc:mysql://web.some-host.com:3306/databaseName)")
                .define("server_kit_database_host", "jdbc:mysql://web.host-example.com:3306/exampleDatabaseName");
        SERVER_KIT_DATABASE_USERNAME = BUILDER.comment("Имя пользователя базы данных")
                .define("server_kit_database_username", "exampleName");
        SERVER_KIT_DATABASE_PASSWORD = BUILDER.comment("Пароль от пользователя базы данных")
                .define("server_kit_database_password", "examplePassword");
        SERVER_KIT_DATABASE_TABLE = BUILDER.comment("Таблица с которой работаем в БД")
                .define("server_kit_database_table", "exampleTable");
        BUILDER.pop();
        BUILDER.push("PrivilegesDatabase");
        // БД с привилегиями игроков
        SERVER_PRIVILEGES_DATABASE_HOST = BUILDER.comment("Хост базы данных c привилегиями игроков (Пример: jdbc:mysql://web.some-host.com:3306/databaseName)")
                .define("server_privileges_database_host", "jdbc:mysql://web.host-example.com:3306/exampleDatabaseName");
        SERVER_PRIVILEGES_DATABASE_USERNAME = BUILDER.comment("Имя пользователя базы данных")
                .define("server_privileges_database_username", "exampleName");
        SERVER_PRIVILEGES_DATABASE_PASSWORD = BUILDER.comment("Пароль от пользователя базы данных")
                .define("server_privileges_database_password", "examplePassword");
        SERVER_PRIVILEGES_DATABASE_TABLE = BUILDER.comment("Таблица с которой работаем в БД")
                .define("server_privileges_database_table", "exampleTable");
        BUILDER.pop();
        CONFIG_SPEC = BUILDER.build();
    }
}
