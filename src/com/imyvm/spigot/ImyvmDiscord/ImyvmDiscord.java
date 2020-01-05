package com.imyvm.spigot.ImyvmDiscord;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ImyvmDiscord extends JavaPlugin implements CommandExecutor{

    private static final Logger log = Logger.getLogger("Acquisition");
    private FileConfiguration config = getConfig();

    public ChatListener chatListener;

    private List<String> boldCommands = new ArrayList<>();
    private List<String> ignoreCommands = new ArrayList<>();


    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(),
                getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));

        config.addDefault("Prefix", "[World]");
        config.addDefault("Token-ID",
                "https://discordapp.com/api/webhooks/xxxxxxxxxxxxx/" +
                        "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        config.addDefault("OpOverride", true);
        config.addDefault("enbaleProxy", false);
        config.addDefault("PROXY_ADDRESS", "localhost");
        config.addDefault("PORT", 7890);
        boldCommands.add("/tp");
        ignoreCommands.add("/msg");
        config.addDefault("BoldCommands",boldCommands);
        config.addDefault("IgnoreCommands", ignoreCommands);
        config.options().copyDefaults(true);
        saveConfig();

        chatListener = new ChatListener(this);

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender instanceof ConsoleCommandSender) || sender.hasPermission("imyvmdiscord.reload")) {
            if (args.length==0){
                sender.sendMessage(ChatColor.GREEN + "---- ImyvmDiscord ----");
                sender.sendMessage(ChatColor.GREEN + "- /imyvmdiscord reload    - reload this plugin");
            }
            else if (args[0].equalsIgnoreCase("reload") && args.length==1) {
                reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Configuration Reloaded!");
            } else {
                sender.sendMessage(ChatColor.GREEN + "---- ImyvmDiscord ----");
                sender.sendMessage(ChatColor.GREEN + "- /imyvmdiscord reload    - reload this plugin");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have the permission!");
        }
        return true;
    }
}
