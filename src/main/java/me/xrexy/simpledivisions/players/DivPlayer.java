package me.xrexy.simpledivisions.players;

import lombok.Data;

import java.util.ArrayList;

@Data
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

    public void addScore(int toAdd) {
        score += toAdd;
    }

    public void removeScore(int toRemove) {
        if (score >= toRemove) {
            score -= toRemove;
            return;
        }
        score = 0; // score is less than toRemove -> would be negative otherwise
    }
}
