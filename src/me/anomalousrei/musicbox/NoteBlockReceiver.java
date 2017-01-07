package me.anomalousrei.musicbox;

import com.google.common.collect.Maps;
import org.bukkit.*;
import org.bukkit.entity.Player;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.io.IOException;
import java.util.Map;
import java.util.Random;


public class NoteBlockReceiver implements Receiver {
    private static final float VOLUME_RANGE = 4.0f;
    private final World w = Bukkit.getWorld("world");
    private final Random rng = new Random();
    public boolean active = true;

    private final Map<Integer, Integer> channelPatches;

    public NoteBlockReceiver() throws InvalidMidiDataException, IOException {
        this.channelPatches = Maps.newHashMap();
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
        Sound instrument = Sound.BLOCK_NOTE_PLING;
        if (patch != null) instrument = MidiUtil.patchToInstrument(patch);

        for (Player p : w.getEntitiesByClass(Player.class))
            if (!Commands.muted.contains(p.getUniqueId())) {
                p.playSound(new Location(w, 57.5, 113.5, 1333.5), instrument, volume, pitch);
            }
        if (instrument == Sound.BLOCK_NOTE_BASS || instrument == Sound.BLOCK_NOTE_BASEDRUM)
            w.spawnParticle(Particle.HEART, new Location(w, 57.5 + (rng.nextBoolean() ? Math.random() / 2 : -(Math.random() / 2)), 114.5 + Math.random(), 1333.5 + (rng.nextBoolean() ? Math.random() / 2 : -(Math.random() / 2))), 1);
        else
            w.spawnParticle(Particle.CRIT, new Location(w, 57.5 + (rng.nextBoolean() ? Math.random() / 2 : -(Math.random() / 2)), 114.5 + Math.random(), 1333.5 + (rng.nextBoolean() ? Math.random() / 2 : -(Math.random() / 2))), 1);
    }

    @Override
    public void close() {
        active = false;
        channelPatches.clear();
    }
}