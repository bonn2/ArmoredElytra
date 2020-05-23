package nl.pim16aap2.armoredElytra.util;

import nl.pim16aap2.armoredElytra.ArmoredElytra;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class ConfigLoader
{
    private final String header;
    private boolean allowStats;
    private boolean unbreakable;
    private boolean enableDebug;
    private String languageFile;
    private int GOLD_TO_FULL;
    private int IRON_TO_FULL;
    private boolean uninstallMode;
    private boolean checkForUpdates;
    private boolean autoDLUpdate;
    private int LEATHER_TO_FULL;
    private int DIAMONDS_TO_FULL;
    private boolean noFlightDurability;
    private List<String> allowedEnchantments;
    private boolean allowMultipleProtectionEnchantments;
    public boolean bypassWearPerm;
    public boolean bypassCraftPerm;

    private ArrayList<nl.pim16aap2.armoredElytra.util.ConfigOption<?>> configOptionsList;
    private ArmoredElytra plugin;

    public ConfigLoader(ArmoredElytra plugin)
    {
        this.plugin = plugin;
        configOptionsList = new ArrayList<>();
        header = "Config file for ArmoredElytra. Don't forget to make a backup before making changes!";
        makeConfig();
    }

    // Read the current config, the make a new one based on the old one or default values, whichever is applicable.
    private void makeConfig()
    {
        // All the comments for the various config options.
        String[] unbreakableComment =
            {
                "Setting this to true will cause armored elytras to be unbreakable.",
                "Changing this to false will NOT make unbreakable elytras breakable again!"
            };
        String[] flyDurabilityComment =
            {
                "Setting this to true will cause armored elytras to not lose any durability while flying.",
                "This is not a permanent option and will affect ALL elytras."
            };
        String[] repairComment =
            {
                "Amount of items it takes to fully repair an armored elytra",
                "Repair cost for every tier of armored elytra in number of items to repair 100%."
            };
        String[] enchantmentsComment =
            {
                "List of enchantments that are allowed to be put on an armored elytra.",
                "If you do not want to allow any enchantments at all, remove them all and add \"NONE\"",
                "You can find supported enchantments here:",
                "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html",
                "Note that only 1 protection enchantment (PROTECTION_FIRE, PROTECTION_ENVIRONMENTAL etc) can be active on an elytra."
            };
        String[] updateComment =
            {
                "Allow this plugin to check for updates on startup. It will not download new versions unless \"auto-update is enabled\'!"
            };
        String[] autoDLUpdateComment =
            {
                "Allow this plugin to automatically download new updates. They will be applied on restart.",
                "This option has no effect if \"checkForUpdates\" is disabled."
            };
        String[] bStatsComment =
            {
                "Allow this plugin to send (anonymised) stats using bStats. Please consider keeping it enabled.",
                "It has a negligible impact on performance and more users on stats keeps me more motivated to support this plugin!"
            };
        String[] debugComment =
            {
                "Print debug messages to console. You will most likely never need this."
            };
        String[] uninstallComment =
            {
                "Setting this to true will disable this plugin and remove any armored elytras it can find.",
                "It will check player's inventories and their end chest upon login and any regular chest when it is opened.",
                "This means it will take a while for all armored elytras to be removed from your server, but it doesn't take up ",
                "a lot of resources, so you can just leave the plugin enabled and ignore it.",
                "Please do not forget to MAKE A BACKUP before enabling this option!"
            };
        String[] languageFileComment =
            {
                "Specify a language file to be used. Note that en_US.txt will get regenerated!"
            };
        String[] allowMultipleProtectionEnchantmentsComment =
            {
                "Allow more than 1 type of protection enchantment on a single armored elytra. ",
                "If true, you could have both blast protection and environmental protection at the same time.",
                "If false, the second enchantment (while crafting) will override the first. So combining an armored",
                "elytra that has the protection enchantment with an enchanted book that has the blast protection enchantment",
                "would result in removal of the protection enchantment and addition of the blast protection enchantment."
            };
        String[] permissionsComment =
            {
                "Globally bypass permissions for wearing and/or crafting amored elytras. Useful if permissions are unavailable."
            };


        // Set default list of allowed enchantments.
        allowedEnchantments = new ArrayList<>(Arrays.asList("DURABILITY", "PROTECTION_FIRE", "PROTECTION_EXPLOSIONS",
                                                            "PROTECTION_PROJECTILE", "PROTECTION_ENVIRONMENTAL",
                                                            "THORNS",
                                                            "BINDING_CURSE", "VANISHING_CURSE", "MENDING"));

        FileConfiguration config = plugin.getConfig();

        unbreakable = addNewConfigOption(config, "unbreakable", false, unbreakableComment);
        noFlightDurability = addNewConfigOption(config, "noFlightDurability", false, flyDurabilityComment);
        LEATHER_TO_FULL = addNewConfigOption(config, "leatherRepair", 6, repairComment);
        GOLD_TO_FULL = addNewConfigOption(config, "goldRepair", 5, null);
        IRON_TO_FULL = addNewConfigOption(config, "ironRepair", 4, null);
        DIAMONDS_TO_FULL = addNewConfigOption(config, "diamondsRepair", 3, null);
        allowedEnchantments = addNewConfigOption(config, "allowedEnchantments", allowedEnchantments,
                                                 enchantmentsComment);
        allowMultipleProtectionEnchantments = addNewConfigOption(config, "allowMultipleProtectionEnchantments", false,
                                                                 allowMultipleProtectionEnchantmentsComment);
        checkForUpdates = addNewConfigOption(config, "checkForUpdates", true, updateComment);
        autoDLUpdate = addNewConfigOption(config, "auto-update", true, autoDLUpdateComment);
        allowStats = addNewConfigOption(config, "allowStats", true, bStatsComment);
        enableDebug = addNewConfigOption(config, "enableDebug", false, debugComment);
        uninstallMode = addNewConfigOption(config, "uninstallMode", false, uninstallComment);
        languageFile = addNewConfigOption(config, "languageFile", "en_US", languageFileComment);
        bypassWearPerm = addNewConfigOption(config, "bypassWearPermissions", true, permissionsComment);
        bypassCraftPerm = addNewConfigOption(config, "bypassCraftPermissions", true, null);

        writeConfig();
    }

    private <T> T addNewConfigOption(FileConfiguration config, String optionName, T defaultValue, String[] comment)
    {
        nl.pim16aap2.armoredElytra.util.ConfigOption<T> option = new nl.pim16aap2.armoredElytra.util.ConfigOption<>(
            plugin, config, optionName, defaultValue, comment);
        configOptionsList.add(option);
        return option.getValue();
    }

    // Write new config file.
    private void writeConfig()
    {
        // Write all the config options to the config.yml.
        try
        {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists())
                dataFolder.mkdir();

            File saveTo = new File(plugin.getDataFolder(), "config.yml");
            if (!saveTo.exists())
                saveTo.createNewFile();
            else
            {
                saveTo.delete();
                saveTo.createNewFile();
            }
            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);

            if (header != null)
                pw.println("# " + header + "\n");

            for (int idx = 0; idx < configOptionsList.size(); ++idx)
                pw.println(configOptionsList.get(idx).toString() +
                               // Only print an additional newLine if the next config option has a comment.
                               (idx < configOptionsList.size() - 1 &&
                                    configOptionsList.get(idx + 1).getComment() == null ? "" : "\n"));

            pw.flush();
            pw.close();
        }
        catch (IOException e)
        {
            Bukkit.getLogger().log(Level.SEVERE,
                                   "Could not save config.yml! Please contact pim16aap2 and show him the following code:");
            e.printStackTrace();
        }
    }


    public boolean allowStats()
    {
        return allowStats;
    }

    public boolean unbreakable()
    {
        return unbreakable;
    }

    public boolean enableDebug()
    {
        return enableDebug;
    }

    public String languageFile()
    {
        return languageFile;
    }

    public int LEATHER_TO_FULL()
    {
        return LEATHER_TO_FULL;
    }

    public int GOLD_TO_FULL()
    {
        return GOLD_TO_FULL;
    }

    public int IRON_TO_FULL()
    {
        return IRON_TO_FULL;
    }

    public int DIAMONDS_TO_FULL()
    {
        return DIAMONDS_TO_FULL;
    }

    public boolean allowMultipleProtectionEnchantments()
    {
        return allowMultipleProtectionEnchantments;
    }

    public boolean uninstallMode()
    {
        return uninstallMode;
    }

    public boolean checkForUpdates()
    {
        return checkForUpdates;
    }

    public boolean autoDLUpdate()
    {
        return autoDLUpdate;
    }

    public boolean noFlightDurability()
    {
        return noFlightDurability;
    }

    public List<String> allowedEnchantments()
    {
        return allowedEnchantments;
    }

    public boolean bypassWearPerm()
    {
        return bypassWearPerm;
    }

    public boolean bypassCraftPerm()
    {
        return bypassCraftPerm;
    }
}