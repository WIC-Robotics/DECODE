package org.firstinspires.ftc.teamcode.WIC.util;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.CameraMgr;

public class OutputHandler {

    private static OutputHandler instance = null;

    private final Telemetry telemetry;
    private final Servo rgbIndicator;
    static final double COLOR_OFF       = 0.000;
    static final double COLOR_ORANGE    = 0.333;
    static final double COLOR_YELLOW    = 0.388;
    static final double COLOR_RED       = 0.277;
    static final double COLOR_GREEN     = 0.500;
    static final double COLOR_BLUE      = 0.611;
    static final double COLOR_VIOLET      = 0.722;
    static final double COLOR_WHITE     = 1.000;

    public OutputHandler(Telemetry telemetry, HardwareMap hardwareMap) {
        instance = this;
        this.telemetry = telemetry;
        // Initialize the servo hardware device
        this.rgbIndicator = hardwareMap.get(Servo.class, "rgbIndicator");
    }

    public static OutputHandler getInstance() {
        if (instance == null)
            throw new IllegalStateException("Don't call OutputHandler.getInstance() before MidBrain construcor");
        return instance;
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

    public void clearAll() {
        telemetry.clearAll();
    }

    public void headStatus(CameraMgr.ApriTagStatus apriTagStatus){
        switch (apriTagStatus){
            case APRILTAG_OFF:
                rgbIndicator.setPosition(COLOR_OFF);
                telemetry.addData("AprilTag", "NOT STARTED");
                break;
            case APRILTAG_NOT_DETECTED:
                rgbIndicator.setPosition(COLOR_RED);
                telemetry.addData("AprilTag", "NOT DETECTED");
                break;
            case APRILTAG_DETECTED_OTHER:
                rgbIndicator.setPosition(COLOR_YELLOW);
                telemetry.addData("AprilTag", "DETECTED OTHER");
                break;
            case APRILTAG_DETECTED_TARGET:
                rgbIndicator.setPosition(COLOR_BLUE);
                telemetry.addData("AprilTag", "DETECTED TARGET");
                break;
            case APRILTAG_FIXED:
                rgbIndicator.setPosition(COLOR_GREEN);
                telemetry.addData("AprilTag", "FIXED");
                break;
            default:
                throw new IllegalArgumentException("Unknown Apriltag status: "+ apriTagStatus);
        }

    }

    public enum MSG_LEVEL {
        TEXT, WARN, ERR
    }

}
