package cz.hatoff.bbn.state;

import java.awt.*;

public enum BuildStatus {

    GRAY("gray.png", 4),
    GREEN("green.png", 3),
    YELLOW("yellow.png", 2),
    RED("red.png", 1);

    private String imageName;
    private int value;

    BuildStatus(String imageName, int value) {
        this.imageName = imageName;
        this.value = value;
    }

    public Image getImage() {
        return Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/" + imageName));
    }

    public int getValue() {
        return value;
    }

    public boolean isBetterThan(BuildStatus status) {
        return value > status.value;
    }
}
