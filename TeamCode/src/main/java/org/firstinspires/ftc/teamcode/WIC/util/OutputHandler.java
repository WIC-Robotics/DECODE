package org.firstinspires.ftc.teamcode.WIC.util;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class OutputHandler {

    private Telemetry telemetry;

    public OutputHandler(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public void writeToTelemetry(MSG_LEVEL msgLevel, String message) {
        if (msgLevel == MSG_LEVEL.TEXT) {
            write(message);
        } else if (msgLevel == MSG_LEVEL.WARN) {
            warn(message);
        } else if (msgLevel == MSG_LEVEL.ERR) {
            error(message);
        } else {
            throw new RuntimeException(message);
        }
    }

    private void write(String message) {
        telemetry.addLine(message);
    }

    private void warn(String message) {
        telemetry.addLine("WARNING: "+ message);
    }
    private void error(String message) {
        telemetry.addLine("ERROR: "+ message);
    }

    public enum MSG_LEVEL {
        TEXT, WARN, ERR
    }

}
