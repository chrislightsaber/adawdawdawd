package dev.thomasqtruong.veryscuffedcobblemonbreeding.commands;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.AbilityPool;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import dev.thomasqtruong.veryscuffedcobblemonbreeding.config.CobblemonConfig;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.config.VeryScuffedCobblemonBreedingConfig;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.permissions.VeryScuffedCobblemonBreedingPermissions;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.screen.PokeBreedHandlerFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;

import net.minecraft.world.entity.player.Player;
//import net.minecraft.text.Text;
import net.minecraft.network.chat.Component;
//import net.minecraft.util.Formatting;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.CommonComponents;

import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.commands.Commands;

import dev.thomasqtruong.veryscuffedcobblemonbreeding.VeryScuffedCobblemonBreeding;

//import static net.minecraft.server.command.CommandManager.literal;

/**
 * Command handler for the breeding process.
 */
public class PokeBreed {
  // Schedules when players are out of cooldown.
  public static ScheduledThreadPoolExecutor scheduler;
  // Keeps track of every breeding session.
  public static HashMap<UUID, BreedSession> breedSessions = new HashMap<>();
  // Used to generate random numbers in functions.
  static Random RNG = new Random();


  /**
   * Registers the command with the command dispatcher.
   * 
   * @param dispatcher - the command dispatcher.
   */
  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    // Set up command.
    

    dispatcher.register(
                Commands.literal("Empty")
                        .requires(src -> VeryScuffedCobblemonBreedingPermissions.checkPermission(src,
                                VeryScuffedCobblemonBreeding.permissions.VIP_POKEBREED_PERMISSION))
                        .executes(c -> {
                            return execute(c);
                        })
    );
    dispatcher.register(
                Commands.literal("pokebreed")
                        .requires(src -> VeryScuffedCobblemonBreedingPermissions.checkPermission(src,
                                VeryScuffedCobblemonBreeding.permissions.VIP_POKEBREED_PERMISSION))
                        .executes(c -> {
                            return execute(c);
                        })
    );


