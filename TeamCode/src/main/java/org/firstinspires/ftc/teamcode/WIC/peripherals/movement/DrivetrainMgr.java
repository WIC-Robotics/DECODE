package org.firstinspires.ftc.teamcode.WIC.peripherals.movement;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class DrivetrainMgr {

    DcMotor FL;
    DcMotor FR;
    DcMotor BL;
    DcMotor BR;

    //TODO implement translateTo
    private boolean translateTo() {
        throw new IllegalStateException("Not yet implemented");
//        return false;
    }

    //TODO implement translateBy
    private boolean translateBy() {
        throw new IllegalStateException("Not yet implemented");
//        return false;
    }

    //TODO implement rotateTo
    private boolean rotateTo() {
        throw new IllegalStateException("Not yet implemented");
//        return false;
    }

    //TODO implement rotateBy
    private boolean rotateBy() {
        throw new IllegalStateException("Not yet implemented");
//        return false;
    }

    public DrivetrainMgr(HardwareMap hardwareMap) {
        this.BL = hardwareMap.get(DcMotor.class, "back left");
        this.BR = hardwareMap.get(DcMotor.class, "back right");
        this.FL = hardwareMap.get(DcMotor.class, "front left");
        this.FR = hardwareMap.get(DcMotor.class, "front right");

        this.BL.setDirection(DcMotorSimple.Direction.REVERSE);
        this.BR.setDirection(DcMotorSimple.Direction.FORWARD);
        this.FL.setDirection(DcMotorSimple.Direction.REVERSE);
        this.FR.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void stop() {
        FL.setPower(0);
        BL.setPower(0);
        FR.setPower(0);
        BR.setPower(0);
    }

    public void start() {

    }

    public void drive(double translation, double rotation, double extraRotation) {
        double leftPower    = translation - rotation - extraRotation;
        double rightPower   = translation + rotation + extraRotation;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(leftPower), Math.abs(rightPower));
        if (max >1.0) {
            leftPower /= max;
            rightPower /= max;
        }

        FL.setPower(leftPower);
        BL.setPower(leftPower);
        FR.setPower(rightPower);
        BR.setPower(rightPower);

    }

    public void driveTo(double x, double y, double heading) {

    }
}
