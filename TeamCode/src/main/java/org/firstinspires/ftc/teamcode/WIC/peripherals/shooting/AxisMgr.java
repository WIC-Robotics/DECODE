package org.firstinspires.ftc.teamcode.WIC.peripherals.shooting;

import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;

/**This class manages the neck left and right movement as if the lazy suzan is looking right or left.
 * The name is analogous to the <B>Axis vertebrum (C2)</B> which articulates with the Atlas vertebum
 * and allows it to look left and right.
 */
public class AxisMgr {


    private double MAX_ANGLE_RAD;
    private double MIN_ANGLE_RAD;
    private double ENCODER_TICKS_TO_HEAD_ANGLE_RAD;
    private double HEAD_ANGLE_RAD_TO_ENCODER_TICKS;
    private double tempLastPosition = 0;

    private static final String AXIS_NAME = "lazysusan";
    private static final String RIGHT_BOUND_SENSOR_NAME = "right-limitSwitch";
    private static final String LEFT_BOUND_SENSOR_NAME = "left-limitSwitch";

//    private final double ONE_OVER_MOTOR_ENCODER_TICKS_PER_REVOLUTION;

    public void stop() {
        continuousMovementThread.stopThread();
    }

    public class ContinuousMovementThread extends Thread /*implements AxisExceptionHandler*/{
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
            while (active) {

                if (leftBoundSensor != null && leftBoundSensor.isPressed()){
                    leftBoundSensorClicked(); //also updates leftmostEncoderValue internally
                    if (leftmostEncoderValue - headAngleRadiansToMotorEncoderTicks(targetTheta) > HEAD_ANGLE_EPSILON_RAD) {
                        axisExceptionHandler.cannotTurnRight();
                    }
                }
                else if (rightBoundSensor != null && rightBoundSensor.isPressed()) {
                    rightBoundSensorClicked(); //also updates rightmostEncoderValue internally
                    if (headAngleRadiansToMotorEncoderTicks(targetTheta) - rightmostEncoderValue > HEAD_ANGLE_EPSILON_RAD) {
                        axisExceptionHandler.cannotTurnLeft();
                    }
                }
                if(!axisDcMotor.isBusy()){
                    System.out.printf("AxisAxis IDLE with targetTheta=\t%f\n", Math.toDegrees(targetTheta));
                }

//                System.out.printf("lastPosition=%f CurrentPosition=%f dTheta=%f nextPower=%f\n",tempLastPosition, currentHeadPosition, Math.toDegrees(dTheta), dTheta);


                double dTheta = targetTheta - getCurrentHeadPositionRad();
                if(!(
                        (rightBoundSensor.isPressed() && dTheta > HEAD_ANGLE_EPSILON_RAD) ||
                        (leftBoundSensor.isPressed() && -dTheta > HEAD_ANGLE_EPSILON_RAD)
                )){
                    axisDcMotor.setTargetPosition(headAngleRadiansToMotorEncoderTicks(targetTheta));
                }
                Thread.yield();
            }
        }
    }

    RevTouchSensor leftBoundSensor, rightBoundSensor;
    public ContinuousMovementThread continuousMovementThread = new ContinuousMovementThread();
    private AxisExceptionHandler axisExceptionHandler = null;
    double ACCEPTED_ERROR_AT_TARGET;
    /**Accepted heading error in degrees for current distance (from camera lens to April tag).
     * Positive values mean turning counterclockwise.
     * For simplicity, let's start with a fixed (always positive) value now*/
    private static final double HEAD_ANGLE_EPSILON_RAD = 0.002;
    private double targetTheta;

    DcMotor axisDcMotor;

    double rightmostEncoderValue, leftmostEncoderValue, range;

