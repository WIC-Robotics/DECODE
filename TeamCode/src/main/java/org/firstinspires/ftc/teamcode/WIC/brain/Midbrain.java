package org.firstinspires.ftc.teamcode.WIC.brain;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.WIC.PatternTracker;
import org.firstinspires.ftc.teamcode.WIC.ShootingMgr;
import org.firstinspires.ftc.teamcode.WIC.peripherals.movement.MovementMgr;
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

    private int desiredAprilTagID;
    private final boolean throwErrors;
    private HardwareMap hardwareMap;
    //    private Telemetry telemetry;
    private OutputHandler outputHandler;

    ShootingMgr shootingMgr;

    private CameraMgr cameraMgr = null;

    private long startTime = 0;
    private double lastTargetAngleAtlas = 0;

    public static final double APRIL_TAG_TO_INCENTER_DIST = 7.91; //in inches
    public static final double APRIL_TAG_TO_INCENTER_DIST_2 = APRIL_TAG_TO_INCENTER_DIST * APRIL_TAG_TO_INCENTER_DIST; //in inches
    PatternTracker patternTracker = PatternTracker.getInstance();
    private boolean calibratingAxis = false;
    private MovementMgr movementMgr;
    private String alliance;
    private boolean autoAim;

    public Midbrain(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        AppContextProvider.setAppContext(this.hardwareMap.appContext);

        this.startTime = System.currentTimeMillis();
        //keep the output handler at the top of constructor
//        this.telemetry = telemetry;
        ConfMgr confMgr = ConfMgr.getInstance();

        this.throwErrors = confMgr.getBoolean(this, "throwErrors");

        try {
            this.outputHandler = new OutputHandler(telemetry, hardwareMap);
            outputHandler.headStatus(CameraMgr.AprilTagStatus.APRILTAG_OFF);
            this.cameraMgr = new CameraMgr(this.hardwareMap, this);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.WARN, "can't create Camera Manager");
            if (throwErrors) {
                throw e;
            }
        }
        try {
            this.shootingMgr = new ShootingMgr(this.hardwareMap, this, outputHandler, throwErrors);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.WARN, "can't create Shooting Manager altogether");
            if (throwErrors) {
                throw e;
            }
        }
        try {
            this.movementMgr = new MovementMgr(this.hardwareMap);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.WARN, "can't create Movement Manager");
            if (throwErrors) {
                throw e;
            }
        }

        if ("RED".equals(alliance)) {
            this.desiredAprilTagID = 24;
        }
        if ("BLUE".equals(alliance)) {
            this.desiredAprilTagID = 20;
        } else {
            this.desiredAprilTagID = Integer.parseInt(confMgr.get(this, "desiredAprilTagID"));
        }
    }

    public void startStreaming() {
        if (cameraMgr != null) {
            cameraMgr.startStreaming();
        }
    }

    public void stopStreaming() {
        if (cameraMgr != null) {
            cameraMgr.stop();
        }
    }

    @Override
    public void aprilTagDetectionsFound(ArrayList<AprilTagDetection> aprilTagDetections) {

        if (aprilTagDetections == null) {
            outputHandler.headStatus(CameraMgr.AprilTagStatus.APRILTAG_NOT_DETECTED);
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
        CameraMgr.AprilTagStatus highestApriTagStatus = aprilTagDetections.isEmpty() ?
                CameraMgr.AprilTagStatus.APRILTAG_NOT_DETECTED:
                CameraMgr.AprilTagStatus.APRILTAG_DETECTED_OTHER;

        for (AprilTagDetection detection : aprilTagDetections) {
            int detectionId = detection.id;
            switch (detectionId){
                case 20: //blue
                case 24: //red
                    if (detectionId == desiredAprilTagID) {
                        desiredAprilTag = detection;
                        highestApriTagStatus = CameraMgr.AprilTagStatus.APRILTAG_DETECTED_TARGET;
                        break;
                    }
                    break;
                case 21:
                    patternTracker.setMatchPattern(PatternTracker.G1);
                    break;
                case 22:
                    patternTracker.setMatchPattern(PatternTracker.G2);
                    break;
                case 23:
                    patternTracker.setMatchPattern(PatternTracker.G3);
                    break;
            }
        }
        OutputHandler.getInstance().headStatus(highestApriTagStatus);

        //TODO use any detected Apriltags to localize yourself.
        // You may cache their location and values, but not react to them.

        if (desiredAprilTag == null)
            return;

        AprilTagPoseFtc pose = desiredAprilTag.ftcPose;
        /**
         * We assume that the camera was put under the flywheel for a reason,
         * and that reason is to have the camera as close to the shooting point as possible
         */
        double yaw = pose.yaw;
        double range = pose.range;
        double bearing = pose.bearing;
        double elevation = pose.elevation;
//        System.out.println("yaw = " + Math.toDegrees(yaw));
//        System.out.println("range = " + range);
//        System.out.println("bearing = " + bearing);
//        System.out.println("elevation = " + elevation);

//        yaw = Math.toRadians(yaw); //It is now in radians

//        double lensToApriltagHorizontalDist = Math.sqrt(pose.range * pose.range - pose.elevation * pose.elevation);
        double lensToApriltagHorizontalDist2 = range * range - elevation * elevation;
//        double lensToApriltagHorizontalDist = Math.sqrt(lensToApriltagHorizontalDist2);
//        System.out.println("lensToApriltagHorizontalDist2 = " + Math.sqrt(lensToApriltagHorizontalDist2));


        if (calibratingAxis) {
            return;
        }
        double internal_angle_c = Math.PI - yaw;
//        System.out.println("internal_angle_c = " + Math.toDegrees(internal_angle_c));

        double distToTarget_2 = APRIL_TAG_TO_INCENTER_DIST_2 + lensToApriltagHorizontalDist2 - 2 *
                Math.cos(internal_angle_c) * Math.sqrt(APRIL_TAG_TO_INCENTER_DIST_2 * lensToApriltagHorizontalDist2);

        double distToTarget = Math.sqrt(distToTarget_2);
//        System.out.println("distToTarget = " + distToTarget);

        double angleA = Math.asin((APRIL_TAG_TO_INCENTER_DIST * Math.sin(internal_angle_c)) / distToTarget);
        double relativeHeadOrientation = shootingMgr.getCurrentHeadPositionRad();
        double targetTheta = angleA + bearing + relativeHeadOrientation;


//        System.out.println("targetTheta = " + Math.toDegrees(targetTheta));

//        System.out.println("targetThetaAxis  = " + targetTheta);

//        boolean fifthSecond = (System.currentTimeMillis() - startTime) % 5000 == 0;
        if (ConfMgr.isDemo()){
            long newAngle = (((System.currentTimeMillis() - startTime)/10000) % 4) *10;
            if(newAngle != lastTargetAngleAtlas) {
                shootingMgr.aim(newAngle, targetTheta);
                lastTargetAngleAtlas = newAngle;
            }
        } else {
            System.out.println("targetTheta B4 aim=\t" + Math.toDegrees(targetTheta));
            System.out.printf("AprilTag yaw= %f\tbearing= %f\tangleA= %f\ttargetTheta= %f\n",
                    Math.toDegrees(yaw), Math.toDegrees(bearing),Math.toDegrees(angleA) ,Math.toDegrees(targetTheta));
//            shootingMgr.aim(distToTarget, targetTheta);
            shootingMgr.aim(-1, targetTheta); //temp
        }
    }

    @Override
    public void cannotTurnLeft() {
        if (calibratingAxis) {
            shootingMgr.aim(-1, -100);
            return;
        }
        outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.TEXT, "Couldn't turn left");
        // turn robot to left on its wheels
//        movementMgr.drive(0.,0., 0.1);
    }

    @Override
    public void cannotTurnRight() {
        if (calibratingAxis) {
            shootingMgr.aim(-1, 0);
            calibratingAxis = false;
            return;
        }
        outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.TEXT, "Couldn't turn right");
        // turn robot to right on its wheels
