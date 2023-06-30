package dev.thomasqtruong.veryscuffedcobblemonbreeding.screen;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCBox;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.oracle.truffle.js.runtime.objects.Null;

import dev.thomasqtruong.veryscuffedcobblemonbreeding.util.ItemBuilder;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.commands.PokeBreed;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.util.PokemonUtility;
//import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.inventory.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.MenuProvider;
//import net.minecraft.inventory.SimpleInventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.MenuType;

import net.minecraft.world.item.Items;
import net.minecraft.ChatFormatting;
//import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
//import net.minecraft.screen.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.Item;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
//import net.minecraft.screen.NamedScreenHandlerFactory;
//not import net.minecraft.world.SimpleMenuProvider;
//not import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.SimpleMenuProvider;
//import net.minecraft.screen.ScreenHandler;
//import net.minecraft.screen.ScreenHandlerType; = MenuType
//import net.minecraft.screen.slot.Slot;
//import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.world.inventory.ClickType;
//import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.nbt.CompoundTag;
//import net.minecraft.text.Text;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.OutgoingChatMessage;
//import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Implements;
import net.minecraftforge.common.extensions.IForgeMenuType;

/**
 * Handles the PokeBreed GUI.
 */
public class PokeBreedHandlerFactory implements MenuProvider {

  private PokeBreed.BreedSession breedSession;
  private int boxNumber = 0;
  final private int[] pageSettings = {1, 5, 10,
          15, 20, 25,
          50, 100, 200};


  /**
   * Default constructor; copies over breedSession.
   *
   * @param breedSession - the breed session to copy.
   */
  public PokeBreedHandlerFactory(PokeBreed.BreedSession breedSession) {
    this.breedSession = breedSession;
  }


  /**
   * Constructor for next/previous page.
   *
   * @param breedSession - the breed session to copy over.
   * @param boxNumber - the current box number to display.
   */
  public PokeBreedHandlerFactory(PokeBreed.BreedSession breedSession, int boxNumber) {
    this.breedSession = breedSession;
    // Negative, figure out the actual page number.
    if (boxNumber < 0) {
      boxNumber *= -1;                                 // Turn to positive.
      boxNumber %= breedSession.maxPCSize;             // Mod by max.
      boxNumber = breedSession.maxPCSize - boxNumber;  // Max - modded = current.
    }
    // Positive, keep within the max boxes range.
    this.boxNumber = boxNumber % breedSession.maxPCSize;
  }

  
  /**
   * Get display name for the GUI.
   */
  @Override
  public Component getDisplayName() {
    return Component.literal(String.valueOf("Breed: PC Box " + (boxNumber + 1)));
  }


  /**
   * Gets the number of rows in the GUI.
   *
   * @return int - the number of rows.
   */
  public int rows() {
    return 6;
  }


  /**
   * Gets the number of slots in the GUI.
   *
   * @return int - the number of slots.
   */
  public int size() {
    return rows() * 9;
  }


  /**
   * Updates the user's GUI inventory.
   *
   * @param inventory - the PokeBreed GUI.
   */
  public void updateInventory(SimpleContainer inventory) {
    ItemStack emptyPokemon = new ItemStack(Items.LIGHT_BLUE_STAINED_GLASS_PANE);

    // For index 15-17, set as blank.
    for (int i = 15; i <= 17; ++i) {
      // Set as gray glass.
      inventory.setItem(i, new ItemStack(Items.GRAY_STAINED_GLASS_PANE)
              .setHoverName(Component.literal(String.valueOf(" "))));
    }

    // Breeding choices.
    inventory.setItem(6, emptyPokemon.setHoverName(Component.literal(String.valueOf("To Breed #1"))));
    if (breedSession.breederPokemon1 != null) {
      inventory.setItem(6, PokemonUtility.pokemonToItem(breedSession.breederPokemon1));
    }
    inventory.setItem(7, new ItemStack(Items.PINK_STAINED_GLASS_PANE).setHoverName(Component.literal(" ")));
    inventory.setItem(8, emptyPokemon.setHoverName(Component.literal(String.valueOf("To Breed #2"))));
    if (breedSession.breederPokemon2 != null) {
      inventory.setItem(8, PokemonUtility.pokemonToItem(breedSession.breederPokemon2));
    }

    // Buttons
    MutableComponent NextBox = Component.literal(String.valueOf("Next Box"));
     MutableComponent PreviousBox = Component.literal(String.valueOf("Previous Box"));
    inventory.setItem(size() - 1, new ItemBuilder(Items.ARROW).hideAdditional().setCustomName(
           NextBox ).build());
    inventory.setItem(size() - 2, new ItemStack(Items.GRAY_DYE).setHoverName(
            Component.literal(String.valueOf("Click to Breed"))));
    inventory.setItem(size() - 3, new ItemBuilder(Items.ARROW).hideAdditional().setCustomName(
            PreviousBox).build());

    // Settings
    int pageIndex = 0;
    for (int i = 24; i <= 42; i += 9) {
      for (int j = 0; j < 3; ++j) {
        // Is current setting, make green pane.
        if (pageSettings[pageIndex] == breedSession.pageChangeSetting) {
          inventory.setItem(i + j, new ItemStack(Items.LIME_STAINED_GLASS_PANE)
                  .setHoverName(Component.literal(String.valueOf("Change box by " + String.valueOf(pageSettings[pageIndex])))));
        } else {
          inventory.setItem(i + j, new ItemStack(Items.WHITE_STAINED_GLASS_PANE)
                  .setHoverName(Component.literal(String.valueOf("Change box by " + String.valueOf(pageSettings[pageIndex])))));
        }
        ++pageIndex;
      }
    }
  }

  

