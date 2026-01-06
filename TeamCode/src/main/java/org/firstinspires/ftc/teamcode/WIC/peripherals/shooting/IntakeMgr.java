package org.firstinspires.ftc.teamcode.WIC.peripherals.shooting;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;

public class IntakeMgr {

    private final double STAGE_1_MAX_POWER = 1;
    private final double STAGE_1_MIN_POWER = 0;
    private final double STAGE_2_MOTOR_TICKS_PER_REV;
    private final double STAGE_2_STEP_DEG;
    private int ballIndex;

    DcMotorEx stage1;
    boolean stage1On;
    DcMotorEx stage2;

    public IntakeMgr(HardwareMap hardwareMap) {
        ballIndex = 0;

        stage1 = hardwareMap.get(DcMotorEx.class, "Stage 1");
        stage1.setDirection(DcMotorSimple.Direction.REVERSE);
        stage2 = hardwareMap.get(DcMotorEx.class, "Stage 2");

        if (stage2 != null) {
            stage2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            stage2.setTargetPosition(0);
            stage2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            stage2.setPower(1);

        }

        STAGE_2_MOTOR_TICKS_PER_REV = ConfMgr.getInstance().getDouble(this, "STAGE_2_MOTOR_TICKS_PER_REVOLUTION");
        STAGE_2_STEP_DEG = Math.toRadians(ConfMgr.getInstance().getDouble(this, "STAGE_2_STEP_DEG"));
    }

    private double stage2AngleDegreesToMotorTicks(double degrees) {
        return (degrees * STAGE_2_MOTOR_TICKS_PER_REV) / 360;
    }

    public void advance() {
        if (stage2 != null) {
            stage2.setTargetPosition((int) stage2AngleDegreesToMotorTicks(++ballIndex * STAGE_2_STEP_DEG));
        }
    }

    public void start() {
        if (stage1 != null) {
            stage1.setPower(STAGE_1_MAX_POWER);
        }
    }

    public void stop() {
        if (stage1 != null) {
            stage1.setPower(STAGE_1_MIN_POWER);
        }
        if (stage2 != null) {
            stage2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            stage2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
        ballIndex = 0;
    }

    public void calibrate() {

    }

    public void toggleIntake() {
        stage1On = !stage1On;
        stage1.setPower(stage1On ? STAGE_1_MAX_POWER : STAGE_1_MIN_POWER);
    }
}
