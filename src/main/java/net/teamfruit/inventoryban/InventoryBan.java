package net.teamfruit.inventoryban;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;

@Mod(
        modid = InventoryBan.MOD_ID,
        name = InventoryBan.MOD_NAME,
        version = InventoryBan.VERSION,
        acceptableRemoteVersions = "*"
)
public class InventoryBan {

    public static final String MOD_ID = "inventoryban";
    public static final String MOD_NAME = "InventoryBan";
    public static final String VERSION = "1.0-SNAPSHOT";

    public Configuration config;
    public File bannedPath;
    public BanModel banned;
    public MinecraftServer server;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Log.log = event.getModLog();

        config = new Configuration(event.getSuggestedConfigurationFile());
        interval = config.get("general", "interval", 20, "Interval Tick").getInt();
        config.save();

        bannedPath = new File(event.getModConfigurationDirectory(), "inventoryban.json");
        banned = DataUtils.loadFileIfExists(bannedPath, BanModel.class, "Inventory Banned Player List");
        if (banned == null)
            banned = new BanModel();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        server = event.getServer();

        event.registerServerCommand(new InventoryBanCommand(this));
        event.registerServerCommand(new InventoryPerdonCommand(this));
    }

    private int tick = 0;
    private int interval;

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;

        if (++tick < interval)
            return;
        tick = 0;

        PlayerList playerList = server.getPlayerList();
        for (BanModel.BanProfile profile : banned.banned.values()) {
            EntityPlayerMP player = playerList.getPlayerByUUID(profile.id);
            if (player != null) {
                player.inventory.dropAllItems();
            }
        }
    }
}
