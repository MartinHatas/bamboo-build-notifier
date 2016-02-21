package cz.hatoff.bbn;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.managers.style.skin.web.PopupStyle;
import cz.hatoff.bbn.bamboo.model.BambooBuildState;
import cz.hatoff.bbn.bamboo.model.Result;
import cz.hatoff.bbn.gui.*;
import cz.hatoff.bbn.gui.Icon;
import cz.hatoff.bbn.state.BuildStatus;
import cz.hatoff.bbn.state.MonitoredBuildsState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

    private void createTrayIconListener(final TrayIcon trayIcon) {
        trayIcon.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                jpopup.removeAll();
                if (e.isPopupTrigger()) {

                    createBuildPlanMenuItems();
                    createExitMenuItem();

                    jpopup.setLocation(e.getXOnScreen() - 30, e.getYOnScreen() - 65);
                    jpopup.setInvoker(jpopup);
                    jpopup.setVisible(true);
                } else {
                    jpopup.setInvoker(null);
                    jpopup.setVisible(false);
                }
            }
        });

    }

    private void createBuildPlanMenuItems() {
        for (String key : monitoredBuildsState.getFavoriteBuildStatus().keySet()) {
            final Result result = monitoredBuildsState.getFavoriteBuildStatus().get(key);
            Icon icon = resolveIcon(result);
            ImageIcon stateIcon = new ImageIcon(icon.getImage());
            WebMenuItem buildItem = new WebMenuItem(result.getPlan().getShortName(), stateIcon);
            buildItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI(result.getLink().getHref()));
                    } catch (Exception e1) {
                        logger.error("Failed to create uri from link " + result.getLink().getHref());
                    }
                }
            });
            jpopup.add(buildItem);
        }
    }

    private Icon resolveIcon(Result result) {
        switch (result.getLifeCycleState()) {
            case FINISHED: {
                switch (result.getBuildState()) {
                    case SUCCESSFUL:
                        return Icon.SUCCESSFUL;
                    case FAILED:
                        return Icon.FAILED;
                }
                break;
            }
            case IN_PROGRESS:
                return Icon.BUILDING;
            case QUEUED:
                return Icon.QUEUED;
            case PENDING:
                return Icon.CHECKING_OUT;
            case NOT_BUILT:
                return Icon.FAILED;

        }

        return Icon.FAILED;
    }

    private void createExitMenuItem() {
        ImageIcon exitIcon = new ImageIcon(this.getClass().getClassLoader().getResource("images/exit.png"));
        WebMenuItem exitMI = new WebMenuItem("Exit", exitIcon);
        exitMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.info("Exiting application.");
                System.exit(0);
            }
        });
        jpopup.add(exitMI);
    }

    public void update(Observable observable, Object arg) {
        if (observable instanceof MonitoredBuildsState) {
            if (!monitoredBuildsState.canConnect()) {
                logger.warn("Lost connection with bamboo server.");
                status = BuildStatus.GRAY;
                trayIcon.setImage(BuildStatus.GRAY.getImage());
                trayIcon.displayMessage("Bamboo build notifier", "Connection with bamboo server has been lost.", TrayIcon.MessageType.ERROR);
                return;
            } else {
                if (status == BuildStatus.GRAY) {
                    logger.info("Connection with bamboo server established.");
                    trayIcon.displayMessage("Bamboo build notifier", "Connection with bamboo server was established.", TrayIcon.MessageType.INFO);
                }

                BuildStatus worstBuildStatus = monitoredBuildsState.getWorstBuildStatus();
                if (worstBuildStatus != status) {
                    logger.info("Changing tray icon status to '" + worstBuildStatus.name() + "'");
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
