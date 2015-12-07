package cz.hatoff.bbn.state;

import cz.hatoff.bbn.configuration.ConfigurationBean;
import cz.hatoff.bbn.configuration.xsd.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Observable;

public class MonitoredBuildsState extends Observable {

    private static final Logger LOGGER = LogManager.getLogger(MonitoredBuildsState.class);

    private static MonitoredBuildsState instance;

    private ConfigurationBean configurationBean;

    private MonitoredBuildsState() {
        LOGGER.info("Creating monitored builds list.");
        configurationBean = ConfigurationBean.getInstance();
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
