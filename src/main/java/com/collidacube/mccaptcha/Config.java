package com.collidacube.mccaptcha;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Config {

    private static final File homeDir = new File("./plugins/MCCaptcha");
    private static final File configFile = new File(homeDir, "config.txt");

    private static final HashMap<String, String> properties = new HashMap<>();

    public static String getProperty(String property) {
        return properties.get(property);
    }

    private static void checkFile(File file, Runnable initializer) {
        if (!file.exists()) {
            initializer.run();
        }
    }

    private static void writeFileContent(File file, String... content) throws IOException {
        FileWriter writer = new FileWriter(file);
        for (String line : content) writer.write(line);
        writer.close();
    }

    private static String[] getFileContent(File file) throws FileNotFoundException {
        Scanner reader = new Scanner(file);
        List<String> str = new ArrayList<>();
        while (reader.hasNextLine()) str.add(reader.nextLine());
        return str.toArray(new String[0]);
    }

    private static void checkFiles() {
        checkFile(homeDir, homeDir::mkdirs);
        checkFile(configFile, () -> {
            try {
                configFile.createNewFile();
                writeFileContent(configFile
                        , "# property: value\n"
                        , "# Permission allowing players to bypass captcha\n"
                        , "bypass-permission: mccaptcha.bypass\n"
                        , "# Command to call when player successfully passes captcha\n"
                        , "# Will execute like: /%CALLBACK_COMMAND% %USERNAME%"
                        , "callback-command: none\n"
                        , "# Permission to reload configuration\n"
                        , "reload-permission: mccaptcha.reload\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static String trimString(String str) {
        int i = 0;
        while (str.charAt(i) == ' ') i++;
        return str.substring(i);
    }

    private static void parseConfigOption(String configuration) {
        if (configuration.startsWith("#")) return;

        String[] args = configuration.split(":", 2); // [ propertyLabel, value ]
        args[1] = trimString(args[1]);
        if (args.length == 2) {
            properties.put(args[0], args[1].equals("none") ? null : args[1]);
        }
    }

    private static void loadConfigOptions() throws FileNotFoundException {
        String[] content = getFileContent(configFile);
        for (String line : content) parseConfigOption(line);
    }

    public static boolean load() {
        try {
            checkFiles();
            loadConfigOptions();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } return true;
    }

}
