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

    private static final Logger logger = LogManager.getLogger(Application.class);

    private static TrayIcon trayIcon;
    private WebPopupMenu jpopup = new SelfClosingPopupMenu();

    private volatile BuildStatus status = BuildStatus.GRAY;
    private MonitoredBuildsState monitoredBuildsState = MonitoredBuildsState.getInstance();

    public static void main(String[] args) {
        logger.info("Starting Bamboo build notifier.");
        new Application().start();
    }

    private void start() {
        initLookAndFeel();
        initTray();
        initObserver();
    }

    private void initObserver() {
        logger.info("Initializing observer of build status.");
        MonitoredBuildsState.getInstance().addObserver(this);
    }

    private void initLookAndFeel() {
        logger.info("Initializing look and feel.");
        WebLookAndFeel.install();
        try {
            UIManager.setLookAndFeel(new WebLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            logger.error("Failed to load look and feel.", e);
            System.exit(1);
        }
    }

    private void initTray() {
        logger.info("Initializing tray icon.");
        if (SystemTray.isSupported()) {
            final SystemTray tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(BuildStatus.GRAY.getImage(), "Bamboo build notifier", null);
            createTrayIconListener(trayIcon);
            addTrayIconIntoTray(tray);
        } else {
            logger.error("Tray is not available in the system. Shutting down application.");
            System.exit(1);
        }
    }

    private void addTrayIconIntoTray(SystemTray tray) {
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            logger.error("Failed to initialize tray icon.", e);
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
            logger.info("Monitored build state changed.");
            if (!monitoredBuildsState.canConnect()) {
                status = BuildStatus.GRAY;
                trayIcon.setImage(BuildStatus.GRAY.getImage());
                trayIcon.displayMessage("Bamboo build notifier", "Connection with bamboo server has been lost.", TrayIcon.MessageType.ERROR);
                return;
            } else {
                if (status == BuildStatus.GRAY) {
                    trayIcon.displayMessage("Bamboo build notifier", "Connection with bamboo server was established.", TrayIcon.MessageType.INFO);
                }

                BuildStatus worstBuildStatus = monitoredBuildsState.getWorstBuildStatus();
                if (worstBuildStatus != status) {
                    status = worstBuildStatus;
                    trayIcon.setImage(worstBuildStatus.getImage());
                }
            }
        }
    }

    public static void showBubbleNotificationInfo( String text) {
        if (trayIcon != null) {
            trayIcon.displayMessage("Bamboo build notifier", text, TrayIcon.MessageType.INFO);
        }
    }

    public static void showBubbleNotificationWarn(String text) {
        if (trayIcon != null) {
            trayIcon.displayMessage("Bamboo build notifier", text, TrayIcon.MessageType.WARNING);
        }
    }
}
