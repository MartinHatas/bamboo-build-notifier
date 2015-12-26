package cz.hatoff.bbn.state;

import cz.hatoff.bbn.bamboo.client.BambooClient;
import cz.hatoff.bbn.bamboo.model.FavouriteBuildResponse;
import cz.hatoff.bbn.configuration.ConfigurationBean;
import cz.hatoff.bbn.configuration.xsd.BambooServersType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MonitoredBuildsState extends Observable {

    private static final Logger logger = LogManager.getLogger(MonitoredBuildsState.class);

    private static MonitoredBuildsState instance;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ConfigurationBean configurationBean = ConfigurationBean.getInstance();

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
                            FavouriteBuildResponse favoriteBuildStatus = bambooClient.getFavoriteBuildStatus();
                            logger.debug("Response from '" + bambooServer.getBambooUrl() + "'" + favoriteBuildStatus);
                        } catch (Exception e) {
                            logger.error("Failed to get data from bamboo server '" + bambooServer.getBambooUrl() +"'", e);
                        } finally {
                            if (bambooClient != null) {
                                logger.debug("Closing connection to '" + bambooServer.getBambooUrl() + "'");
                                bambooClient.closeClient();
                            }
                        }
                    }
                };
    }


    public static MonitoredBuildsState getInstance() {
        if (instance == null) {
            instance = new MonitoredBuildsState();
        }
        return instance;
    }

    public boolean canConnect() {
        return true;
    }

    public BuildStatus getWorstBuildStatus() {
        return BuildStatus.GREEN;
    }
}
