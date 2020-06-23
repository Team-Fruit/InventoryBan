package net.teamfruit.inventoryban;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InventoryPerdonCommand extends CommandBase {
    private final InventoryBan mod;

    public InventoryPerdonCommand(InventoryBan mod) {
        this.mod = mod;
    }

    @Override public String getName() {
        return "invperdon";
    }

    @Override public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override public String getUsage(ICommandSender iCommandSender) {
        return "/invperdon <player> インベントリBANを解除します";
    }

    @Override public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("引数が足らんぞ /help invperdon で使い方を見て"));
            return;
        }
        String playerName = args[0];
        EntityPlayerMP player = getPlayer(server, sender, playerName);
        List<BanModel.BanProfile> remove = mod.banned.banned.values().stream()
                .filter(e -> StringUtils.equalsIgnoreCase(playerName, e.name) || (player != null && Objects.equals(player.getGameProfile().getId(), e.id)))
                .collect(Collectors.toList());
        mod.banned.banned.values().removeAll(remove);
        DataUtils.saveFile(mod.bannedPath, BanModel.class, mod.banned, "Inventory Banned Player List");
        sender.sendMessage(new TextComponentString(remove.stream().map(e -> e.name).collect(Collectors.joining(", ")) + "のインベントリBANを解除しました"));
    }

    @Override public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : super.getTabCompletions(server, sender, args, targetPos);
    }
}
