package de.chefexperte.grandtheftminecraft.guns;

import java.util.ArrayList;

public class RecoilPatterns {

    public static RecoilPattern AK47 = new RecoilPattern(new ArrayList<>() {{
        add(new RecoilStep(-1f, 0.1f));
        add(new RecoilStep(-1f, 0.7f));
        add(new RecoilStep(-0.7f, -1.3f));
        add(new RecoilStep(-0.2f, -1.8f));
        add(new RecoilStep(-0.6f, 0.9f));
    }});

    public static RecoilPattern DESERT_EAGLE = new RecoilPattern(new ArrayList<>() {{
        add(new RecoilStep(-0.4f, 0.03f));
        add(new RecoilStep(-1.2f, -0.3f));
        add(new RecoilStep(-2.1f, 1.1f));
        add(new RecoilStep(-1.3f, -1.5f));
        add(new RecoilStep(-2.6f, 0.8f));
    }});

    public static RecoilPattern ROCKET_LAUNCHER = new RecoilPattern(new ArrayList<>() {{
        add(new RecoilStep(2.5f, 0));
    }});


    public static class RecoilPattern {
        public ArrayList<RecoilStep> steps;

        public RecoilPattern(ArrayList<RecoilStep> steps) {
            this.steps = steps;
        }
    }

    public record RecoilStep(float pitch, float yaw) {
    }

}
