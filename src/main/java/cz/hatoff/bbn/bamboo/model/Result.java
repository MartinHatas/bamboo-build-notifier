package cz.hatoff.bbn.bamboo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    @JsonProperty("buildNumber")
    private int buildNumber;

    @JsonProperty("status")
    private String status;

    @JsonProperty("buildStatus")
    private String buildStatus;

    @JsonProperty("lifeCycleState")
    private String lifeCycleState;

    @JsonProperty("plan")
    private Plan plan;

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(String buildStatus) {
        this.buildStatus = buildStatus;
    }

    public String getLifeCycleState() {
        return lifeCycleState;
    }

    public void setLifeCycleState(String lifeCycleState) {
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
                ", status='" + status + '\'' +
                ", buildStatus='" + buildStatus + '\'' +
                ", lifeCycleState='" + lifeCycleState + '\'' +
                ", plan=" + plan +
                '}';
    }
}

