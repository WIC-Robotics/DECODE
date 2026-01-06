package org.firstinspires.ftc.teamcode.WIC;


import android.graphics.Color;

public class PatternTracker {

    public static final int G = Color.GREEN;
    public static final int P = Color.rgb( 128, 0, 128);

    public static final int[] G1 = {G, P, P};
    public static final int[] G2 = {P, G, P};
    public static final int[] G3 = {P, P, G};

    private static PatternTracker instance;

    public static PatternTracker getInstance() {
        if (instance == null)
            instance = new PatternTracker();
        return instance;
    }

    public void setMatchPattern(int[] matchPattern) {
        this.matchPattern = matchPattern;
    }

    private int[] matchPattern = G1; //To avoid nullPointerException
    private int nextArtifactIdx = 0;

    public int advance() {
        nextArtifactIdx = ++nextArtifactIdx % 3;
        return nextArtifactColor();
    }

    public int retreat() {
        nextArtifactIdx = (--nextArtifactIdx + 3) % 3;
        return getInstance().nextArtifactColor();
    }

    public int nextArtifactColor() {
        return matchPattern[nextArtifactIdx];
    }


    private PatternTracker() {
    }
}
