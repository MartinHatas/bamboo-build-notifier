package cz.hatoff.bbn.state;

import cz.hatoff.bbn.bamboo.client.BambooClient;
import cz.hatoff.bbn.bamboo.model.FavouriteBuildResponse;
import cz.hatoff.bbn.bamboo.model.Result;
import cz.hatoff.bbn.configuration.ConfigurationBean;
import cz.hatoff.bbn.configuration.xsd.BambooServersType;
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
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(configurationBean.getConfiguration().getBambooServers().size());

    private final Map<String, String> favoriteBuildStatus = new ConcurrentHashMap<String, String>();

    private MonitoredBuildsState() {
        logger.info("Creating monitored builds list.");
        scheduleCheckingOfBuilds();
    }

    private void scheduleCheckingOfBuilds() {
        logger.info("Scheduling bamboo checking state jobs.");
        for (final BambooServersType bambooServer : configurationBean.getConfiguration().getBambooServers()) {
            final Runnable checkBuildStatus = getCreateBambooCheckingJob(bambooServer);
            scheduler.scheduleAtFixedRate(checkBuildStatus, 10, 10, TimeUnit.SECONDS);
            logger.info("Scheduled checking job of '" + bambooServer.getBambooUrl() + "' done.");
        }
    }

    private Runnable getCreateBambooCheckingJob(final BambooServersType bambooServer) {
        return new Runnable() {
                    public void run() {
                        BambooClient bambooClient = null;
                        try {
                            logger.debug("Checking favourites build plans at '" + bambooServer.getBambooUrl() + "'.");
                            URL bambooUrl = new URL(bambooServer.getBambooUrl());;
                            bambooClient = new BambooClient(bambooUrl);
                            FavouriteBuildResponse favoriteBuildStatusResponse = bambooClient.getFavoriteBuildStatus();
                            updateFavoriteBuildsStatus(favoriteBuildStatusResponse);
                            logger.debug("Response from '" + bambooServer.getBambooUrl() + "'" + favoriteBuildStatusResponse);
                        } catch (Exception e) {
                            logger.error("Failed to get data from bamboo server '" + bambooServer.getBambooUrl() +"'", e);
                            if (canConnect.compareAndSet(true, false)) {
                                setChanged();
                                notifyObservers();
                            }
                        } finally {
                            if (bambooClient != null) {
                                logger.debug("Closing connection to '" + bambooServer.getBambooUrl() + "'");
                                bambooClient.closeClient();
                            }
                        }
                    }
                };
    }

    private void updateFavoriteBuildsStatus(FavouriteBuildResponse favoriteBuildStatusResponse) {
        boolean statusChanged = false;
        for (Result result : favoriteBuildStatusResponse.getResults().getResult()) {
            String key = result.getPlan().getKey();
            String newBuildState = result.getBuildState();
            if (favoriteBuildStatus.containsKey(key) && !favoriteBuildStatus.get(key).equals(newBuildState)) {
                //status changed
                favoriteBuildStatus.put(key, newBuildState);
            }
        }
        if (statusChanged || canConnect.compareAndSet(false, true)) {
            setChanged();
            notifyObservers();
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
        return BuildStatus.GREEN;
    }
}
