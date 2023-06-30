package dev.thomasqtruong.veryscuffedcobblemonbreeding.util;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty;
import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import net.minecraft.world.item.ItemStack;
//import net.minecraft.text.*;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
//import net.minecraft.util.ChatChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
//net.minecraft.text.Texts
//net.minecraft.text.Text

import java.util.List;

public class PokemonUtility {


    public static Component getHoverText(MutableComponent toSend, Pokemon pokemon) {
        MutableComponent statsHoverText = Component.literal("").setStyle(Style.EMPTY);
        statsHoverText.append(pokemon.getDisplayName().setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN).withUnderlined(true)));
        if (pokemon.getShiny()) {
            statsHoverText.append(Component.literal(" ★").withStyle(ChatFormatting.GOLD));
        }
        statsHoverText.append(Component.literal("\n"));
        statsHoverText.append(Component.literal("Gender: ").withStyle(ChatFormatting.GREEN).append(Component.literal(String.valueOf(pokemon.getGender())).withStyle(ChatFormatting.WHITE)));
        statsHoverText.append(Component.literal("\n"));
        statsHoverText.append(Component.literal("Level: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(pokemon.getLevel())).withStyle(ChatFormatting.WHITE)));
        statsHoverText.append(Component.literal("\n"));
        statsHoverText.append(Component.literal("Nature: ").withStyle(ChatFormatting.YELLOW).append(LocalizationUtilsKt.lang(pokemon.getNature().getDisplayName().replace("cobblemon.", "")).withStyle(ChatFormatting.WHITE)));
        statsHoverText.append(Component.literal("\n"));
        statsHoverText.append(Component.literal("Ability: ").withStyle(ChatFormatting.GOLD).append(LocalizationUtilsKt.lang(pokemon.getAbility().getDisplayName().replace("cobblemon.", "")).withStyle(ChatFormatting.WHITE)));
        statsHoverText.append(Component.literal("\n"));
        statsHoverText.append(Component.literal("Form: ").withStyle(ChatFormatting.GREEN).append(Component.literal(pokemon.getForm().getName()).withStyle(ChatFormatting.WHITE)));

        Component test = Component.literal("[Stats]").withStyle(ChatFormatting.RED);//.toFlatList(Style.EMPTY.withColor(ChatFormatting.RED).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, statsHoverText)));
        Component test2 = Component.literal(StringTag.valueOf("").toString());
        
       
        Component statsText = CommonComponents.joinLines(test, test2);

        toSend.append(statsText);

        MutableComponent evsText = Component.literal(" [EVs]").withStyle(ChatFormatting.GOLD);
        MutableComponent evsHoverText = Component.literal("");
        List<Component> evsHoverTextList = Component.literal("EVs").withStyle(ChatFormatting.GOLD).toFlatList(Style.EMPTY.withUnderlined(Boolean.TRUE));
        evsHoverTextList.add(Component.literal("\n"));
        evsHoverTextList.add(Component.literal("HP: ").withStyle(ChatFormatting.RED).append(Component.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.HP))).withStyle(ChatFormatting.WHITE)));
        evsHoverTextList.add(Component.literal("\n"));
        evsHoverTextList.add(Component.literal("Attack: ").withStyle(ChatFormatting.BLUE).append(Component.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.ATTACK))).withStyle(ChatFormatting.WHITE)));
        evsHoverTextList.add(Component.literal("\n"));
        evsHoverTextList.add(Component.literal("Defense: ").withStyle(ChatFormatting.GRAY).append(Component.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.DEFENCE))).withStyle(ChatFormatting.WHITE)));
        evsHoverTextList.add(Component.literal("\n"));
        evsHoverTextList.add(Component.literal("Sp. Attack: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPECIAL_ATTACK))).withStyle(ChatFormatting.WHITE)));
        evsHoverTextList.add(Component.literal("\n"));
        evsHoverTextList.add(Component.literal("Sp. Defense: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPECIAL_DEFENCE))).withStyle(ChatFormatting.WHITE)));
        evsHoverTextList.add(Component.literal("\n"));
        evsHoverTextList.add(Component.literal("Speed: ").withStyle(ChatFormatting.GREEN).append(Component.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPEED))).withStyle(ChatFormatting.WHITE)));

        evsHoverTextList.forEach(evsHoverText::append);
        List<Component> evsList = evsText.toFlatList(evsText.getStyle().withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        evsHoverText
                ))
        );
        evsList.forEach(toSend::append);

        MutableComponent ivsText = Component.literal(" [IVs]").withStyle(ChatFormatting.LIGHT_PURPLE);
        MutableComponent ivsHoverText = Component.literal("");
        List<Component> ivsHoverTextList = Component.literal("IVs").withStyle(ChatFormatting.GOLD).toFlatList(Style.EMPTY.withUnderlined(Boolean.TRUE));
        ivsHoverTextList.add(Component.literal("\n"));
        ivsHoverTextList.add(Component.literal("HP: ").withStyle(ChatFormatting.RED).append(Component.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.HP))).withStyle(ChatFormatting.WHITE)));
        ivsHoverTextList.add(Component.literal("\n"));
        ivsHoverTextList.add(Component.literal("Attack: ").withStyle(ChatFormatting.BLUE).append(Component.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.ATTACK))).withStyle(ChatFormatting.WHITE)));
        ivsHoverTextList.add(Component.literal("\n"));
        ivsHoverTextList.add(Component.literal("Defense: ").withStyle(ChatFormatting.GRAY).append(Component.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.DEFENCE))).withStyle(ChatFormatting.WHITE)));
        ivsHoverTextList.add(Component.literal("\n"));
        ivsHoverTextList.add(Component.literal("Sp. Attack: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPECIAL_ATTACK))).withStyle(ChatFormatting.WHITE)));
        ivsHoverTextList.add(Component.literal("\n"));
        ivsHoverTextList.add(Component.literal("Sp. Defense: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPECIAL_DEFENCE))).withStyle(ChatFormatting.WHITE)));
        ivsHoverTextList.add(Component.literal("\n"));
        ivsHoverTextList.add(Component.literal("Speed: ").withStyle(ChatFormatting.GREEN).append(Component.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPEED))).withStyle(ChatFormatting.WHITE)));

        ivsHoverTextList.forEach(ivsHoverText::append);
        List<Component> ivsList = ivsText.toFlatList(ivsText.getStyle().withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        ivsHoverText
                ))
        );
        ivsList.forEach(toSend::append);

        MutableComponent movesText = Component.literal(" [Moves]").withStyle(ChatFormatting.BLUE);
        MutableComponent movesHoverText = Component.literal("");
        List<Component> movesHoverTextList = Component.literal("Moves").withStyle(ChatFormatting.BLUE).toFlatList(Style.EMPTY.withUnderlined(Boolean.TRUE));
        movesHoverTextList.add(Component.literal("\n"));
        String moveOne = pokemon.getMoveSet().getMoves().size() >= 1 ? pokemon.getMoveSet().get(0).getDisplayName().getString() : "Empty";
        String moveTwo = pokemon.getMoveSet().getMoves().size() >= 2 ? pokemon.getMoveSet().get(1).getDisplayName().getString() : "Empty";
        String moveThree = pokemon.getMoveSet().getMoves().size() >= 3 ? pokemon.getMoveSet().get(2).getDisplayName().getString() : "Empty";
        String moveFour = pokemon.getMoveSet().getMoves().size() >= 4 ? pokemon.getMoveSet().get(3).getDisplayName().getString() : "Empty";
        movesHoverTextList.add(Component.literal("Move 1: ").withStyle(ChatFormatting.RED).append(Component.literal(moveOne).withStyle(ChatFormatting.WHITE)));
        movesHoverTextList.add(Component.literal("\n"));
        movesHoverTextList.add(Component.literal("Move 2: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(moveTwo).withStyle(ChatFormatting.WHITE)));
        movesHoverTextList.add(Component.literal("\n"));
        movesHoverTextList.add(Component.literal("Move 3: ").withStyle(ChatFormatting.AQUA).append(Component.literal(moveThree).withStyle(ChatFormatting.WHITE)));
        movesHoverTextList.add(Component.literal("\n"));
        movesHoverTextList.add(Component.literal("Move 4: ").withStyle(ChatFormatting.GREEN).append(Component.literal(moveFour).withStyle(ChatFormatting.WHITE)));

        movesHoverTextList.forEach(movesHoverText::append);
        List<Component> movesList = movesText.toFlatList(movesText.getStyle().withHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        movesHoverText
                ))
        );
        movesList.forEach(toSend::append);
        return toSend;
    }

    public static ItemStack pokemonToItem(Pokemon pokemon) {
        String moveOne = pokemon.getMoveSet().getMoves().size() >= 1 ? pokemon.getMoveSet().get(0).getDisplayName().getString() : "Empty";
        String moveTwo = pokemon.getMoveSet().getMoves().size() >= 2 ? pokemon.getMoveSet().get(1).getDisplayName().getString() : "Empty";
        String moveThree = pokemon.getMoveSet().getMoves().size() >= 3 ? pokemon.getMoveSet().get(2).getDisplayName().getString() : "Empty";
        String moveFour = pokemon.getMoveSet().getMoves().size() >= 4 ? pokemon.getMoveSet().get(3).getDisplayName().getString() : "Empty";


        ItemStack itemstack = new ItemBuilder(PokemonItem.from(pokemon, 1))
                
                .hideAdditional()
                .addLore(new MutableComponent[]{
                        //pokemon.getCaughtBall().item().getName().copy().setStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.DARK_GRAY)),
                        Component.literal("Gender: ").withStyle(ChatFormatting.GREEN).append(Component.literal(String.valueOf(pokemon.getGender())).withStyle(ChatFormatting.WHITE)),
                        Component.literal("Level: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(pokemon.getLevel())).withStyle(ChatFormatting.WHITE)),
                        Component.literal("Nature: ").withStyle(ChatFormatting.YELLOW).append(LocalizationUtilsKt.lang(pokemon.getNature().getDisplayName().replace("cobblemon.", "")).withStyle(ChatFormatting.WHITE)),
                        Component.literal("Ability: ").withStyle(ChatFormatting.GOLD).append(LocalizationUtilsKt.lang(pokemon.getAbility().getDisplayName().replace("cobblemon.", "")).withStyle(ChatFormatting.WHITE)),
                        Component.literal("IVs: ").withStyle(ChatFormatting.LIGHT_PURPLE),
                        Component.literal("  HP: ").withStyle(ChatFormatting.RED).append(Component.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.HP))).withStyle(ChatFormatting.WHITE))
                                .append(Component.literal("  Atk: ").withStyle(ChatFormatting.BLUE).append(Component.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.ATTACK))).withStyle(ChatFormatting.WHITE)))
                                .append(Component.literal("  Def: ").withStyle(ChatFormatting.GRAY).append(Component.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.DEFENCE))).withStyle(ChatFormatting.WHITE))),
                        Component.literal("  SpAtk: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPECIAL_ATTACK))).withStyle(ChatFormatting.WHITE))
                                .append(Component.literal("  SpDef: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPECIAL_DEFENCE))).withStyle(ChatFormatting.WHITE)))
                                .append(Component.literal("  Spd: ").withStyle(ChatFormatting.GREEN).append(Component.literal(String.valueOf(pokemon.getIvs().getOrDefault(Stats.SPEED))).withStyle(ChatFormatting.WHITE))),

                        Component.literal("EVs: ").withStyle(ChatFormatting.DARK_AQUA),
                        Component.literal("  HP: ").withStyle(ChatFormatting.RED).append(Component.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.HP))).withStyle(ChatFormatting.WHITE))
                                .append(Component.literal("  Atk: ").withStyle(ChatFormatting.BLUE).append(Component.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.ATTACK))).withStyle(ChatFormatting.WHITE)))
                                .append(Component.literal("  Def: ").withStyle(ChatFormatting.GRAY).append(Component.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.DEFENCE))).withStyle(ChatFormatting.WHITE))),
                        Component.literal("  SpAtk: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPECIAL_ATTACK))).withStyle(ChatFormatting.WHITE))
                                .append(Component.literal("  SpDef: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPECIAL_DEFENCE))).withStyle(ChatFormatting.WHITE)))
                                .append(Component.literal("  Spd: ").withStyle(ChatFormatting.GREEN).append(Component.literal(String.valueOf(pokemon.getEvs().getOrDefault(Stats.SPEED))).withStyle(ChatFormatting.WHITE))),
                        Component.literal("Moves: ").withStyle(ChatFormatting.DARK_GREEN),
                        Component.literal(" ").append(Component.literal(moveOne).withStyle(ChatFormatting.WHITE)),
                        Component.literal(" ").append(Component.literal(moveTwo).withStyle(ChatFormatting.WHITE)),
                        Component.literal(" ").append(Component.literal(moveThree).withStyle(ChatFormatting.WHITE)),
                        Component.literal(" ").append(Component.literal(moveFour).withStyle(ChatFormatting.WHITE)),
                        Component.literal("Form: ").withStyle(ChatFormatting.GOLD).append(pokemon.getForm().getName())
                })
                .setCustomName(
                        pokemon.getShiny() ?
                                pokemon.getDisplayName().withStyle(ChatFormatting.GRAY).append(Component.literal(" ★").withStyle(ChatFormatting.GOLD)) :
                                pokemon.getDisplayName().withStyle(ChatFormatting.GRAY)
                )
                .build();
        return itemstack;
    }
}
