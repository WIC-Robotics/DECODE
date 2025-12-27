package org.firstinspires.ftc.teamcode.WIC.peripherals.shooting;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;

public class WheelMgr {

    HeadExceptionHandler headExceptionHandler;
    DcMotorEx flywheel;

    String FLYWHEEL_NAME = "flywheel";

    double MOTOR_TICKS_PER_SECOND;

    public WheelMgr(HardwareMap hardwareMap, HeadExceptionHandler headExceptionHandler) {
        this.headExceptionHandler = headExceptionHandler;
        this.flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");

        MOTOR_TICKS_PER_SECOND = ConfMgr.getInstance().getDouble(this, "MOTOR_TICKS_PER_SECOND");
    }

    public void speedupTo(double speed){
        //TODO implement
    }

    public float timeBetweenSpeeds(int fromRPM, int toRPM) {
        //TODO implement
        return 0;
    }

    public float timeToSpeed(int targetRPM) {
        double velocity = flywheel.getVelocity() * MOTOR_TICKS_PER_SECOND; //Velocity(AngleUnit.RADIANS);
        return timeBetweenSpeeds((int) velocity, targetRPM);
    }

    public void stop(){
        //TODO implement
    }
}
