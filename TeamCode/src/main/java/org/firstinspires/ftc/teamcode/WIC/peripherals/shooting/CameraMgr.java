package org.firstinspires.ftc.teamcode.WIC.peripherals.shooting;

import android.util.Size;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.WIC.util.AprilTagDetectionListener;
import org.firstinspires.ftc.teamcode.WIC.util.OutputHandler;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;

public class CameraMgr {

    VisionPortal visionPortal;
    AprilTagProcessor aprilTagProcessor;
    public static enum AprilTagStatus {APRILTAG_OFF, APRILTAG_NOT_DETECTED, APRILTAG_DETECTED_OTHER, APRILTAG_DETECTED_TARGET, APRILTAG_FIXED};

    private final String WEBCAM_NAME = "Webcam 1";
    CameraName CAMERA;
//    BuiltinCameraDirection CAMERA = BuiltinCameraDirection.BACK;

    private CameraThread cameraThread = null;

    private AprilTagDetectionListener aprilTagDetectionListener = null;

    public CameraMgr(HardwareMap hardwareMap, AprilTagDetectionListener aprilTagDetectionListener) {
        this.aprilTagDetectionListener = aprilTagDetectionListener;

        this.aprilTagProcessor = buildAprilTagProcessor(true, true, true,
                AprilTagProcessor.TagFamily.TAG_36h11, AprilTagGameDatabase.getCurrentGameTagLibrary(),
                DistanceUnit.INCH, AngleUnit.RADIANS);
        this.aprilTagProcessor.setDecimation(2);

        CAMERA = hardwareMap.get(WebcamName.class, WEBCAM_NAME);
        this.visionPortal = buildVisionPortal(CAMERA, this.aprilTagProcessor, VisionPortal.StreamFormat.MJPEG
                , 1280, 720
//                , 640, 480
        );
//        this.visionPortal = buildVisionPortal(BuiltinCameraDirection.BACK, this.aprilTagProcessor); // Use if phone camera

        cameraThread = new CameraThread();
        this.aprilTagProcessor.setDecimation(3);
    }


    private VisionPortal buildVisionPortal(CameraName camera, AprilTagProcessor aprilTagProcessor, VisionPortal.StreamFormat streamFormat
            , int width, int height
    ) {
        VisionPortal.Builder VPBuilder = new VisionPortal.Builder()

                .setCamera(camera)
                .addProcessors(aprilTagProcessor)
                .setCameraResolution(new Size(width, height))
                .setStreamFormat(streamFormat)
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

    public void stop() {
        cameraThread.stopThread();
        visionPortal.stopStreaming();
        OutputHandler.getInstance().headStatus(CameraMgr.AprilTagStatus.APRILTAG_OFF);
    }

    private class CameraThread extends Thread {
        boolean moreWork = false;

        public synchronized void startThread() {
            moreWork = true;
            System.out.println("Starting Camera Thread");
            super.start();
            System.out.println("Camera Thread Started");
            OutputHandler.getInstance().headStatus(AprilTagStatus.APRILTAG_NOT_DETECTED);
        }

        @Override
        public void run() {
            while (moreWork){
//                System.out.println("CameraThread.run() called at " + System.currentTimeMillis());
                ArrayList<AprilTagDetection> detections = aprilTagProcessor.getFreshDetections();
                if (moreWork && detections != null) {
                    aprilTagDetectionListener.aprilTagDetectionsFound(detections);
                } else {
                    aprilTagDetectionListener.aprilTagDetectionsFound(null);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    moreWork = false;
                }
            }

        }

        public void stopThread(){
            moreWork = false;
        }
    }

}
