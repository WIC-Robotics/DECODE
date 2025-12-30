package org.firstinspires.ftc.teamcode.WIC.peripherals.shooting;

import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;

import java.util.Random;

/**This class manages the neck left and right movement as if the lazy suzan is looking right or left.
 * The name is analogous to the <B>Axis vertebrum (C2)</B> which articulates with the Atlas vertebum
 * and allows it to look left and right.
 */
public class AxisMgr {


    private double tempLastPosition = 0;

    private static final String AXIS_NAME = "lazysusan";
    private static final String MAX_BOUND_SENSOR_NAME = "right- limitSwitch";
    private static final String MIN_BOUND_SENSOR_NAME = "left-limitSwitch";

    private final double ONE_OVER_MOTOR_ENCODER_TICKS_PER_REVOLUTION;

    public void stop() {
        continousMovementThread.stopThread();
    }

    public class ContinousMovementThread extends Thread{
        boolean active = false;

        public synchronized void startThread() {
            setActive(true);
            super.start();
        }
        public synchronized void stopThread() {
            this.active = false;
        }

        void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public void run() {
            while (active){
                double currentPosition = getCurrentPosition();
                double dTheta = targetTheta - currentPosition;

//                System.out.printf("lastPosition=%f CurrentPosition=%f dTheta=%f nextPower=%f\n",tempLastPosition, currentPosition, Math.toDegrees(dTheta), dTheta);


//                if(Math.abs(dTheta) > EPSILON) {
//                    axisDcMotor.setPower(dTheta);
//                }else{
//                    axisDcMotor.setPower(0);
//                }
                if (ConfMgr.isTestingDevice()) {
                    System.out.println("---------------> Axis Power: " + dTheta);
                }
                axisDcMotor.setPower(dTheta);

                if (leftBoundSensor != null && leftBoundSensor.isPressed()){
                    leftBoundSensorClicked();
                    if (dTheta > EPSILON)
                        axisExceptionHandler.cannotTurnLeft();
                }
                else if (rightBoundSensor != null && rightBoundSensor.isPressed()) {
                    rightBoundSensorClicked();
                    if (-dTheta > EPSILON)
                        axisExceptionHandler.cannotTurnRight();
                }
                if(ConfMgr.isTestingDevice()){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {}
                }else {
                    Thread.yield();
                }
            }
        }
    }

    private double getCurrentPosition() {
        if (ConfMgr.isTestingDevice()) {
            //We will simulate because we don't have encoder cable
            double deltaTheta = targetTheta - tempLastPosition;
            Random random = new Random();
            double change = random.nextDouble() * deltaTheta;
            tempLastPosition += (change - deltaTheta / 3);
            return tempLastPosition;
        } else {
            return axisDcMotor.getCurrentPosition() * ONE_OVER_MOTOR_ENCODER_TICKS_PER_REVOLUTION * 2 * Math.PI;
        }
    }

    public ContinousMovementThread continousMovementThread = new ContinousMovementThread();
    private AxisExceptionHandler axisExceptionHandler = null;
    double ACCEPTED_ERROR_AT_TARGET;
    /**Accepted heading error in degrees for current distance (from camera lens to April tag).
     * Positive values mean turning counterclockwise.
     * For simplicity, let's start with a fixed (always positive) value now*/
    private static final double EPSILON = 2;
    private double targetTheta;

    DcMotor axisDcMotor;
    RevTouchSensor leftBoundSensor, rightBoundSensor;

    double rightmostEncoderValue, leftmostEncoderValue;

//    double speedInDegreesPerTick;


    public AxisMgr(HardwareMap hardwareMap, AxisExceptionHandler axisExceptionHandler) {
        ONE_OVER_MOTOR_ENCODER_TICKS_PER_REVOLUTION = 1.0 / ConfMgr.getInstance().getDouble(this, "MOTOR_ENCODER_TICKS_PER_REVOLUTION");
        this.axisExceptionHandler = axisExceptionHandler;
        axisDcMotor = hardwareMap.get(DcMotor.class, AXIS_NAME);
        if (! ConfMgr.isTestingDevice()) {
            leftBoundSensor = hardwareMap.get(RevTouchSensor.class, MAX_BOUND_SENSOR_NAME);
            rightBoundSensor = hardwareMap.get(RevTouchSensor.class, MIN_BOUND_SENSOR_NAME);
        }

        //TODO Shall I keep them or undo this temp
        if (ConfMgr.isTestingDevice()) {
            axisDcMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            axisDcMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        }

//        speedInDegreesPerTick = ConfMgr.getInstance().getDouble(this, "changePerTick");
        ACCEPTED_ERROR_AT_TARGET = ConfMgr.getInstance().getDouble(this, "ACCEPTED_ERROR_AT_TARGET");

        continousMovementThread.startThread();
    }

    public void calibrate(){
        //turn all the way to the left (stop when leftBoundSensor is clicked).
        //save encoder value as leftmostEncoderValue
        //turn all the way to the right (stop when rightBoundSensor is clicked).
        //save encoder value as rightmostEncoderValue

        //calculate the time for each degree to go to the next using different power values.
        update();

        //set values in the ConfMgr to save them.
    }
    private void update(){
//        EPSILON = Math.toDegrees(Math.asin(ACCEPTED_ERROR_AT_TARGET/(ApriltagToCenterDistance + cameraTocCenterDistance+ apriltagDistance)));
    }

    public void turnHeadTo(double theta) {
        this.targetTheta = theta;
//        double deltaTheta = targetTheta - theta;
    }


    private void leftBoundSensorClicked(){
        this.axisDcMotor.setPower(0);
        this.leftmostEncoderValue = axisDcMotor.getCurrentPosition();
        update();
    }
    private void rightBoundSensorClicked(){
        this.axisDcMotor.setPower(0);
        this.rightmostEncoderValue = axisDcMotor.getCurrentPosition();
        update();
    }

}
