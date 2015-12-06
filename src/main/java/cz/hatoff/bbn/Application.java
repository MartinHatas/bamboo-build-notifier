package cz.hatoff.bbn;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.menu.WebPopupMenu;
import cz.hatoff.bbn.gui.SelfClosingPopupMenu;
import cz.hatoff.bbn.state.BuildStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Application {

    private TrayIcon trayIcon;
    private WebPopupMenu jpopup = new SelfClosingPopupMenu();

    public static void main(String[] args) {
        new Application().start();
    }

    private void start() {
        initLookAndFeel();
        initTray();
    }

    private void initLookAndFeel() {
        WebLookAndFeel.install();
        try {
            UIManager.setLookAndFeel(new WebLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void initTray() {
        if (SystemTray.isSupported()) {

            final SystemTray tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(BuildStatus.GRAY.getImage(), "Bamboo build notifier", null);

            createTrayIconListener(trayIcon);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("Can't add to tray");
            }
        } else {
            System.err.println("Tray is not available in the system.");
            System.exit(1);
        }
    }

    private void createTrayIconListener(TrayIcon trayIcon) {
        trayIcon.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    jpopup.setLocation(e.getXOnScreen() + 10, e.getYOnScreen() - 170);
                    jpopup.setInvoker(jpopup);
                    jpopup.setVisible(true);
                } else {
                    jpopup.setInvoker(null);
                    jpopup.setVisible(false);
                }
            }
        });
    }
}
