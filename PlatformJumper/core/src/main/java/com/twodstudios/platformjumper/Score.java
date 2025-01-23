package com.twodstudios.platformjumper;

public class Score {
    public String name;
    public int score;

    public Score() {
    }

    public Score(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Score: " + score;
    }
}
