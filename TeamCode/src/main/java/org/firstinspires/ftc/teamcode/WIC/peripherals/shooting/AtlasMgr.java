package org.firstinspires.ftc.teamcode.WIC.peripherals.shooting;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;

import java.util.Random;

/**This class manages the hood up and down movement as if the robot is gazing up or down.
 * The name is analogous to the <B>Atlas vertebrum (C1)</B> which carries the skull and allows it to
 * look up and down.
 */
public class AtlasMgr {

    private static final String POTENTIOMETER_NAME = "potentiometer";
    private static final String SERVO_NAME = "hood angle";
    private double tempLastPosition = 0;
    private double targetTheta_deg = 0;

    public void stop() {
        continousMovementThread.stopThread();
    }

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
                double dTheta = targetTheta_deg - getCurrentAngle_deg();
                System.out.print("targetTheta= " + targetTheta_deg + " dTheta= "+ dTheta+" power= ");
                if(Math.abs(dTheta) > EPSILON){
                    atlasServo.setPower(Range.clip(dTheta, -1, 1));
                    System.out.print(dTheta);
                }else{
                    System.out.print("----");
                }
                System.out.println();
                if(ConfMgr.isTesting()){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {}
                }else {
                    Thread.yield();
                }
            }
        }
    }
    public ContinousMovementThread continousMovementThread = new ContinousMovementThread();


    /**
     * accepted angle error margin
     */
    private static final double EPSILON = 0.01;
    HardwareMap hardwareMap;
    CRServo atlasServo;
    AnalogInput atlasPotentiometer;

    private double SERVO_START_POWER = 0;
    private double SERVO_END_POWER = 0;
    private double ATLAS_START_ANGLE = 0;
    private double ATLAS_END_ANGLE;

    private double POTENTIOMETER_START_ANGLE_DEG;
    private double POTENTIOMETER_END_ANGLE_DEG;
    private double POTENTIOMETER_START_VOLTAGE;
    private double POTENTIOMETER_END_VOLTAGE;
    private double POTENTIOMETER_VOLTAGE_TO_ANGLE_DEG;

    private double POTENTIOMETER_OFFSET_ANGLE = 0;
    private double atlasToServoGearRatio = 0;

    //TODO create a <B>ConfigureAndCalibrate<//B> OpMode. Put a calibrateAtlas() function in it.

    public AtlasMgr(HardwareMap hardwareMap) {
        ConfMgr confMgr = ConfMgr.getInstance();

        this.hardwareMap = hardwareMap;

        this.atlasServo = hardwareMap.get(CRServo.class, SERVO_NAME);
        if (! ConfMgr.isTesting()) {
            this.atlasPotentiometer = hardwareMap.get(AnalogInput.class, POTENTIOMETER_NAME);
        }

        SERVO_START_POWER = confMgr.getDouble(this, "SERVO_START_POWER");
        SERVO_END_POWER = confMgr.getDouble(this, "SERVO_END_POWER");
        double SERVO_START_ANGLE = confMgr.getDouble(this, "SERVO_START_ANGLE");
        double SERVO_END_ANGLE = confMgr.getDouble(this, "SERVO_END_ANGLE");
        ATLAS_START_ANGLE = confMgr.getDouble(this, "ATLAS_START_ANGLE");
        ATLAS_END_ANGLE = confMgr.getDouble(this, "ATLAS_END_ANGLE");

        POTENTIOMETER_START_ANGLE_DEG = confMgr.getDouble(this, "POTENTIOMETER_START_ANGLE_DEG");
        POTENTIOMETER_END_ANGLE_DEG = confMgr.getDouble(this, "POTENTIOMETER_END_ANGLE_DEG");
        POTENTIOMETER_START_VOLTAGE = confMgr.getDouble(this, "POTENTIOMETER_START_VOLTAGE");
        POTENTIOMETER_END_VOLTAGE = confMgr.getDouble(this, "POTENTIOMETER_START_VOLTAGE");
        POTENTIOMETER_VOLTAGE_TO_ANGLE_DEG = (POTENTIOMETER_END_VOLTAGE - POTENTIOMETER_START_VOLTAGE) /
                (POTENTIOMETER_END_ANGLE_DEG - POTENTIOMETER_START_ANGLE_DEG);

        //TODO find min and max power, save them, and simplify the calculations
        atlasToServoGearRatio = (SERVO_END_ANGLE - SERVO_START_ANGLE) / (ATLAS_END_ANGLE - ATLAS_START_ANGLE);

        this.continousMovementThread.startThread();

//        KP = confMgr.getDouble(this, "KP");
    }

    public double voltageToAngle_deg(double voltage){
        return voltage * POTENTIOMETER_VOLTAGE_TO_ANGLE_DEG;
    }

    public double getCurrentAngle_deg(){
        if (ConfMgr.isTesting()){
            Random random = new Random();
            //We will simulate because we don't have a potentiometer
            double deltaThetaAtlas = targetTheta_deg - tempLastPosition;
            double change = random.nextDouble() * deltaThetaAtlas;
            tempLastPosition += (change - deltaThetaAtlas / 3);
            return tempLastPosition;
        } else {
            return voltageToAngle_deg(atlasPotentiometer.getVoltage()) - POTENTIOMETER_OFFSET_ANGLE;
        }
    }

    public void gazeUpTo_deg(double targetTheta) {
        this.targetTheta_deg = targetTheta;
        //TODO complete the calculations after getting feedback and power and angle ranges

//        double servoPowerFromStart = (targetTheta - ATLAS_START_ANGLE) * atlasToServoGearRatio;
//        double servoPower = servoPowerFromStart + SERVO_START_POWER;
//        atlasServo.setPosition(servoPower);
//        double servoPower = 0;
//        double currVoltage = atlasPotentiometer.getVoltage();
//        double currTheta = voltageToTheta(currVoltage);
//        double errorTheta = targetTheta - currTheta;
//
    }

//    private double voltageToTheta(double currVoltage) {
//        return (445.5(currVoltage)) / ()
//    }
//
    /**
     *
     * @param dTheta in the range [-180,180], effectively [0, 90]
     */
    public void gazeUpBy(double dTheta) {
//        this.Theta = dTheta;
    }
}