    // Set up scheduler.
    scheduler = new ScheduledThreadPoolExecutor(1, r -> {
      Thread thread = Executors.defaultThreadFactory().newThread(r);
      thread.setName("PokeBreed Thread");
      return thread;
    });
    scheduler.setRemoveOnCancelPolicy(true);
    scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
  }


  /**
   * What to do when the PokeBreed command is executed.
   *
   * @param ctx - the command context.
   */
  private static int execute(CommandContext<CommandSourceStack> ctx) {
    if (ctx.getSource().getPlayer() != null) {
      ServerPlayer player = ctx.getSource().getPlayer();
      // Checks whether player has VIP permissions.
      boolean isVIP = VeryScuffedCobblemonBreedingPermissions.checkPermission(ctx.getSource(),
              VeryScuffedCobblemonBreeding.permissions.VIP_POKEBREED_PERMISSION);

      // Breed session already exists for player.
      if (breedSessions.containsKey(player.getUUID())) {
        BreedSession breedSession = breedSessions.get(player.getUUID());

        // Bred never happened in the first place.
        if (breedSession.timeBred == 0) {
          breedSession.cancel("Possibly a dupelicate somehow.");
          breedSessions.remove(player.getUUID());
        } else {
          // Pokemon was bred before; check if it never got off cooldown.
          long timeSince = System.currentTimeMillis() - breedSession.timeBred;

          // User is a VIP.
          if (isVIP) {
            // Cooldown was supposed to be over!
            if (timeSince > 1000L * 60
                    * VeryScuffedCobblemonBreedingConfig.VIP_COOLDOWN_IN_MINUTES) {
              breedSessions.remove(player.getUUID());
            }
          } else {
            // User is not a VIP.
            // Cooldown was supposed to be over!
            if (timeSince > 1000L * 60 * VeryScuffedCobblemonBreedingConfig.COOLDOWN_IN_MINUTES) {
              breedSessions.remove(player.getUUID());
            }
          }
        }
      }

      // Checking if user is under cooldown still.
      if (breedSessions.containsKey(player.getUUID())) {
        // Get time since in seconds.
        BreedSession breedSession = breedSessions.get(player.getUUID());
        long cooldownDuration = (System.currentTimeMillis() - breedSession.timeBred) / 1000;
        // Total cooldown time - time since = time left.
        if (isVIP) {
          cooldownDuration = (VeryScuffedCobblemonBreedingConfig.VIP_COOLDOWN_IN_MINUTES
                  * 60L) - cooldownDuration;
        } else {
          cooldownDuration = (VeryScuffedCobblemonBreedingConfig.COOLDOWN_IN_MINUTES
                  * 60L) - cooldownDuration;
        }

        //Component toSend = Component.literal("Breed cooldown: " + cooldownDuration + " seconds.")
        //        .formatted(ChatFormatting.RED);
        player.displayClientMessage(Component.translatable("Breed cooldown: " + cooldownDuration + " seconds."), false);
        //player.sendMessage(toSend);

        return -1;
      }

      // Create and start breeding session.
      BreedSession breedSession = new BreedSession(player);
      breedSession.isVIP = isVIP;
      breedSessions.put(player.getUUID(), breedSession);
      breedSession.start();
    }
    return 1;
  }


  /**
   * A breeding session with all of the necessary
   * information and tools to breed.
   */
  public static class BreedSession {
    // Breeder information.
    public ServerPlayer breeder;
    UUID breederUUID;
    boolean isVIP = false;
    // Breeding information.
    public int maxPCSize = 30;  // 30 by default.
    public int pageChangeSetting = 1;  // Amount of pages to change for PC.
    public boolean breederAccept = false;
    public Pokemon breederPokemon1;
    public Pokemon breederPokemon2;
    public long timeBred;
    public boolean cancelled = false;
    public boolean changePage = false;
    public boolean dittoOrSelfBreeding = false;

    // Power item mapping (item name : stat).
    final HashMap<String, Stats> powerItemsMap = new HashMap<>() {{
      put("Power Anklet", Stats.SPEED);
      put("Power Band",   Stats.SPECIAL_DEFENCE);
      put("Power Belt",   Stats.DEFENCE);
      put("Power Bracer", Stats.ATTACK);
      put("Power Lens",   Stats.SPECIAL_ATTACK);
      put("Power Weight", Stats.HP);
    }};


    /**
     * Constructor: set default value and obtain user information.
     *
     * @param breeder - the player that is breeding their Cobblemons.
     */
    public BreedSession(ServerPlayer breeder) {
      this.breeder = breeder;
      this.breederUUID = breeder.getUUID();
      this.timeBred = 0;
    }


    /**
     * Breeding was cancelled for some reason.
     * Display message and remove from session list.
     *
     * @param msg - the message to tell the user; contains the reason for breed cancellation.
     */
    public void cancel(String msg) {
      //Component testttt = Component.literal(String.valueOf("Breed cancelled: " + msg).formatted(ChatFormatting.RED));
      breeder.displayClientMessage(Component.translatable("Breeding has been cancelled."), false);
      breedSessions.remove(breederUUID);
      this.cancelled = true;
    }


    /**
     * Start the breeding process.
     */
    public void start() {
      // Give player GUI.
      PokeBreedHandlerFactory breedHandler = new PokeBreedHandlerFactory(this);
      breeder.openMenu(breedHandler);
    }


    /**
     * Breed 2 Cobblemons together.
     */
    public void doBreed() {
      // Breed cancelled, why are we still doing the breed?
      if (this.cancelled) {
        System.out.println("Something funky is goin' on");
        cancel("Something funky is goin' on.");
        return;
      }
      // Only provided 1 or 0 Pokemon to breed or pokemons don't exist.
      if (breederPokemon1 == null || breederPokemon2 == null) {
        cancel("Not enough Cobblemons provided.");
        return;
      }

      // Failed the breeding conditions.
      if (!checkBreed()) {
        return;
      }

      // Proceeding to breed.
      cancelled = true;

      // Pokemons exist.
      if (breederPokemon1 != null && breederPokemon2 != null) {
        // Get the bred Pokemon.
        Pokemon baby = getPokemonBred();

        // Add Pokemon to party.
        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(breeder);
        party.add(baby);

        // Send success message and set cooldown.
         
        //Component toSend = Component.literal(String.valueOf("Breed complete!").formatted(ChatFormatting.GREEN));
        breeder.displayClientMessage(Component.translatable("Breed complete!"), false);
        //breeder.sendMessage(toSend);
        // Player has VIP status.
        if (isVIP) {
          scheduler.schedule(() -> {
            breedSessions.remove(breederUUID);
          }, VeryScuffedCobblemonBreedingConfig.VIP_COOLDOWN_IN_MINUTES, TimeUnit.MINUTES);
        } else {
          // Player does not have VIP status.
          scheduler.schedule(() -> {
            breedSessions.remove(breederUUID);
          }, VeryScuffedCobblemonBreedingConfig.COOLDOWN_IN_MINUTES, TimeUnit.MINUTES);
        }
        timeBred = System.currentTimeMillis();
      } else {
        cancel("One of the Cobblemons does not exist!");
      }
    }


    /**
     * Checks whether the breeding combination is valid or not.
     *
     * @return boolean - whether the breeding combination is valid or not.
     */
    public boolean checkBreed() {
      // Get Pokemon attributes.
      String pokemon1Gender = String.valueOf(breederPokemon1.getGender());
      String pokemon2Gender = String.valueOf(breederPokemon2.getGender());
      String pokemon1Species = String.valueOf(breederPokemon1.getSpecies());
      String pokemon2Species = String.valueOf(breederPokemon2.getSpecies());
      Set<EggGroup> pokemon1EggGroup = breederPokemon1.getForm().getEggGroups();
      Set<EggGroup> pokemon2EggGroup = breederPokemon2.getForm().getEggGroups();

      // Cannot breed same genders (unless genderless + ditto).
      if (pokemon1Gender.equals(pokemon2Gender) && !pokemon1Gender.equals("GENDERLESS")) {
        cancel("Cannot breed same genders.");
        return false;
      }
      // Both dittos.
      if (pokemon1Species.equals("ditto") && pokemon2Species.equals("ditto")) {
        cancel("Cannot breed dittos.");
        return false;
      }
      // Any are part of the Undiscovered egg group.
      if (pokemon1EggGroup.contains(EggGroup.UNDISCOVERED)
              || pokemon2EggGroup.contains(EggGroup.UNDISCOVERED)) {
        // In undiscovered egg group.
        cancel("Cannot breed with Undiscovered egg group.");
        return false;
      }

      // Get matching egg groups.
      int matchingEggGroupCount = 0;
      for (EggGroup g : pokemon1EggGroup) {
        if (pokemon2EggGroup.contains(g)) {
          ++matchingEggGroupCount;
        }
      }

      // None are ditto.
      if (!(pokemon1Species.equals("ditto") || pokemon2Species.equals("ditto"))) {
        if (pokemon1Gender.equals("GENDERLESS") && pokemon2Gender.equals("GENDERLESS")
                && !pokemon1Species.equals(pokemon2Species)) {
          // Both are genderless and not the same species.
          cancel("Cannot breed two differing genderless species (unless theres a ditto).");
          return false;
        } else if (pokemon1Gender.equals("GENDERLESS") || pokemon2Gender.equals("GENDERLESS")) {
          // One is genderless.
          cancel("Cannot breed a genderless non-ditto species with a regular Cobblemon.");
          return false;
        } else if (matchingEggGroupCount == 0) {
          // Not the same egg group.
          cancel("Cannot breed two Cobblemons from different egg groups.");
          return false;
        }
      }

      // Both Pokemons look breedable, check if one is ditto or self.
      if ((pokemon1Species.equals("ditto") || pokemon2Species.equals("ditto"))
              || pokemon1Species.equals(pokemon2Species)) {
        dittoOrSelfBreeding = true;
      }
      return true;
    }


    /**
     * Breeds the Cobblemons and gets the baby.
     * The baby will have default values and inherit some things
     * from the parent.
     *
     * @return Pokemon - the baby that was bred.
     */
    public Pokemon getPokemonBred() {
      Pokemon baby;

      // Ditto/self breeding = itself.
      if (dittoOrSelfBreeding) {
        // Pokemon 1 is not ditto.
        if (!String.valueOf(breederPokemon1.getSpecies()).equals("ditto")) {
          baby = breederPokemon1.clone(true, true);
        } else {
          // Pokemon 2 is not ditto.
          baby = breederPokemon2.clone(true, true);
        }
      } else {
        // Same egg group breeding.
        // Pokemon1 is the mother, offspring = same species as mother.
        if (breederPokemon1.getGender() == Gender.FEMALE) {
          baby = breederPokemon1.clone(true, true);
        } else {
          // Pokemon2 is the mother.
          baby = breederPokemon2.clone(true, true);
        }
      }

      // Get base evolution.
      while (baby.getPreEvolution() != null) {
        Species preEvolution = baby.getPreEvolution().getSpecies();
        baby.setSpecies(preEvolution);
      }

      // SPECIAL CASE: manaphy -> phione.
      if (String.valueOf(baby.getSpecies()).equals("manaphy")) {
        baby.setSpecies(Objects.requireNonNull(PokemonSpecies.INSTANCE.getByName("phione")));
      }

      // Got the Pokemon, time to set its proper default.
      baby.setEvs(new EVs());
      baby.setExperienceAndUpdateLevel(0);
      baby.removeHeldItem();
      baby.initializeMoveset(true);
      baby.heal();

      // Generate friendship (base% - 30%).
      int intRNG = RNG.nextInt() % 77 + baby.getForm().getBaseFriendship();
      baby.setFriendship(intRNG, true);

      // Reset to default and RNG for shiny.
      baby.setShiny(false);
      // Shinies enabled.
      if (CobblemonConfig.shinyRate > 0) {
        CompoundTag fullNbt1 = breederPokemon1.heldItem().getTag();
        CompoundTag fullNbt2 = breederPokemon2.heldItem().getTag();

        // Get items' title NBT if exists.
        String parent1Item = "";
        if (fullNbt1 != null && fullNbt1.contains("breedItem")) {
          parent1Item = fullNbt1.getString("breedItem");
        }
        String parent2Item = "";
        if (fullNbt2 != null && fullNbt2.contains("breedItem")) {
          parent2Item = fullNbt2.getString("breedItem");
        }

        // Check if its a shiny breeding charm
        if (parent1Item.equals("ShinyBreedingCharm") || parent2Item.equals("ShinyBreedingCharm"))
        {
          intRNG = RNG.nextInt(500);  // 0-shinyRate 
        }
        else{
          intRNG = RNG.nextInt(CobblemonConfig.shinyRate);  // 0-shinyRate
        }
        
        
        // Hit shiny (1/shinyRate chance).
        if (intRNG == 0) {
          baby.setShiny(true);
        }
      }


      baby.setGender(getRandomGender(baby));
      baby.setAbility(getRandomAbility(baby));
      baby.setIvs(getIVs());
      baby.setNature(getRandomNature());

      return baby;
    }


    /**
     * Retrieves a random gender based on the male ratio for that Cobblemon.
     *
     * @param getFor - the Cobblemon to get the gender for.
     * @return Gender - the randomly chosen gender.
     */
    public Gender getRandomGender(Pokemon getFor) {
      int maleRatio = (int) (getFor.getForm().getMaleRatio() * 100);
      int genderRNG = RNG.nextInt(101);

      if (maleRatio < 0) {
        // No male ratio (genderless).
        return Gender.GENDERLESS;
      } else if (genderRNG <= maleRatio) {
        // In male ratio (male).
        return Gender.MALE;
      }
      // Is female.
      return Gender.FEMALE;
    }


    /**
     * Checks whether the Cobblemon has a hidden ability or not.
     *
     * @param toCheck - the Cobblemon to check.
     * @return boolean - whether the Cobblemon has a hidden ability or not.
     */
    public boolean hasHiddenAbility(Pokemon toCheck) {
      List<AbilityTemplate> possibleHiddens = new ArrayList<>();

      // Get list of hidden abilities.
      for (PotentialAbility potentialAbility : toCheck.getForm().getAbilities()) {
        if (potentialAbility.getPriority() == Priority.LOW) {
          possibleHiddens.add(potentialAbility.getTemplate());
        }
      }

      return possibleHiddens.contains(toCheck.getAbility().getTemplate());
    }


    /**
     * Gets a random ability for a Cobblemon.
     * Supports hidden abilities.
     *
     * @param getFor - the Cobblemon to get a random ability for.
     * @return Ability - the ability that was chosen randomly.
     */
    public Ability getRandomAbility(Pokemon getFor) {
      // Priority.LOWEST = common ability, Priority.LOW = hidden ability.
      // Get all possible abilities for Pokemon.
      AbilityPool possibleAbilities = getFor.getForm().getAbilities();
      // Defaulting to common ability.
      int intRNG = 100;

      // Get lists of all the possible hidden/common abilities.
      List<AbilityTemplate> possibleHiddens = new ArrayList<>();
      List<AbilityTemplate> possibleCommons = new ArrayList<>();

      for (PotentialAbility potentialAbility : possibleAbilities) {
        // Is a hidden ability.
        if (potentialAbility.getPriority() == Priority.LOW) {
          possibleHiddens.add(potentialAbility.getTemplate());
        } else if (potentialAbility.getPriority() == Priority.LOWEST) {
          // Is a common ability.
          possibleCommons.add(potentialAbility.getTemplate());
        }
      }

      // Parent(s) has hidden ability, offspring has a 60% chance of getting it too.
      if (hasHiddenAbility(breederPokemon1) || hasHiddenAbility(breederPokemon2)) {
        intRNG = RNG.nextInt(100);  // 0-99
      }

      // Hit hidden ability.
      if (intRNG < 60) {  // 0-59 (60%)
        // Add every hidden ability to possibleDraws, draw random hidden if exists.
        if (possibleHiddens.size() > 0) {
          intRNG = RNG.nextInt(possibleHiddens.size());
          return new Ability(possibleHiddens.get(intRNG), false);
        }
      } else {
        // Did not hit hidden ability, draw random common if exists.
        if (possibleCommons.size() > 0) {
          intRNG = RNG.nextInt(possibleCommons.size());
          return new Ability(possibleCommons.get(intRNG), false);
        }
      }

      // No ability found.
      return getFor.getAbility();
    }


    /**
     * Gets IVs for the baby.
     * IVs will be randomly inherited from parents or randomized.
     *
     * @return IVs - the baby's new IVs.
     */
    public IVs getIVs() {
      List<Stats> toSet = new ArrayList<>();
      toSet.add(Stats.SPEED);
      toSet.add(Stats.SPECIAL_DEFENCE);
      toSet.add(Stats.DEFENCE);
      toSet.add(Stats.ATTACK);
      toSet.add(Stats.SPECIAL_ATTACK);
      toSet.add(Stats.HP);

      IVs newIVs = new IVs();

      // Get parents' items' NBT.
      CompoundTag fullNbt1 = breederPokemon1.heldItem().getTag();
      CompoundTag fullNbt2 = breederPokemon2.heldItem().getTag();

      // Get items' title NBT if exists.
      String parent1Item = "";
      if (fullNbt1 != null && fullNbt1.contains("breedItem")) {
        parent1Item = fullNbt1.getString("breedItem");
      }
      String parent2Item = "";
      if (fullNbt2 != null && fullNbt2.contains("breedItem")) {
        parent2Item = fullNbt2.getString("breedItem");
      }

      // Default is 3, 5 with destiny knot.
      int amountOfIVsToGet = 3;
      if (parent1Item.equals("Destiny Knot") || parent2Item.equals("Destiny Knot")) {
        amountOfIVsToGet = 5;
      }

      // Count how many Cobblemons have a power item.
      int powerItemsCount = 0;
      if (powerItemsMap.containsKey(parent1Item)) {
        ++powerItemsCount;
      }
      if (powerItemsMap.containsKey(parent2Item)) {
        ++powerItemsCount;
      }

      // Initially select parent1 to get IVs from.
      int intRNG = 0;
      // Both parents have a power item.
      if (powerItemsCount == 2) {
        intRNG = RNG.nextInt(2);  // Choose a random parent's IV.
      } else if (powerItemsCount == 1) {
        // Only one parent has a power item.
        // Parent 2 has the item.
        if (powerItemsMap.containsKey(parent2Item)) {
          intRNG = 1;
        }
      }

      // Get IV from parent1 if holding power item.
      if (powerItemsCount > 0 && intRNG == 0) {
        Stats stat = powerItemsMap.get(parent1Item);
        newIVs.set(stat, breederPokemon1.getIvs().getOrDefault(stat));
        --amountOfIVsToGet;
        toSet.remove(powerItemsMap.get(parent1Item));
      } else if (powerItemsCount > 0) {
        // Get IV from parent2 if holding power item.
        Stats stat = powerItemsMap.get(parent2Item);
        newIVs.set(stat, breederPokemon2.getIvs().getOrDefault(stat));
        --amountOfIVsToGet;
        toSet.remove(powerItemsMap.get(parent2Item));
      }

      // Inherit stats randomly from parents.
      for (int i = 0; i < amountOfIVsToGet; ++i) {
        int statIndex = RNG.nextInt(toSet.size());  // 0-(size - 1).
        int randomParent = RNG.nextInt(2);
        Stats stat = toSet.get(statIndex);

        // Parent 1's stat gets inherited.
        if (randomParent == 0) {
          newIVs.set(stat, breederPokemon1.getIvs().getOrDefault(stat));
        } else {
          // Parent 2's stat gets inherited.
          newIVs.set(stat, breederPokemon2.getIvs().getOrDefault(stat));
        }
        toSet.remove(statIndex);
      }

      // Get the rest of the stats (0-31).
      for (Stats stat : toSet) {
        newIVs.set(stat, RNG.nextInt(32));
      }

      return newIVs;
    }


    /**
     * Gets a random nature.
     *
     * @return Nature - the random nature.
     */
    public Nature getRandomNature() {
      // Get parents' items' NBT.
      CompoundTag fullNbt1 = breederPokemon1.heldItem().getTag();
      CompoundTag fullNbt2 = breederPokemon2.heldItem().getTag();

      // Get items' title NBT if exists.
      String parent1Item = "";
      if (fullNbt1 != null && fullNbt1.contains("breedItem")) {
        parent1Item = fullNbt1.getString("breedItem");
      }
      String parent2Item = "";
      if (fullNbt2 != null && fullNbt2.contains("breedItem")) {
        parent2Item = fullNbt2.getString("breedItem");
      }

      // Both have everstones.
      if (parent1Item.equals("Everstone") && parent2Item.equals("Everstone")) {
        int parentRNG = RNG.nextInt(2);
        // First parent's nature inherited.
        if (parentRNG == 0) {
          return breederPokemon1.getNature();
        }
        // Second parent's nature inherited.
        return breederPokemon2.getNature();
      }
      // Only parent 1 has everstone.
      if (parent1Item.equals("Everstone")) {
        return breederPokemon1.getNature();
      }
      if (parent2Item.equals("Everstone")) {
        return breederPokemon2.getNature();
      }

      // No one has everstone, randomize.
      return Natures.INSTANCE.getRandomNature();
    }
  }
}
