package me.anomalousrei.musicbox;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SongEndEvent extends Event {
    private static HandlerList handlerList = new HandlerList();

    public SongEndEvent() {
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