  /**
   * Create PokeBreed GUI.
   *
   * @param syncId - the ID used to sync (?).
   * @param inv - the player's inventory.
   * @param player - the player themselves.
   * @return ScreenHandler - the created PokeBreed GUI.
   */
  @Override
  public ChestMenu createMenu(int syncId, Inventory inv, Player  player) {
    // Make GUI of size() size.
    SimpleContainer inventory = new SimpleContainer(size());
    ServerPlayer serverPlayer = (ServerPlayer) player;

    updateInventory(inventory);

    // Grab player's Party and PC data.
    PlayerPartyStore breederParty = null;
    PCStore breederStorage = null;
    try {
      breederParty = Cobblemon.INSTANCE.getStorage().getParty(serverPlayer.getUUID());
      breederStorage = Cobblemon.INSTANCE.getStorage().getPC(serverPlayer.getUUID());
    } catch (NoPokemonStoreException e) {
      return null;
    }

    // Set up Party in GUI.
    for (int i = 0; i < breederParty.size(); ++i) {
      // Get pokemon.
      Pokemon pokemon = breederParty.get(i);

      // Pokemon exists.
      if (pokemon != null) {
        // Turn Pokemon into item.
        ItemStack item = PokemonUtility.pokemonToItem(pokemon);
        CompoundTag slotNbt = item.getOrCreateTagElement("slot");
        slotNbt.putInt("slot", i);
        //item.setSubNbt("slot", slotNbt);
        item.addTagElement("slot", slotNbt);
        inventory.setItem(i, item);
      } else {
      // Doesn't exist.
        // Put a red stained glass instead.
        inventory.setItem(i, new ItemStack(Items.RED_STAINED_GLASS_PANE).setHoverName(
                Component.literal(String.valueOf("Empty").formatted(ChatFormatting.GRAY))));
      }
    }

    PCBox box = breederStorage.getBoxes().get(boxNumber);
    breedSession.maxPCSize = breederStorage.getBoxes().size();
    // Set up PC in GUI (for every Pokemon in box [box size = 6x5]).
    for (int i = 0; i < 30; i++) {
      Pokemon pokemon = box.get(i);
      double row = 1 + Math.floor((double) i / 6.0D);
      int index = i % 6;

      if (pokemon != null) {
        ItemStack item = PokemonUtility.pokemonToItem(pokemon);
        CompoundTag slotNbt = item.getOrCreateTagElement("slot");
        slotNbt.putInt("slot", i);
        item.addTagElement("slot", slotNbt);
        inventory.setItem((int) (row * 9) + index, item);
      } else {
        inventory.setItem((int) (row * 9) + index, new ItemStack(Items.RED_STAINED_GLASS_PANE)
                .setHoverName(Component.literal(String.valueOf("Empty")
                        .formatted(ChatFormatting.GRAY))));
      }
    }
    PlayerPartyStore finalBreederParty = breederParty;

   
    // Returns the GUI.
    return new ChestMenu(MenuType.GENERIC_9x6,
            syncId, inv, inventory, rows()) {

      /**
       *
       * @param slotIndex - the index of the clicked slot.
       * @param button - the button clicked (?).
       * @param actionType - the type of action (?).
       */
      //@Override
      public void slotClicked(int slotIndex, int button, ClickType actionType,
                              LocalPlayer player) {
        // If player cancels.
        if (breedSession.cancelled) {
          //player.sendChat("Breeding has been cancelled.", null);
          player.displayClientMessage(Component.translatable("Breeding has been cancelled."), false);
          player.closeContainer();
        }

        // Clicked accept.
        if (slotIndex == size() - 2) {
          breedSession.breederAccept = true;
          breedSession.doBreed();
          breedSession.breeder.closeContainer();
        }
        // Ignore when clicking a slot outside of the GUI.
        if (slotIndex > size()) {
          return;
        }

        // Clicked on a breeding Pokemon, remove from breed.
        if (slotIndex == 6) {
          breedSession.breederPokemon1 = null;
        } else if (slotIndex == 8) {
          breedSession.breederPokemon2 = null;
        } else if ((slotIndex >= 24 && slotIndex <= 26) ||
                (slotIndex >= 33 && slotIndex <= 35) ||
                (slotIndex >= 42 && slotIndex <= 44)) {
          // Clicked a page change setting.
          breedSession.pageChangeSetting = pageSettings[(slotIndex % 9 - 6) + (slotIndex / 9 - 2) * 3];
        } else if (slotIndex == size() - 1) {
          // Clicked next page.
          // Indicate that the old GUI closing is a page change, not cancel.
          breedSession.changePage = true;
          player.openMenu(new PokeBreedHandlerFactory(breedSession,
                  boxNumber + breedSession.pageChangeSetting));
          // Back to default value.
          breedSession.changePage = false;
        } else if (slotIndex == size() - 3) {
          // Clicked previous page.
          // Indicate that the old GUI closing is a page change, not cancel.
          breedSession.changePage = true;
          player.openMenu(new PokeBreedHandlerFactory(breedSession,
                  boxNumber - breedSession.pageChangeSetting));
          // Back to default value.
          breedSession.changePage = false;
        } else {
          // Get item that was clicked.
          ItemStack stack = getContainer().getItem(slotIndex);
          // If item is a slot.
          if (stack != null && stack.hasTag() && stack.getOrCreateTagElement("slot") != null) {
            // Get pokemon at slot.
            int slot = stack.getOrCreateTagElement("slot").getInt("slot");
            Pokemon pokemon = null;
            if (slotIndex >= 0 && slotIndex <= 5) {
              pokemon = finalBreederParty.get(slot);
            } else {
              pokemon = box.get(slot);
            }

            // Pokemon exists.
            if (pokemon != null) {
              if (breedSession.breederPokemon1 == null) {
                // Selected Pokemon is already in 2nd slot.
                if (breedSession.breederPokemon2 == pokemon) {
                  return;
                }
                // First Pokemon not selected yet, select on first slot.
                breedSession.breederPokemon1 = pokemon;
              } else {
                // First Pokemon already selected, select on second slot if not dupelicate.
                if (breedSession.breederPokemon1 == pokemon) {
                  return;
                }
                breedSession.breederPokemon2 = pokemon;
              }
            }
          }
        }

        updateInventory(inventory);
      }


      /**
       * Disable transferring between slots.
       *
       * @param player - the player that clicked the slot.
       * @param index - the clicked slot's index.
       * @return ItemStack - the item at the.
       */
     
      public ItemStack transferSlot(Player player, int index) {
        return null;
      }


      /**
       * Disable insertion in the slots (return false always).
       *
       * @param slot - the slot that was inserted to.
       * @return boolean - whether it can be inserted into.
       */
     
      public boolean canInsertIntoSlot(Slot slot) {
        return false;
      }


      /**
       * Disable dropping items from inventory.
       *
       * @param player - the player that tried to drop.
       * @param inventory - the PokeBreed GUI.
       */
      protected void dropInventory(Player player, Inventory inventory) {
      }


      /**
       * Action: when player closes PokeBreed GUI, cancel breeding.
       *
       * @param player - the player that was trying to breed Cobblemons.
       */
      
      public void close(Player player) {
        // GUI closed AND it wasn't to change page (player closed).
        if (!breedSession.cancelled && !breedSession.changePage) {
          // Cancel session.
          breedSession.cancel("GUI closed.");
          breedSession.breeder.closeContainer();
        }
      }
    };
  }
}
