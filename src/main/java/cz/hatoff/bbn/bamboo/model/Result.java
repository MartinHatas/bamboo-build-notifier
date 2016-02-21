package cz.hatoff.bbn.bamboo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    @JsonProperty("buildNumber")
    private int buildNumber;

    @JsonProperty("state")
    private BambooBuildState state;

    @JsonProperty("buildState")
    private BambooBuildState buildState;

    @JsonProperty("lifeCycleState")
    private LifeCycleState lifeCycleState;

    @JsonProperty("plan")
    private Plan plan;

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    public BambooBuildState getState() {
        return state;
    }

    public void setState(BambooBuildState state) {
        this.state = state;
    }

    public BambooBuildState getBuildState() {
        return buildState;
    }

    public void setBuildState(BambooBuildState buildState) {
        this.buildState = buildState;
    }

    public LifeCycleState getLifeCycleState() {
        return lifeCycleState;
    }

    public void setLifeCycleState(LifeCycleState lifeCycleState) {
        this.lifeCycleState = lifeCycleState;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    @Override
    public String toString() {
        return "Result{" +
                "buildNumber=" + buildNumber +
                ", state='" + state + '\'' +
                ", buildState='" + buildState + '\'' +
                ", lifeCycleState='" + lifeCycleState + '\'' +
                ", plan=" + plan +
                '}';
    }
}

