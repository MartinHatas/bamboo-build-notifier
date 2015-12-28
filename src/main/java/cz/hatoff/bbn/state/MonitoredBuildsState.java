package cz.hatoff.bbn.state;

import cz.hatoff.bbn.Application;
import cz.hatoff.bbn.bamboo.client.BambooClient;
import cz.hatoff.bbn.bamboo.model.BambooBuildState;
import cz.hatoff.bbn.bamboo.model.FavouriteBuildResponse;
import cz.hatoff.bbn.bamboo.model.Result;
import cz.hatoff.bbn.configuration.ConfigurationBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MonitoredBuildsState extends Observable {

    private AtomicBoolean canConnect = new AtomicBoolean(false);

    private static final Logger logger = LogManager.getLogger(MonitoredBuildsState.class);

    private static MonitoredBuildsState instance;

    private ConfigurationBean configurationBean = ConfigurationBean.getInstance();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Map<String, BambooBuildState> favoriteBuildStatus = new ConcurrentHashMap<String, BambooBuildState>();

    private MonitoredBuildsState() {
        logger.info("Creating monitored builds list.");
        scheduleCheckingOfBuilds();
    }

    private void scheduleCheckingOfBuilds() {
        logger.info("Scheduling bamboo checking state job.");
        String bambooUrl = configurationBean.getConfiguration().getBambooUrl();
        final Runnable checkBuildStatus = getCreateBambooCheckingJob(bambooUrl);
        scheduler.scheduleAtFixedRate(checkBuildStatus, 5, configurationBean.getConfiguration().getPollingInterval(), TimeUnit.SECONDS);
        logger.info("Scheduled checking job of '" + bambooUrl + "' done.");
    }

    private Runnable getCreateBambooCheckingJob(final String bambooUrlString) {
        return new Runnable() {
                    public void run() {
                        BambooClient bambooClient = null;
                        try {
                            logger.debug("Checking favourites build plans at '" + bambooUrlString + "'.");
                            URL bambooUrl = new URL(bambooUrlString);;
                            bambooClient = new BambooClient(bambooUrl);
                            FavouriteBuildResponse favoriteBuildStatusResponse = bambooClient.getFavoriteBuildStatus();
                            updateFavoriteBuildsStatus(favoriteBuildStatusResponse);
                            logger.debug("Response from '" + bambooUrl + "'" + favoriteBuildStatusResponse);
                        } catch (Exception e) {
                            logger.error("Failed to get data from bamboo server '" + bambooUrlString +"'", e);
                            if (canConnect.compareAndSet(true, false)) {
                                setChanged();
                            }
                        } finally {
                            notifyObservers();
                            if (bambooClient != null) {
                                logger.debug("Closing connection to '" + bambooUrlString + "'");
                                bambooClient.closeClient();
                            }
                        }
                    }
                };
    }

    private void updateFavoriteBuildsStatus(FavouriteBuildResponse favoriteBuildStatusResponse) {
        for (Result result : favoriteBuildStatusResponse.getResults().getResult()) {
            String key = result.getPlan().getKey();
            BambooBuildState newBuildState = result.getBuildState();
            BambooBuildState oldBuildState = favoriteBuildStatus.get(key);
            if (favoriteBuildStatus.containsKey(key) && oldBuildState != newBuildState) {
                if (newBuildState == BambooBuildState.FAILED || newBuildState == BambooBuildState.UNKNOWN) {
                    String message = "Build '" + result.getPlan().getShortName() + "' changed changed status from '" + oldBuildState.getStateName() + "' to '" + newBuildState.getStateName() + "'.";
                    logger.warn(message);
                    Application.showBubbleNotificationWarn(message);
                }
                if (newBuildState == BambooBuildState.SUCCESSFUL) {
                    String message = "Build '" + result.getPlan().getShortName() + "' changed changed status from '" + oldBuildState.getStateName() + "' to '" + newBuildState.getStateName() + "'.";
                    logger.info(message);
                    Application.showBubbleNotificationInfo(message);
                }
            }
            favoriteBuildStatus.put(key, newBuildState);
        }
        setChanged();
        if (canConnect.compareAndSet(false, true)) {
            logger.info("Connection with bamboo server has been established.");
            setChanged();
        }
    }

    public static MonitoredBuildsState getInstance() {
        if (instance == null) {
            instance = new MonitoredBuildsState();
        }
        return instance;
    }

    public boolean canConnect() {
        return canConnect.get();
    }

    public BuildStatus getWorstBuildStatus() {
        BuildStatus worstBuildStatus = BuildStatus.GRAY;
        for (String key : favoriteBuildStatus.keySet()) {
            BambooBuildState bambooBuildState = favoriteBuildStatus.get(key);
            if (worstBuildStatus == BuildStatus.GRAY || worstBuildStatus.isBetterThan(bambooBuildState.getStatus())) {
                worstBuildStatus = bambooBuildState.getStatus();
            }
        }
        return worstBuildStatus;
    }
}
