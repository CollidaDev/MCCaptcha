package com.collidacube.mccaptcha;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class Captcha implements Listener {

    private static final HashMap<Player, Boolean> verifiedPlayers = new HashMap<>();

    // returns true if player is already verified
    public static boolean verify(Player player) {
        Boolean oldValue = verifiedPlayers.put(player, true);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        return oldValue != null && oldValue;
    }

    public static boolean isVerified(Player player) {
        Boolean verified = verifiedPlayers.get(player);
        return verified != null && verified;
    }

    private static TextComponent buildVerifyURLMessage(Player player) {
        String url = "https://captcha.collidacube.repl.co/home?uuid=" + player.getUniqueId().toString();
        TextComponent component = new TextComponent("§f §b" + url);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to verify!")));
        return component;
    }

    private static TextComponent buildCheckRequestMessage() {
        TextComponent component = new TextComponent("§f §a§l[VERIFY]");
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mccaptcha verify"));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to verify captcha!")));
        return component;
    }

    private static void sendVerificationMessage(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(MCCaptcha.getInstance(), () -> {
            for (int i = 0; i < 100; i++) {
                player.sendMessage("§a");
                player.sendMessage("§b");
            }
            player.sendMessage("§f");
            player.sendMessage("§f §c§lCAPTCHA »");
            player.sendMessage("§f §7Please go to the following url to verify you are not a bot!");
            player.spigot().sendMessage(buildVerifyURLMessage(player));
            player.spigot().sendMessage(buildCheckRequestMessage());
            player.sendMessage("§f");
            sendOutdatedMessage(player);
        }, 5);
    }

    private static TextComponent buildOutdatedMessage() {
        TextComponent component = new TextComponent("§c§lMCCaptcha §8| §fThis plugin is currently out of date. You can get the latest version §nhere§f.");
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to download")));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, MCCaptcha.getUpdateUrl()));
        return component;
    }

    private static final TextComponent OUTDATED_MESSAGE = buildOutdatedMessage();

    private static void sendOutdatedMessage(Player player) {
        if (player.hasPermission("mccaptcha.updates") && !MCCaptcha.isUpdated()) {
            player.spigot().sendMessage(OUTDATED_MESSAGE);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String bypassPermission = Config.getProperty("bypass-permission");
        if (bypassPermission != null && player.hasPermission(bypassPermission)) {
            verify(player);
            sendOutdatedMessage(player);
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, false, true, true));
        sendVerificationMessage(player);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        boolean verified = isVerified(event.getPlayer());
        if (!verified) event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        boolean verified = isVerified(event.getPlayer());
        if (!verified) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        boolean verified = isVerified((Player)event.getWhoClicked());
        if (!verified) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        boolean verified = isVerified((Player)event.getPlayer());
        if (!verified) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryCreative(InventoryCreativeEvent event) {
        boolean verified = isVerified((Player)event.getWhoClicked());
        if (!verified) event.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        boolean verified = isVerified(event.getPlayer());
        if (!verified) event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/mccaptcha") || event.getMessage().startsWith("/captcha")) return;
        boolean verified = isVerified(event.getPlayer());
        if (!verified) event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        verifiedPlayers.remove(event.getPlayer());
    }

}
