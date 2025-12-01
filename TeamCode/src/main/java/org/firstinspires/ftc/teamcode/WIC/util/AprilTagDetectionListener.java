package org.firstinspires.ftc.teamcode.WIC.util;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.ArrayList;

public interface AprilTagDetectionListener {

    public void aprilTagDetectionsFound(ArrayList<AprilTagDetection> detections);

}
