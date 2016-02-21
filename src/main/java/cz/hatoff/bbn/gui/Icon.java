package cz.hatoff.bbn.gui;


import java.awt.*;

public enum Icon {

    SUCCESSFUL("icon-build-successful.png"),
    FAILED("icon-build-failed.png"),
    BUILDING("icon-building-06.gif"),
    CHECKING_OUT("icon-checking-out.gif"),
    QUEUED("icon-build-queued.png");

    private String fileName;

    Icon(String fileName) {
        this.fileName = fileName;
    }

    public Image getImage() {
        return Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/" + fileName));
    }
}