//    double speedInDegreesPerTick;


    public AxisMgr(HardwareMap hardwareMap, AxisExceptionHandler axisExceptionHandler) {
//        ONE_OVER_MOTOR_ENCODER_TICKS_PER_REVOLUTION = 1.0 / ConfMgr.getInstance().getDouble(this, "MOTOR_ENCODER_TICKS_PER_REVOLUTION");
        this.axisExceptionHandler = axisExceptionHandler;
        ConfMgr confMgr = ConfMgr.getInstance();

        leftBoundSensor = hardwareMap.get(RevTouchSensor.class, LEFT_BOUND_SENSOR_NAME);
        rightBoundSensor = hardwareMap.get(RevTouchSensor.class, RIGHT_BOUND_SENSOR_NAME);

        axisDcMotor = hardwareMap.get(DcMotor.class, AXIS_NAME);
        axisDcMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        axisDcMotor.setTargetPosition(0);
        axisDcMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        axisDcMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        axisDcMotor.setPower(0.5);
        axisDcMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        leftmostEncoderValue = confMgr.getInt(this, "LEFTMOST_ENCODER_VALUE");
        rightmostEncoderValue = confMgr.getInt(this, "RIGHTMOST_ENCODER_VALUE");

        MAX_ANGLE_RAD = confMgr.getDouble(this, "MAX_ANGLE");
        MIN_ANGLE_RAD = confMgr.getDouble(this, "MIN_ANGLE");

        ACCEPTED_ERROR_AT_TARGET = confMgr.getDouble(this, "ACCEPTED_ERROR_AT_TARGET");
        update();
    }

    public void calibrate(){
//        calibrating = true;
//        double v1 = headAngleRadiansToMotorEncoderTicks(100);
//        System.out.println("AxisAxis calibrate() targeting 100 as " + v1);
//        turnHeadTo(v1);
//        while (calibrating) {
//            System.out.println("AxisAxis going to yield at the left side");
//            Thread.yield();
//        }
//
//        calibrating = true;
//        double v2 = headAngleRadiansToMotorEncoderTicks(-100);
//        System.out.println("AxisAxis calibrate() targeting -100 as " + v2);
//        turnHeadTo(v2);
//        while (calibrating) {
//            System.out.println("AxisAxis going to yield at the right side");
//            Thread.yield();
//        }
//
//        turnHeadTo(0);

        //calculate the time for each degree to go to the next using different power values.
        //set values in the ConfMgr to save them.

    }
    public double getCurrentHeadPositionRad() {
        return motorEncoderTicksToHeadAngleRadians(axisDcMotor.getCurrentPosition());
    }

    private double motorEncoderTicksToHeadAngleRadians(double encoderTicks) {
        return encoderTicks * ENCODER_TICKS_TO_HEAD_ANGLE_RAD;
    }
    private int headAngleRadiansToMotorEncoderTicks(double headAngle_rad) {
        return (int) (headAngle_rad * HEAD_ANGLE_RAD_TO_ENCODER_TICKS);
    }

    private void update(){
//        EPSILON = Math.toDegrees(Math.asin(ACCEPTED_ERROR_AT_TARGET/(ApriltagToCenterDistance + cameraTocCenterDistance+ apriltagDistance)));
        this.HEAD_ANGLE_RAD_TO_ENCODER_TICKS =
                (rightmostEncoderValue - leftmostEncoderValue) /
                (MAX_ANGLE_RAD - MIN_ANGLE_RAD);
        this.ENCODER_TICKS_TO_HEAD_ANGLE_RAD = 1.0 / this.HEAD_ANGLE_RAD_TO_ENCODER_TICKS;
    }

    public void turnHeadTo(double theta) {
        this.targetTheta = theta;
//        double deltaTheta = targetTheta - theta;
    }

    public void turnHeadBy(double dTheta) {
        turnHeadTo(dTheta + getCurrentHeadPositionRad());
    }

    private void leftBoundSensorClicked(){
        //accessing the field is 10-40 times slower than accessing a local variable.
        // So we will use a local variable instead.
        //this.leftmostEncoderValue = axisDcMotor.getCurrentPosition();

        /*
         * we put {@code axisDcMotor.getCurrentPosition()} in a local variable first to ensure
         * that we get the position the <strong>INSTANT</strong> we touch the sensor, and that
         * we set the target position to the exact value. If we set the target value first then
         * retrieve it, we could retrieve a different value.
         */
        int currentPosition = axisDcMotor.getCurrentPosition();
        this.axisDcMotor.setTargetPosition(currentPosition);
        this.leftmostEncoderValue = currentPosition;
        System.out.println("AxisAxis left BoundSensor Clicked");
        System.out.println("AxisAxis new leftmostEncoderValue = " + leftmostEncoderValue );
        update();
    }

    /**
     * we put {@code axisDcMotor.getCurrentPosition()} in a local variable first to ensure that we
     * get the position the <strong>INSTANT</strong> we touch the sensor
     */
    private void rightBoundSensorClicked(){
        //accessing the field is 10-40 times slower than accessing a local variable.
        // So we will use a local variable instead.
        //this.rightmostEncoderValue = axisDcMotor.getCurrentPosition();

        /*
         * we put {@code axisDcMotor.getCurrentPosition()} in a local variable first to ensure
         * that we get the position the <strong>INSTANT</strong> we touch the sensor, and that
         * we set the target position to the exact value. If we set the target value first then
         * retrieve it, we could retrieve a different value.
         */
        int currentPosition = axisDcMotor.getCurrentPosition();
        this.axisDcMotor.setTargetPosition(currentPosition);
        this.rightmostEncoderValue = currentPosition;
        System.out.println("AxisAxis right BoundSensor Clicked. New rightmostEncoderValue = " + rightmostEncoderValue );
        update();
    }

    public void start() {
        System.out.println("AtlasAtlas Starting Continuous Movement Thread");
        continuousMovementThread.startThread();
    }
}
