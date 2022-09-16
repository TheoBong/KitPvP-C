package com.bongbong.kitpvp.commands.impl;

import com.bongbong.kitpvp.commands.BaseCommand;
import com.bongbong.kitpvp.util.message.CC;
import org.bukkit.command.CommandSender;

public class HelpCommand extends BaseCommand {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(CC.BOARD_SEPARATOR);
        sender.sendMessage(CC.SECONDARY + "/msg <sender> <message>" + CC.GRAY + " - " + CC.PRIMARY + "message another player");
        sender.sendMessage(CC.SECONDARY + "/r <message>" + CC.GRAY + " - " + CC.PRIMARY + "reply to the person who last messaged you");
        sender.sendMessage(CC.SECONDARY + "/(un)ignore <sender>" + CC.GRAY + " - " + CC.PRIMARY + "ignore or unignore an offline/online player");
        sender.sendMessage(CC.SECONDARY + "/helpop <request>" + CC.GRAY + " - " + CC.PRIMARY + "message all online staff for assistance");
        sender.sendMessage(CC.SECONDARY + "/report <sender>" + CC.GRAY + " - " + CC.PRIMARY + "report another player for breaking the rules");
        sender.sendMessage(CC.SECONDARY + "/settings" + CC.GRAY + " - " + CC.PRIMARY + "opens your player settings menu");
        sender.sendMessage(CC.SECONDARY + "/spawn" + CC.GRAY + " - " + CC.PRIMARY + "returns you to spawn");
        sender.sendMessage(CC.SECONDARY + "/shop" + CC.GRAY + " - " + CC.PRIMARY + "opens the pvp shop");
        sender.sendMessage(CC.SECONDARY + "/clearkit" + CC.GRAY + " - " + CC.PRIMARY + "clears your kit and returns hotbar items");
        sender.sendMessage(CC.SECONDARY + "/stats <sender>" + CC.GRAY + " - " + CC.PRIMARY + "view your own or another player's stats");
        sender.sendMessage(CC.SECONDARY + "/repair" + CC.GRAY + " - " + CC.PRIMARY + "repairs your armor for 50 credits");
        sender.sendMessage(CC.BOARD_SEPARATOR);
    }
}
