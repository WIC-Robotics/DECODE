package org.firstinspires.ftc.teamcode.WIC.peripherals.movement;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class MovementMgr {

    private DrivetrainMgr drivetrainMgr;
    private DeadwheelMgr deadwheelMgr;

    public MovementMgr(HardwareMap hardwareMap) {
        this.drivetrainMgr = new DrivetrainMgr(hardwareMap);
        this.deadwheelMgr = new DeadwheelMgr(hardwareMap);
    }

    public void drive(double translation, double rotation, double extraRotation) {
        //TODO use roadrunner
        //TODO use Pinpoint Odometry Computer
        drivetrainMgr.drive(translation, rotation, extraRotation);

    }

    public void stop() {
        this.drivetrainMgr.stop();
        this.deadwheelMgr.stop();
    }

    public void start() {
        if (drivetrainMgr != null) {
            this.drivetrainMgr.start();
        }
        if (deadwheelMgr != null) {
            this.deadwheelMgr.start();
        }
    }
}
