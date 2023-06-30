package dev.thomasqtruong.veryscuffedcobblemonbreeding.util;


import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.chat.CommonComponents;

public class ItemBuilder {
    ItemStack stack = null;

    public ItemBuilder(Item item) {
        this.stack = new ItemStack(item);
    }
    public ItemBuilder(ItemStack item) {
        this.stack = item;
    }

    public ItemBuilder addLore(MutableComponent[] lore) {
        CompoundTag nbt = this.stack.getOrCreateTag();
        CompoundTag displayNbt = this.stack.getOrCreateTagElement("display");
        ListTag nbtLore = new ListTag();

        for (MutableComponent chrisVar : lore) {
            Component test = Component.literal(StringTag.valueOf("").toString());
            Component test2 = chrisVar.withStyle(Style.EMPTY.withItalic(false));
           
            Component line = CommonComponents.joinLines(test2, test);
            nbtLore.add(StringTag.valueOf(MutableComponent.Serializer.toJson(line)));
        }

        displayNbt.put("Lore", nbtLore);
        nbt.put("display", displayNbt);
        this.stack.setTag(nbt);
        return this;
    }

    public ItemBuilder hideAdditional() {
        //this.stack.addHideFlag(ItemStack.TooltipPart.ADDITIONAL);
        return this;
    }
    public ItemBuilder setCustomName(MutableComponent customName) {
        Component test = Component.literal(StringTag.valueOf("").toString());
        Component test2 = customName.withStyle(Style.EMPTY.withItalic(false));
        
        Component pokemonName = CommonComponents.joinLines(test2, test);
        this.stack.setHoverName(pokemonName);
        return this;
    }

    public ItemStack build() {
        return this.stack;
    }
}
