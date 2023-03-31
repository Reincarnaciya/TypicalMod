package space.typro.typicalmod.server.kit;

import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import javax.swing.text.html.parser.TagElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class KitItems {

/*
    Гайд книга иммерсива = immersiveengineering:manual
    Счетчик гейгера = nuclearscience:geigercounter
 */

    public static ItemStack[] startItemStack(){
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(Items.STONE_SWORD, 1));
        list.add(new ItemStack(Items.STONE_SHOVEL, 1));
        list.add(new ItemStack(Items.STONE_PICKAXE, 1));
        list.add(new ItemStack(Items.STONE_HOE, 1));
        list.add(new ItemStack(Items.CARROT, 16));
        list.add(new ItemStack(Items.COOKED_BEEF, 16));
        list.add(new ItemStack(Items.BREAD, 16));
        list.add(new ItemStack(Items.LEATHER_BOOTS, 1));
        list.add(new ItemStack(Items.LEATHER_LEGGINGS, 1));
        list.add(new ItemStack(Items.LEATHER_CHESTPLATE, 1));
        list.add(new ItemStack(Items.LEATHER_HELMET, 1));
        ResourceLocation[] itemRls = new ResourceLocation[]{
                new ResourceLocation("immersiveengineering:manual"),
                new ResourceLocation("nuclearscience:geigercounter")
        };
        List<ResourceLocation> available = Arrays.stream(itemRls).filter(ForgeRegistries.ITEMS::containsKey).collect(Collectors.toList());
        available.forEach(resourceLocation -> list.add(new ItemStack(RegistryObject.of(resourceLocation, ForgeRegistries.ITEMS).get(), 1)));
        return list.toArray(new ItemStack[]{});
    }

    public static ItemStack[] vipItemStack(){
        return new ItemStack[]{
                new ItemStack(Items.DIRT, 64)
        };
    }

    public static ItemStack[] premiumItemStack(){
        return new ItemStack[]{
                new ItemStack(Items.DIRT, 64)
        };
    }
    public static ItemStack[] deluxItemStack(){
        return new ItemStack[]{
                new ItemStack(Items.DIRT, 64)
        };
    }
    public static ItemStack[] luxItemStack(){
        return new ItemStack[]{
                new ItemStack(Items.DIRT, 64)
        };
    }




}
