package org.firstinspires.ftc.teamcode.WIC.util;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class OutputMgr {

    private Telemetry telemetry;

    public OutputMgr(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public void writeToTelemetry(MSG_LEVEL msgLevel, String message) {
        if (msgLevel == MSG_LEVEL.TEXT) {
            write(message);
        } else if (msgLevel == MSG_LEVEL.WARN) {
            warn(message);
        } else {
            throw new RuntimeException(message);
        }
    }

    private void write(String message) {

    }

    private void warn(String message) {}

    public enum MSG_LEVEL {
        TEXT, WARN, ERR
    }

}
