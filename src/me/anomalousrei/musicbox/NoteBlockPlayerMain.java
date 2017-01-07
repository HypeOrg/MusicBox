package me.anomalousrei.musicbox;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class NoteBlockPlayerMain extends JavaPlugin {
    public static NoteBlockPlayerMain plugin;
    public static BukkitTask playing;
    public static String[] songs;
    public static Integer[] length;
    private static Sequencer currentS;
    public static int current = -1;

    public void onEnable() {
        songs = new String[]{"HOUSE.MID", "overworld-2.mid", "pirates.mid", "to_town.mid", "rudolph.mid", "deckthe.mid", "s-night.mid", "rock_ar.mid"};
        length = new Integer[]{290, 95, 78, 111, 138, 87, 105, 120};
        //songs = new String[]{"to_town.mid", "rudolph.mid", "deckthe.mid", "s-night.mid", "rock_ar.mid"};
        //length = new Integer[]{111, 138, 87, 105, 120};
        plugin = this;
        registerCommands();
        try {
            onTask();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onTask() throws MidiUnavailableException, InvalidMidiDataException, IOException {
        if (currentS != null)
            currentS.stop();
        currentS = null;
        if (playing != null)
            playing.cancel();
        playing = null;
        int prev = current;
        current = -1;
        while (current == -1) {
            Random rng = new Random();
            int gen = rng.nextInt(songs.length);
            if (gen == prev) continue;
            current = gen;
        }
        currentS = MidiUtil.playMidi(new File(getDataFolder() + "/" + songs[current]), 1F);
        playing = Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                try {
                    onTask();
                } catch (MidiUnavailableException e) {
                    e.printStackTrace();
                } catch (InvalidMidiDataException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, (length[current] * 20) + 40);
    }

    public void onDisable() {
        if (currentS != null)
            currentS.stop();
        currentS = null;
        if (playing != null)
            playing.cancel();
        Bukkit.getScheduler().cancelTasks(this);
    }

    /**
     * *******************************************************************
     * Code to use for sk89q's command framework goes below this comment! *
     * ********************************************************************
     */

    private CommandsManager<CommandSender> commands;

    private void registerCommands() {
        // Register the commands that we want to use
        commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender player, String perm) {
                return plugin.hasPermission(player, perm);
            }
        };
        commands.setInjector(new SimpleInjector(this));
        final CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, commands);

        cmdRegister.register(Commands.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "You need to enter a number!");
            } else {
                sender.sendMessage(ChatColor.RED + "Error occurred, contact developer.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    public boolean hasPermission(CommandSender sender, String perm) {
        if (!(sender instanceof Player)) {
            if (sender.hasPermission(perm)) {
                return ((sender.isOp() && (sender instanceof ConsoleCommandSender)));
            }
        }
        return hasPermission(sender, ((Player) sender).getWorld(), perm);
    }

    public boolean hasPermission(CommandSender sender, World world, String perm) {
        if ((sender.isOp()) || sender instanceof ConsoleCommandSender || sender.hasPermission(perm)) {
            return true;
        }

        return false;
    }
}