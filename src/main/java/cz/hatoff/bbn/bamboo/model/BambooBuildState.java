package cz.hatoff.bbn.bamboo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import cz.hatoff.bbn.state.BuildStatus;

public enum BambooBuildState {

    SUCCESSFUL("Successful", BuildStatus.GREEN),
    FAILED("Failed", BuildStatus.RED),
    UNKNOWN("Unknown", BuildStatus.RED);

    private final String stateName;
    private final BuildStatus status;

    BambooBuildState(String stateName, BuildStatus status) {
        this.stateName = stateName;
        this.status = status;
    }

    @JsonCreator
    public BambooBuildState fromString(String stateName) {
        for (BambooBuildState buildState : BambooBuildState.values()) {
            if (buildState.stateName.equals(stateName)) {
                return buildState;
            }
        }
        return BambooBuildState.UNKNOWN;
    }

    @JsonValue
    public String getStateName() {
        return stateName;
    }

    public BuildStatus getStatus() {
        return status;
    }

}