//        movementMgr.drive(0.,0., -0.1);
    }

    @Override
    public void robotTooClose() {
        // TODO move robot further from goal (back in case you are looking forward)
    }

    @Override
    public void robotTooFar() {
        // TODO move robot close to goal (forward in case you are looking forward)
    }

    public void stop() {
        if (this.cameraMgr != null) {
            this.cameraMgr.stop();
            this.cameraMgr = null;
        }
        System.out.println("CameraMgr stopped. Trying to stop ShootingMgr");
        if (this.shootingMgr != null) {
            this.shootingMgr.stop();
            this.shootingMgr = null;
        }
        System.out.println("ShootingMgr stopped. Trying to stop MovementMgr");
        if (movementMgr != null) {
            this.movementMgr.stop();
            this.movementMgr = null;
        }
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

    public void start() {
        if (shootingMgr != null) {
            shootingMgr.start();
        }

        initAxisCalibration();

        if (movementMgr != null) {
            movementMgr.start();
        }
    }

    public void gazeUpTo_deg(double targetTheta) {
        shootingMgr.gazeUpTo_deg(targetTheta);
    }

    public void gazeUpBy_deg(double dTheta) {
        shootingMgr.gazeUpBy_deg(dTheta);
    }

    public void initAxisCalibration() {
        calibratingAxis = true;
        if (shootingMgr != null) {
            shootingMgr.aim(-1, Math.toRadians(60));
        }
    }

    public void turnHeadBy(double dTheta) {
        shootingMgr.turnHeadBy(dTheta);
    }

    public void shoot() {
        //TODO wait until everything is ready
        shootingMgr.shoot();
        PatternTracker.getInstance().advance();
    }

    public void toggleIntake() {
        shootingMgr.toggleIntake();
    }

    public void speedupTo(int rpm) {
        shootingMgr.speedupTo(rpm);
    }

    public void drive(double translation, double rotation, double extraRotation) {
        if (movementMgr != null) {
            movementMgr.drive(translation, rotation, extraRotation);
        }
//        return new FutureTask<>(this::drive)
    }

    public void driveTo(double x, double y, double heading) {
        movementMgr.driveTo(x, y, heading);
    }

    public double getCurrentHeadPositionRad() {
        return shootingMgr.getCurrentHeadPositionRad();
    }

    public void turnHeadTo(double theta) {
        shootingMgr.turnHeadTo(theta);
    }

    public void setAlliance(String alliance) {
        this.alliance = alliance;
    }

    public void setAutoAim(boolean autoAim) {
        this.autoAim = autoAim;
    }

    public void aim(double r, double theta) {
        shootingMgr.aim(r, theta);
    }

    public void speedUpTo(int RPM) {
        shootingMgr.speedupTo(RPM);
    }
}


