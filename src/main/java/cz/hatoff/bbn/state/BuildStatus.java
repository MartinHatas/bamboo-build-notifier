package cz.hatoff.bbn.state;

import java.awt.*;

public enum BuildStatus {

    GREEN("green.png"),
    YELLOW("yellow.png"),
    RED("red.png"),
    GRAY("gray.png");

    String imageName;

    BuildStatus(String imageName) {
        this.imageName = imageName;
    }

    public Image getImage() {
        return Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/" + imageName));
    }
}
