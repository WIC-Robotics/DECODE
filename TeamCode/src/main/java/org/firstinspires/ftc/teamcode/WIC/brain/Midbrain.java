package org.firstinspires.ftc.teamcode.WIC.brain;

import android.os.SystemClock;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.WIC.ShootingMgr;
import org.firstinspires.ftc.teamcode.WIC.util.AppContextProvider;
import org.firstinspires.ftc.teamcode.WIC.util.AprilTagDetectionListener;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.CameraMgr;
import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.ArrayList;

public class Midbrain implements AprilTagDetectionListener {

    private HardwareMap hardwareMap = null;
    private Telemetry telemetry;

    ShootingMgr shootingMgr;

    private ConfMgr confMgr;

    private CameraMgr cameraMgr;

    private long startTime = 0;

    public Midbrain(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;

        AppContextProvider.setAppContext(this.hardwareMap.appContext);
        this.confMgr = ConfMgr.getInstance();

        this.cameraMgr = new CameraMgr(this.hardwareMap, this);
        this.shootingMgr = new ShootingMgr(this.hardwareMap);
        this.startTime = System.currentTimeMillis();
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
            telemetry.clearAll();
            telemetry.addLine("NO DETECTIONS at "+ (System.currentTimeMillis() - startTime)/1000 );
            return;
        }

        telemetry.addLine("# AprilTags Detected " + aprilTagDetections.size() + " at " + SystemClock.currentThreadTimeMillis()/1000);

        for (AprilTagDetection detection : aprilTagDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }
        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation");


        System.out.println("\n# AprilTags Detected " + aprilTagDetections.size());

        for (AprilTagDetection detection : aprilTagDetections) {
            if (detection.metadata != null) {
                System.out.println(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                System.out.println(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                System.out.println(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                System.out.println(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                System.out.println(String.format("\n==== (ID %d) Unknown", detection.id));
                System.out.println(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }
    }
}

