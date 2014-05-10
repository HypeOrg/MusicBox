package me.anomalousrei.musicbox;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.io.File;
import java.io.IOException;

public class Commands {

    NoteBlockPlayerMain plugin;

    public Commands(NoteBlockPlayerMain pl) {
        plugin = pl;
    }

    @Command(aliases = {"play", "song", "mbplay"},
            desc = "Play a song",
            usage = "<song> <tempo>",
            max = 2,
            min = 2)
    @CommandPermissions("musicbox.admin")
    public void play(CommandContext args, CommandSender sender) {
        try {
            MidiUtil.playMidi(new File(NoteBlockPlayerMain.plugin.getDataFolder(), args.getString(0)), Float.parseFloat(args.getString(1)), null);
            Bukkit.broadcastMessage(ChatColor.GOLD + "[Jukebox] " + ChatColor.YELLOW + sender.getName() + " put on " + args.getString(0));
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Could not play midi.");
            sender.sendMessage(ChatColor.RED + "Error: " + e.getMessage());
        }
    }

    @Command(aliases = {"mbstop", "stopsong", "stp"},
            desc = "Stop a song")
    @CommandPermissions("musicbox.admin")
    public void stop(CommandContext args, CommandSender sender) {
        Bukkit.broadcastMessage(ChatColor.GOLD + "[Jukebox] " + ChatColor.YELLOW + sender.getName() + " stopped all playing songs!");
        NoteBlockPlayerMain.plugin.getServer().getPluginManager().callEvent(new SongEndEvent());
    }
}
