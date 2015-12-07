package cz.hatoff.bbn.state;

import java.awt.*;

public enum BuildStatus {

    GREEN("green.png", 3),
    YELLOW("yellow.png", 2),
    RED("red.png", 1),
    GRAY("gray.png", 0);

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
}
