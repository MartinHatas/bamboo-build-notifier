package cz.hatoff.bbn.bamboo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BambooBuildState {

    FAILED("Failed", 0),
    UNKNOWN("Unknown", -1);

    private final String stateName;
    private final int code;

    BambooBuildState(String stateName, int code) {
        this.stateName = stateName;
        this.code = code;
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

    public int getCode() {
        return code;
    }
}
