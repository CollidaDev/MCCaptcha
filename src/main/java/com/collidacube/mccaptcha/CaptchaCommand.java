package com.collidacube.mccaptcha;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaptchaCommand implements CommandExecutor, TabCompleter {

    public CaptchaCommand(MCCaptcha plugin) {
        PluginCommand cmd = plugin.getCommand("mccaptcha");
        assert cmd != null;
        cmd.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("verify")) return onCommand_verify(sender, args);
            if (args[0].equalsIgnoreCase("reload")) return onCommand_reload(sender);
        } else {
            sender.sendMessage("Invalid usage");
        }
        return true;
    }

    public boolean onCommand_reload(CommandSender sender) {
        String permission = Config.getProperty("reload-permission");
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage("§cInsufficient permissions!");
            return true;
        }

        System.out.println("Reloading...");
        if (Config.load()) {
            System.out.println("Successfully reloaded!");
            sender.sendMessage("§aSuccessfully reloaded");
        } else {
            sender.sendMessage("§cError occurred while reloading! Check console for details.");
        }
        return true;
    }

    public boolean onCommand_verify(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length >= 2) {
            String permission = Config.getProperty("verify-others-permission");
            if (permission != null && sender.hasPermission(permission)) {
                player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage("§cPlayer is not online!");
                    return true;
                }
                Captcha.verify(player);
                sender.sendMessage("§aSuccessfully verified" + args[0]);
                player.sendMessage("§aYou have been verified!");
            } else {
                sender.sendMessage("You do not have permission to verify other users!");
            }
            return true;
        }

        boolean verified = ServerCommunication.getVerified(player.getUniqueId().toString());
        if (verified && !Captcha.verify(player)) {
            sender.sendMessage("§aSuccessfully verified");
            String callback = Config.getProperty("callback-command");
            if (callback != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), callback + " " + player.getName());
            }
        } else {
            sender.sendMessage("§cPlayer was either already verified or did not pass the captcha!");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("verify", "reload");
        } if (args.length == 2 && args[0].equalsIgnoreCase("verify")) {
            List<String> suggestions = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().startsWith(args[1])) suggestions.add(player.getName());
            } return suggestions;
        } return null;
    }

}
