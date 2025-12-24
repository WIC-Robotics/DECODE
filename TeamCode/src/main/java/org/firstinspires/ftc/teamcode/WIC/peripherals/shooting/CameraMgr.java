package org.firstinspires.ftc.teamcode.WIC.peripherals.shooting;

import android.util.Size;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.WIC.util.AprilTagDetectionListener;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;

public class CameraMgr {

    VisionPortal visionPortal;
    AprilTagProcessor aprilTagProcessor;

    private final String WEBCAM_NAME = "Webcam 1";
    CameraName CAMERA;
//    BuiltinCameraDirection CAMERA = BuiltinCameraDirection.BACK;

    private CameraThread cameraThread = null;

    private AprilTagDetectionListener aprilTagDetectionListener = null;

    public CameraMgr(HardwareMap hardwareMap, AprilTagDetectionListener aprilTagDetectionListener) {
        this.aprilTagDetectionListener = aprilTagDetectionListener;

        this.aprilTagProcessor = buildAprilTagProcessor(true, true, true,
                AprilTagProcessor.TagFamily.TAG_36h11, AprilTagGameDatabase.getCurrentGameTagLibrary(),
                DistanceUnit.INCH, AngleUnit.DEGREES);
        this.aprilTagProcessor.setDecimation(2);

        CAMERA = hardwareMap.get(WebcamName.class, WEBCAM_NAME);
        this.visionPortal = buildVisionPortal(CAMERA, this.aprilTagProcessor, 640, 480);
//        this.visionPortal = buildVisionPortal(BuiltinCameraDirection.BACK, this.aprilTagProcessor); // Use if phone camera

        cameraThread = new CameraThread();
    }


    private VisionPortal buildVisionPortal(CameraName camera, AprilTagProcessor aprilTagProcessor,
                                           int width, int height) {
        VisionPortal.Builder VPBuilder = new VisionPortal.Builder()

                .setCamera(camera)
                .addProcessors(aprilTagProcessor)
//                .setCameraResolution(new Size(width, height))
                ;

        return VPBuilder.build();
    }

    private VisionPortal buildVisionPortal(BuiltinCameraDirection camera, AprilTagProcessor aprilTagProcessor) {
            VisionPortal.Builder VPBuilder = new VisionPortal.Builder()

                    .setCamera(camera)
                    .addProcessors(aprilTagProcessor)
                    ;

            return VPBuilder.build();
    }

    private AprilTagProcessor buildAprilTagProcessor(
            boolean drawAxes, boolean drawCube, boolean drawTagOutline,
            AprilTagProcessor.TagFamily tagFamily, AprilTagLibrary tagLibrary,
            DistanceUnit distanceUnit, AngleUnit angleUnit) {
        AprilTagProcessor.Builder aTPBuilder = new AprilTagProcessor.Builder()
                .setDrawAxes(drawAxes)
                .setDrawCubeProjection(drawCube)
                .setDrawTagOutline(drawTagOutline)
                .setTagFamily(tagFamily)
                .setTagLibrary(tagLibrary)
                .setOutputUnits(distanceUnit, angleUnit)
                ;
        return aTPBuilder.build();
    }

    private AprilTagProcessor buildAprilTagProcessor(
            boolean drawAxes, boolean drawCube, boolean drawTagOutline,
            AprilTagProcessor.TagFamily tagFamily, AprilTagLibrary tagLibrary,
            DistanceUnit distanceUnit, AngleUnit angleUnit,
            double fx, double fy, double cx, double cy) {
        AprilTagProcessor.Builder ATPBuilder = new AprilTagProcessor.Builder()
                .setDrawAxes(drawAxes)
                .setDrawCubeProjection(drawCube)
                .setDrawTagOutline(drawTagOutline)
                .setTagFamily(tagFamily)
                .setTagLibrary(tagLibrary)
                .setOutputUnits(distanceUnit, angleUnit)
                .setLensIntrinsics(fx, fy, cx, cy)
                ;
        return ATPBuilder.build();
    }

    public void startStreaming() {
        visionPortal.resumeStreaming();
        cameraThread.startThread();
    }

    public void stopStreaming() {
        visionPortal.stopStreaming();
        cameraThread.stopThread();
    }

    private class CameraThread extends Thread {
        boolean moreWork = false;

        public synchronized void startThread() {
            moreWork = true;
            System.out.println("Starting Thread");
            super.start();
            System.out.println("Thread Started");
        }

        @Override
        public void run() {
            while (moreWork){
                ArrayList<AprilTagDetection> detections = aprilTagProcessor.getFreshDetections();
                if (detections != null) {
                    aprilTagDetectionListener.aprilTagDetectionsFound(detections);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    moreWork = false;
                }
                aprilTagDetectionListener.aprilTagDetectionsFound(null);
            }

        }

        public void stopThread(){
            moreWork = false;
        }
    }

}
