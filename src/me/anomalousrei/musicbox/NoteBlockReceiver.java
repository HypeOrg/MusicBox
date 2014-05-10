package me.anomalousrei.musicbox;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class NoteBlockReceiver implements Receiver, Listener {
    private static final float VOLUME_RANGE = 10.0f;
    public boolean active = true;

    private final List<Player> listeners = Arrays.asList(Bukkit.getOnlinePlayers());
    private final Map<Integer, Integer> channelPatches;

    public NoteBlockReceiver(Set<Player> listeners) throws InvalidMidiDataException, IOException {
        this.channelPatches = Maps.newHashMap();
        NoteBlockPlayerMain.plugin.getServer().getPluginManager().registerEvents(this, NoteBlockPlayerMain.plugin);
    }

    @Override
    public void send(MidiMessage m, long time) {
        if (m instanceof ShortMessage) {
            ShortMessage smessage = (ShortMessage) m;
            int chan = smessage.getChannel();

            switch (smessage.getCommand()) {
                case ShortMessage.PROGRAM_CHANGE:
                    int patch = smessage.getData1();
                    channelPatches.put(chan, patch);
                    break;

                case ShortMessage.NOTE_ON:
                    this.playNote(smessage);
                    break;

                case ShortMessage.NOTE_OFF:
                    break;
            }
        }
    }

    public void playNote(ShortMessage message) {
        // if this isn't a NOTE_ON message, we can't play it
        if (ShortMessage.NOTE_ON != message.getCommand()) return;

        // get pitch and volume from the midi message
        float pitch = (float) ToneUtil.midiToPitch(message);
        float volume = VOLUME_RANGE * (message.getData2() / 127.0f);

        // get the correct instrument
        Integer patch = channelPatches.get(message.getChannel());
        Sound instrument = Sound.NOTE_PIANO;
        if (patch != null) instrument = MidiUtil.patchToInstrument(patch);

        for (Player p : Bukkit.getOnlinePlayers())
            if (active)
                p.playSound(p.getLocation(), instrument, volume, pitch);
    }

    @EventHandler
    public void end(SongEndEvent event) {
        active = false;
        close();
        SongEndEvent.getHandlerList().unregister(this);
    }

    @Override
    public void close() {
        active = false;
        listeners.clear();
        channelPatches.clear();
    }
}