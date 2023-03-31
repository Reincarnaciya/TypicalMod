package space.typro.typicalmod.server.kit;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import static space.typro.typicalmod.server.kit.KitManager.serverKits;


public class Kit {


    public static final Kit START = new Kit(172800000000L, 0, "start", 0);
    public static final Kit VIP = new Kit(800000000L, 1, "LaboratoryAssistant", 1);
    public static final Kit PREMIUM = new Kit(50000000000000000L, 2, "Bachelor", 2);
    public static final Kit DELUX = new Kit(99999999L, 3, "Master", 3);
    public static final Kit LUX = new Kit(9218392L, 4, "Doctor", 4);
    private final long delay;
    private ItemStack[] itemsToGive;
    private final float cost;
    private final int requiredLvl;
    public final String name;
    public final int id;





    public void setItemsToGive(ItemStack[] itemsToGive) {
        this.itemsToGive = itemsToGive;
    }

    public Kit(long delay, int lvl, String showName, int id){
        this.delay = delay;
        this.id = id;
        requiredLvl = lvl;
        name = showName;
        cost = 0f;
    }
    public Kit(long delay, float cost, int lvl, String showName, int id){
        this.delay = delay;
        this.id = id;
        this.cost = cost;
        name = showName;
        requiredLvl = lvl;
    }


    public int getRequiredLvl() {
        return requiredLvl;
    }

    public ItemStack[] getItems(){
        return this.itemsToGive;
    }
    public long getDelay(){
        return this.delay;
    }

    public float getCost() {
        return this.cost;
    }


    private ItemStack[] getStartItemStack(){
        return itemsToGive;
    }
}
