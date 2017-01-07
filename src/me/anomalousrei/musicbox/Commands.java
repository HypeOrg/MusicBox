package me.anomalousrei.musicbox;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class Commands {

    NoteBlockPlayerMain plugin;

    public Commands(NoteBlockPlayerMain pl) {
        plugin = pl;
    }

    public static ArrayList<UUID> muted = new ArrayList<UUID>();

    @Command(aliases = {"nosong"},
            desc = "Mute song")
    public void mute(CommandContext args, CommandSender sender) {
        if (muted.contains(((Player) sender).getUniqueId()))
            muted.remove(((Player) sender).getUniqueId());
        else muted.add(((Player) sender).getUniqueId());
    }

    @Command(aliases = {"skipsong"},
            desc = "Skip song")
    @CommandPermissions("skytopia.admin")
    public void skip(CommandContext args, CommandSender sender) {
        try {
            plugin.onTask();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
