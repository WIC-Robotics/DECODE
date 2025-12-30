package org.firstinspires.ftc.teamcode.WIC.peripherals.shooting;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;

public class WheelMgr {

    private final double EPSILON;
    private int targetRPM;
    private double currentPower;

    public void setRPM_ADJUSTMENT_FACTOR(double RPM_ADJUSTMENT_FACTOR) {
        this.RPM_ADJUSTMENT_FACTOR = RPM_ADJUSTMENT_FACTOR;
    }

    private double RPM_ADJUSTMENT_FACTOR;

    public class ContinousMovementThread extends Thread{
        boolean active = false;

        public synchronized void startThread() {
            this.active = true;
            super.start();
        }
        public synchronized void stopThread() {
            this.active = false;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public void run() {
            while (active){
                double currentRPM = getCurrentSpeedRPM();
                double dRPM = (targetRPM - currentRPM) * RPM_ADJUSTMENT_FACTOR;
                double tempOldPower = currentPower;

                if(Math.abs(dRPM) > EPSILON){
                    if (currentPower > 1.2 && Math.signum(dRPM) > 0) {
                        shootingExceptionHandler.robotTooFar();
                    } else if(currentPower < 0. && Math.signum(dRPM) < 0){ //TODO update lower threshold
                        shootingExceptionHandler.robotTooClose();
                    }else {
                        currentPower += (dRPM / MOTOR_MAX_RPM);
                        flywheel.setPower(currentPower);
                    }
                }
                System.out.printf("WheelMgr targetRPM: %d currentRPM: %.6f dRPM: %.6f oldPower: %.6f currentPower: %.6f\n",
                        targetRPM, currentRPM, dRPM, tempOldPower, currentPower);
                if(ConfMgr.isDemo()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {}
                }else {
                    Thread.yield();
                }
            }
        }
    }
    public ContinousMovementThread continousMovementThread = new ContinousMovementThread();

    private final double MOTOR_MAX_RPM;
    ShootingExceptionHandler shootingExceptionHandler;
    DcMotorEx flywheel;

    String FLYWHEEL_NAME = "flywheel";

    double MOTOR_TICKS_PER_REVOLUTION;

    public WheelMgr(HardwareMap hardwareMap, ShootingExceptionHandler shootingExceptionHandler) {
        ConfMgr confMgr = ConfMgr.getInstance();
        this.shootingExceptionHandler = shootingExceptionHandler;
        this.flywheel = hardwareMap.get(DcMotorEx.class, FLYWHEEL_NAME);
        this.flywheel.setDirection(DcMotorSimple.Direction.FORWARD);

        MOTOR_TICKS_PER_REVOLUTION = confMgr.getDouble(this, "MOTOR_TICKS_PER_REVOLUTION");
        MOTOR_MAX_RPM = confMgr.getDouble(this, "MOTOR_MAX_RPM");
        EPSILON = confMgr.getDouble(this, "EPSILON");
        RPM_ADJUSTMENT_FACTOR = confMgr.getDouble(this, "RPM_ADJUSTMENT_FACTOR");

        this.continousMovementThread.startThread();
    }

    public void speedupTo(int rpm){
        targetRPM = rpm;
//        currentPower = findInitialPowerFor(speed);
    }

    private double findInitialPowerFor(int targetRPM) {
        return targetRPM / MOTOR_MAX_RPM;
    }

    public float timeBetweenSpeeds(int fromRPM, int toRPM) {
        //TODO implement
        return 0;
    }

    public double getCurrentSpeedRPM() {
        return (flywheel.getVelocity() / MOTOR_TICKS_PER_REVOLUTION) * 60;
    }

    public float timeToSpeed(int targetRPM) {
        double velocity = getCurrentSpeedRPM(); //Velocity(AngleUnit.RADIANS);
        return timeBetweenSpeeds((int) velocity, targetRPM);
    }

    public void stop(){
        flywheel.setPower(0);
        continousMovementThread.stopThread();
        //TODO implement
    }
}
