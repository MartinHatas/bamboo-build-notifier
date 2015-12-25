package cz.hatoff.bbn;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.menu.WebPopupMenu;
import cz.hatoff.bbn.gui.SelfClosingPopupMenu;
import cz.hatoff.bbn.state.BuildStatus;
import cz.hatoff.bbn.state.MonitoredBuildsState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

public class Application implements Observer {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    private TrayIcon trayIcon;
    private WebPopupMenu jpopup = new SelfClosingPopupMenu();

    private BuildStatus status = BuildStatus.GRAY;
    private MonitoredBuildsState monitoredBuildsState;

    public static void main(String[] args) {
        LOGGER.info("Starting Bamboo build notifier.");
        new Application().start();
    }

    private void start() {
        initLookAndFeel();
        initTray();
        initObserver();
    }

    private void initObserver() {
        LOGGER.info("Initializing observer of build status.");
        MonitoredBuildsState.getInstance().addObserver(this);
    }

    private void initLookAndFeel() {
        LOGGER.info("Initializing look and feel.");
        WebLookAndFeel.install();
        try {
            UIManager.setLookAndFeel(new WebLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            LOGGER.error("Failed to load look and feel.", e);
            System.exit(1);
        }
    }

    private void initTray() {
        LOGGER.info("Initializing tray icon.");
        if (SystemTray.isSupported()) {
            final SystemTray tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(BuildStatus.GRAY.getImage(), "Bamboo build notifier", null);
            createTrayIconListener(trayIcon);
            addTrayIconIntoTray(tray);
        } else {
            LOGGER.error("Tray is not available in the system. Shutting down application.");
            System.exit(1);
        }
    }

    private void addTrayIconIntoTray(SystemTray tray) {
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            LOGGER.error("Failed to initialize tray icon.", e);
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

    public void update(Observable observable, Object arg) {
        if (observable instanceof MonitoredBuildsState) {
            LOGGER.info("Monitored build state changed.");
            if (!monitoredBuildsState.canConnect()) {
                trayIcon.setImage(BuildStatus.GRAY.getImage());
                return;
            }
            trayIcon.setImage(monitoredBuildsState.getWorstBuildStatus().getImage());
        }
    }
}
