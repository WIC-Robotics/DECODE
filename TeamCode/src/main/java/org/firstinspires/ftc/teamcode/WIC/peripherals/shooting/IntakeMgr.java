package org.firstinspires.ftc.teamcode.WIC.peripherals.shooting;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;

public class IntakeMgr {

    private final double STAGE_1_MAX_POWER = 1;
    private final double STAGE_1_MIN_POWER = 0;
    private final double INDEXER_MOTOR_TICKS_PER_REV;
    private final double INDEXER_STEP_DEG;
    private int ballIndex;

    DcMotorEx intake;
    boolean intakeOn;
    DcMotorEx indexer;

    public IntakeMgr(HardwareMap hardwareMap) {
        ballIndex = 0;

        intake = hardwareMap.get(DcMotorEx.class, "Intake");
        if (intake != null) {
            intake.setDirection(DcMotorSimple.Direction.REVERSE);
        }

        indexer = hardwareMap.get(DcMotorEx.class, "Indexer");
        if (indexer != null) {
            indexer.setDirection(DcMotorSimple.Direction.REVERSE);
            indexer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            indexer.setTargetPosition(0);
            indexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            indexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            indexer.setPower(1);
        }

        INDEXER_MOTOR_TICKS_PER_REV = ConfMgr.getInstance().getDouble(this, "INDEXER_MOTOR_TICKS_PER_REVOLUTION");
        INDEXER_STEP_DEG = ConfMgr.getInstance().getDouble(this, "INDEXER_STEP_DEG");
    }

    private double stage2AngleDegreesToMotorTicks(double degrees) {
        return (degrees * INDEXER_MOTOR_TICKS_PER_REV) / 360;
    }

    public void advance() {
        if (indexer != null) {
            indexer.setTargetPosition((int) stage2AngleDegreesToMotorTicks(++ballIndex * INDEXER_STEP_DEG));
        }
    }

    public void start() {
        if (intake != null) {
            updatePower();
        }
    }

    public void stop() {
        if (intake != null) {
            intake.setPower(STAGE_1_MIN_POWER);
        }
        if (indexer != null) {
            indexer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            indexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            indexer.setTargetPosition(0);
        }
        ballIndex = 0;
    }

    public void calibrate() {

    }

    public void toggleIntake() {
        intakeOn = !intakeOn;
        updatePower();
    }

    private void updatePower() {
        intake.setPower(intakeOn ? STAGE_1_MAX_POWER : STAGE_1_MIN_POWER);
    }

    public void shoot() {

    }
}
