package org.firstinspires.ftc.teamcode.WIC.peripherals.shooting;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;

/**This class manages the hood up and down movement as if the robot is gazing up or down.
 * The name is analogous to the <B>Atlas vertebrum (C1)</B> which carries the skull and allows it to
 * look up and down.
 */
public class AtlasMgr {

    private double targetTheta = 0;
    public class CotinousMovementThread extends Thread{
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
                double dTheta = getCurrentAngle() - targetTheta;
                if(Math.abs(dTheta) > EPSILON)
                    atlasServo.setPower(dTheta);
                Thread.yield();
            }
        }
    }
    public CotinousMovementThread cotinousMovementThread = new CotinousMovementThread();


    /**
     * accepted error margin in angles
     */
    private static final double EPSILON = 0.1;
    HardwareMap hardwareMap;
    CRServo atlasServo;
    AnalogInput atlasPotentiometer;

    private double SERVO_START_POWER = 0;
    private double SERVO_END_POWER = 0;
    private double ATLAS_START_ANGLE = 0;
    private double ATLAS_END_ANGLE;

    private double POTENTIOMETER_START_ANGLE;
    private double POTENTIOMETER_END_ANGLE;
    private double POTENTIOMETER_START_VOLTAGE;
    private double POTENTIOMETER_END_VOLTAGE;
    private double POTENTIOMETER_VOLTAGE_TO_ANGLE;

    private double POTENTIOMETER_OFFSET_ANGLE = 0;
    private double atlasToServoGearRatio = 0;

//    double KP;

    //TODO create a <B>ConfigureAndCalibrate<//B> OpMode. Put a calibrateAtlas() function in it.

    public AtlasMgr(HardwareMap hardwareMap) {
        ConfMgr confMgr = ConfMgr.getInstance();

        this.hardwareMap = hardwareMap;

        this.atlasServo = hardwareMap.get(CRServo.class, "Hood");
        this.atlasPotentiometer = hardwareMap.get(AnalogInput.class, "Hood Potentiometer");

        SERVO_START_POWER = confMgr.getDouble(this, "SERVO_START_POWER");
        SERVO_END_POWER = confMgr.getDouble(this, "SERVO_END_POWER");
        double SERVO_START_ANGLE = confMgr.getDouble(this, "SERVO_START_ANGLE");
        double SERVO_END_ANGLE = confMgr.getDouble(this, "SERVO_END_ANGLE");
        ATLAS_START_ANGLE = confMgr.getDouble(this, "ATLAS_START_ANGLE");
        ATLAS_END_ANGLE = confMgr.getDouble(this, "ATLAS_END_ANGLE");

        POTENTIOMETER_START_ANGLE = confMgr.getDouble(this, "POTENTIOMETER_START_ANGLE");
        POTENTIOMETER_END_ANGLE = confMgr.getDouble(this, "POTENTIOMETER_END_ANGLE");
        POTENTIOMETER_START_VOLTAGE = confMgr.getDouble(this, "POTENTIOMETER_START_VOLTAGE");
        POTENTIOMETER_END_VOLTAGE = confMgr.getDouble(this, "POTENTIOMETER_START_VOLTAGE");
        POTENTIOMETER_VOLTAGE_TO_ANGLE = (POTENTIOMETER_END_VOLTAGE / POTENTIOMETER_START_VOLTAGE) /
                (POTENTIOMETER_END_ANGLE - POTENTIOMETER_START_ANGLE);

        //TODO find min and max power, save them, and simplify the calculations
        atlasToServoGearRatio = (SERVO_END_ANGLE - SERVO_START_ANGLE) / (ATLAS_END_ANGLE - ATLAS_START_ANGLE);

        this.cotinousMovementThread.startThread();

//        KP = confMgr.getDouble(this, "KP");
    }

    public double voltageToAngle(double voltage){
        return voltage * POTENTIOMETER_VOLTAGE_TO_ANGLE;
    }

    public double getCurrentAngle(){
        return voltageToAngle(atlasPotentiometer.getVoltage()) - POTENTIOMETER_OFFSET_ANGLE;
    }

    public void gazeUpTo(double targetTheta) {
        this.targetTheta = targetTheta;
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
