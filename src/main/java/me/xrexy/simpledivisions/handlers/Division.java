package me.xrexy.simpledivisions.handlers;

import java.util.List;

public class Division {
    private final String displayName;
    private final int score;
    private final int index;
    private final int slot;
    private final List<String> rewards;
    private final String title;
    private final List<String> description;
    private final boolean show;
    private final String displayChat;

    public Division(String displayName, int score, int index, int slot, List<String> rewards, String title, List<String> description, boolean show, String displayChat) {
        this.displayName = displayName;
        this.score = score;
        this.index = index;
        this.slot = slot;
        this.rewards = rewards;
        this.title = title;
        this.description = description;
        this.show = show;
        this.displayChat = displayChat;
    }

    public int getIndex() {
        return index;
    }

    public int getSlot() {
        return slot;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isShown() {
        return show;
    }

    public String getDisplayChat() {
        return displayChat;
    }

    public int getScore() {
        return score;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "[" + index + "] " + displayName + " : " + score;
    }
}
