package me.xrexy.simpledivisions.utils.mysql;

public class DivPlayerDummy {
    private final String username;
    private final int score;
    private final int index;

    public DivPlayerDummy(String username, int score, int index) {
        this.username = username;
        this.score = score;
        this.index = index;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "DivPlayerDummy{" +
                "username='" + username + '\'' +
                ", score=" + score +
                ", index=" + index +
                '}';
    }
}

