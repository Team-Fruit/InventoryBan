package net.teamfruit.inventoryban;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class InventoryBanCommand extends CommandBase {
    private final InventoryBan mod;

    public InventoryBanCommand(InventoryBan mod) {
        this.mod = mod;
    }

    @Override public String getName() {
        return "invban";
    }

    @Override public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override public String getUsage(ICommandSender iCommandSender) {
        return "/invban <player> インベントリBANします\n/invperdonで解除";
    }

    @Override public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("引数が足らんぞ /help invban で使い方を見て"));
            return;
        }
        String playerName = args[0];
        EntityPlayerMP player = getPlayer(server, sender, playerName);
        if (player == null) {
            sender.sendMessage(new TextComponentString("プレイヤーが見つかりません"));
            return;
        }
        UUID uuid = player.getGameProfile().getId();
        mod.banned.banned.put(uuid.toString(), new BanModel.BanProfile(uuid, player.getName()));
        DataUtils.saveFile(mod.bannedPath, BanModel.class, mod.banned, "Inventory Banned Player List");
        server.getPlayerList().sendMessage(new TextComponentString(TextFormatting.GOLD + player.getGameProfile().getName() + "をインベントリBANにしました"));
    }

    @Override public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : super.getTabCompletions(server, sender, args, targetPos);
    }
}
