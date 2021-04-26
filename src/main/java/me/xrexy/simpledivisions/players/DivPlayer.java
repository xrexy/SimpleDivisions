package me.xrexy.simpledivisions.players;

import java.util.ArrayList;

public class DivPlayer implements java.io.Serializable {
    private int divisionIndex;
    private final String username;
    private int score;
    private final String uuid;
    private int maxDivision;
    private ArrayList<Integer> claimed;

    public DivPlayer(int divisionIndex, String username, int score, String uuid, int maxDivision, ArrayList<Integer> claimed) {
        this.divisionIndex = divisionIndex;
        this.username = username;
        this.score = score;
        this.uuid = uuid;
        this.maxDivision = maxDivision;
        this.claimed = claimed;
    }

    public int getMaxDivision() {
        return maxDivision;
    }

    public void setClaimed(ArrayList<Integer> claimed) {
        this.claimed = claimed;
    }

    public void setMaxDivision(int maxDivision) {
        this.maxDivision = maxDivision;
    }

    public ArrayList<Integer> getClaimed() {
        return claimed;
    }

    public void setDivisionIndex(int divisionIndex) {
        this.divisionIndex = divisionIndex;
    }

    public int getDivisionIndex() {
        return divisionIndex;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int toAdd) {
        score += toAdd;
    }

    public void removeScore(int toRemove) {
        if (score >= toRemove) {
            score -= toRemove;
            return;
        }
        score = 0; // score is less than toRemove -> would be negative
    }

    public String getUsername() {
        return username;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUuid() {
        return uuid;
    }
}
