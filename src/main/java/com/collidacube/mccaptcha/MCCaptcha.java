package com.collidacube.mccaptcha;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MCCaptcha extends JavaPlugin {

    private static MCCaptcha instance = null;
    public static MCCaptcha getInstance() {
        return instance;
    }
    private static boolean updated = false;
    private static String updateUrl = null;
    public static boolean isUpdated() {
        return updated;
    }
    public static String getUpdateUrl() {
        return updateUrl;
    }

    @Override
    public void onEnable() {
        if (Config.load()) {
            instance = this;
            new CaptchaCommand(this);
            Bukkit.getPluginManager().registerEvents(new Captcha(), this);

            try {
                updateUrl = ServerCommunication.getResponse("https://captcha.collidacube.repl.co/version");
                updated = updateUrl.equals("https://github.com/CollidaDev/MCCaptcha/releases/tag/v1.0-beta.1");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            getLogger().severe("Error occurred when loading configuration. Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }

}
