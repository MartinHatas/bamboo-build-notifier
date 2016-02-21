package cz.hatoff.bbn.bamboo.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LifeCycleState {

    FINISHED("Finished"),
    IN_PROGRESS("InProgress"),
    NOT_BUILT("NotBuild"),
    PENDING("Pending"),
    QUEUED("Queued"),
    UNKNOWN("Unknown");

    private String name;

    LifeCycleState(String name) {
        this.name = name;
    }

    @JsonCreator
    public LifeCycleState fromString(String name) {
        for (LifeCycleState lifeCycleState : LifeCycleState.values()) {
            if (lifeCycleState.name.equals(name)) {
                return lifeCycleState;
            }
        }
        return LifeCycleState.UNKNOWN;
    }

    @JsonValue
    public String getStateName() {
        return name;
    }
}
