package dev.thomasqtruong.veryscuffedcobblemonbreeding;

import com.mojang.brigadier.CommandDispatcher;

import dev.architectury.event.events.common.CommandRegistrationEvent;
//import com.mojang.brigadier.CommandDispatcher;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.commands.*;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.commands.PokeBreed;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.config.CobblemonConfig;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.config.VeryScuffedCobblemonBreedingConfig;
import dev.thomasqtruong.veryscuffedcobblemonbreeding.permissions.VeryScuffedCobblemonBreedingPermissions;
//import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
//import net.minecraft.server.command.CommandManager;
//import net.minecraft.server.command.ServerCommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(VeryScuffedCobblemonBreeding.MODID)
public class VeryScuffedCobblemonBreeding {
    public static final String MODID = "veryscuffedcobblemonbreeding";
    public static VeryScuffedCobblemonBreedingPermissions permissions;

    public VeryScuffedCobblemonBreeding() {
        System.out.println("VeryScuffedCobblemonBreedingPermissions - Initialized");
        new VeryScuffedCobblemonBreedingConfig(); // must load before permissions so perms use default permission level.
        permissions = new VeryScuffedCobblemonBreedingPermissions();

        // Load official Cobblemon's config.
        new CobblemonConfig();

        MinecraftForge.EVENT_BUS.addListener(this::commands);
        //CommandRegistrationEvent.EVENT.register(VeryScuffedCobblemonBreeding::registerCommands);
        //RegisterCommandsEvent event = new RegisterCommandsEvent(dispatcher, environment, context);
    }   

    public void commands(RegisterCommandsEvent e) {
        PokeBreed.register(e.getDispatcher());
    }

    
}
/*@Mod(VeryScuffedCobblemonBreeding.MODID)
public class VeryScuffedCobblemonBreeding {
    public static final String MODID = "veryscuffedcobblemonbreeding";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static VeryScuffedCobblemonBreedingPermissions permissions;

    public WhereAreMyTMs() {
        Registration.init();
        ModSetup.setup();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WhereAreMyTMsConfig.CONFIG_BUILDER);
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(ModSetup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientSetup::init));
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name.toLowerCase(Locale.ROOT));
    }


} */
