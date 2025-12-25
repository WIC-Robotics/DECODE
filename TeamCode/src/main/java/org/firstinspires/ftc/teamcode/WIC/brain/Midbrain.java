package org.firstinspires.ftc.teamcode.WIC.brain;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.WIC.ShootingMgr;
import org.firstinspires.ftc.teamcode.WIC.util.AppContextProvider;
import org.firstinspires.ftc.teamcode.WIC.util.AprilTagDetectionListener;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.CameraMgr;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.HeadExceptionHandler;
import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;
import org.firstinspires.ftc.teamcode.WIC.util.OutputHandler;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;

import java.util.ArrayList;

/**
 * It is assumed that the distance unit used everywhere is the INCH. If you find anything else,
 * please change it and raise a flag.
 */
public class Midbrain implements AprilTagDetectionListener, HeadExceptionHandler {

    private final int desiredAprilTagID;
    private HardwareMap hardwareMap = null;
//    private Telemetry telemetry;
    private OutputHandler outputHandler;

    ShootingMgr shootingMgr = null;

    private CameraMgr cameraMgr = null;

    private long startTime = 0;

    public static final double APRIL_TAG_TO_INCENTER_DIST = 7.91; //in inches
    public static final double APRIL_TAG_TO_INCENTER_DIST_2 = APRIL_TAG_TO_INCENTER_DIST * APRIL_TAG_TO_INCENTER_DIST; //in inches

    public Midbrain(HardwareMap hardwareMap, Telemetry telemetry) {
        this.startTime = System.currentTimeMillis();

        this.hardwareMap = hardwareMap;
//        this.telemetry = telemetry;
        this.outputHandler = new OutputHandler(telemetry);

        AppContextProvider.setAppContext(this.hardwareMap.appContext);
        ConfMgr confMgr = ConfMgr.getInstance();

        try {
            this.cameraMgr = new CameraMgr(this.hardwareMap, this);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.WARN, "can't create Camera Manager");
        }
        try {
            this.shootingMgr = new ShootingMgr(this.hardwareMap, this, outputHandler);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.WARN, "can't create Shooting Manager altogether");
        }

        this.desiredAprilTagID = Integer.parseInt(confMgr.get(this, "desiredAprilTagID"));
    }

    public void startStreaming() {
        cameraMgr.startStreaming();
    }

    public void stopStreaming() {
        cameraMgr.stopStreaming();
    }

    @Override
    public void aprilTagDetectionsFound(ArrayList<AprilTagDetection> aprilTagDetections) {
        if (aprilTagDetections == null) {
            outputHandler.clearAll();
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.TEXT, "NO DETECTIONS at "+ (System.currentTimeMillis() - startTime)/1000 );
            return;
        }

//        telemetry.addLine("# AprilTags Detected " + aprilTagDetections.size() + " at " + SystemClock.currentThreadTimeMillis()/1000);
//
//        for (AprilTagDetection detection : aprilTagDetections) {
//            if (detection.metadata != null) {
//                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
//                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
//                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
//                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
//            } else {
//                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
//                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
//            }
//        }
//        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
//        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
//        telemetry.addLine("RBE = Range, Bearing & Elevation");
//
//
//        System.out.println("\n# AprilTags Detected " + aprilTagDetections.size());
//
//        for (AprilTagDetection detection : aprilTagDetections) {
//            if (detection.metadata != null) {
//                System.out.println(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
//                System.out.println(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
//                System.out.println(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
//                System.out.println(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
//            } else {
//                System.out.println(String.format("\n==== (ID %d) Unknown", detection.id));
//                System.out.println(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
//            }
//        }

        AprilTagDetection desiredAprilTag = null;

        for (AprilTagDetection detection : aprilTagDetections) {
            if (detection.id == desiredAprilTagID) {
                desiredAprilTag = detection;
                break;
            }
        }

        if (desiredAprilTag == null)
            return;

        AprilTagPoseFtc pose = desiredAprilTag.ftcPose;
        /**
         * We assume that the camera was put under the flywheel for a reason,
         * and that reason is to have the camera as close to the shooting point as possible
         */
        double yaw = pose.yaw;

//        double lensToApriltagHorizontalDist = Math.sqrt(pose.range * pose.range - pose.elevation * pose.elevation);
        double lensToApriltagHorizontalDist2 = pose.range * pose.range - pose.elevation * pose.elevation;
//        double lensToApriltagHorizontalDist = Math.sqrt(lensToApriltagHorizontalDist2);

        double distToTarget_2 = APRIL_TAG_TO_INCENTER_DIST_2 + lensToApriltagHorizontalDist2 - 2 * Math.cos(180 - yaw) * Math.sqrt(APRIL_TAG_TO_INCENTER_DIST_2 * lensToApriltagHorizontalDist2);

        double distToTarget = Math.sqrt(distToTarget_2);

        double targetTheta = Math.acos((APRIL_TAG_TO_INCENTER_DIST * Math.sin(distToTarget))/ (180 - yaw));
        System.out.println(targetTheta);

        shootingMgr.aim(distToTarget, targetTheta);
    }

    @Override
    public void cannotTurnLeft() {
        outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.TEXT, "Couldn't turn left");
        //TODO turn robot to left on its wheels
    }

    @Override
    public void cannotTurnRight() {
        outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.TEXT, "Couldn't turn right");
        //TODO turn robot to right on its wheels
    }

    @Override
    public void robotTooClose() {
        // TODO move robot further from goal (back in case you are looking forward)
    }

    @Override
    public void robotTooFar() {
        // TODO move robot close to goal (forward in case you are looking forward)
    }

//    public void gazeUpTo(double theta) {
//        shootingMgr.gazeUpTo(theta);
//    }
//
//    public void gazeUpBy(double dTheta) {
//        shootingMgr.gazeUpBy(dTheta);
//    }

//    public class ContinuousMovementThread extends Thread{
//
//        @Override
//        public void run() {
//        }
//    }
//    public ContinuousMovementThread continuousMovementThread = new ContinuousMovementThread();

}


