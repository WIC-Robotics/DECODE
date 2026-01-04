package org.firstinspires.ftc.teamcode.WIC.peripherals.shooting;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
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
//    private double tempLastPosition = 0;
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
//                System.out.print("AtlasAtlas targetTheta= " + targetTheta_deg + " dTheta= "+ dTheta / 10+" power= "); //TODO undo temp
                if(Math.abs(dTheta) > EPSILON){
                    atlasServo.setPower(Range.clip(dTheta / 10, -1, 1));
//                    System.out.print(dTheta);
                }else{
//                    System.out.print("----");
                    atlasServo.setPower(0);
                }
                System.out.println();
                if(ConfMgr.isDemo()){
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
    private final double EPSILON;
    HardwareMap hardwareMap;
    CRServo atlasServo;
    AnalogInput atlasPotentiometer;

//    private double SERVO_START_POWER = 0;
//    private double SERVO_END_POWER = 0;
//    private double ATLAS_START_ANGLE = 0;
//    private double ATLAS_END_ANGLE;

    private final double POTENTIOMETER_START_ANGLE_DEG;
    private final double POTENTIOMETER_END_ANGLE_DEG;
    private final double POTENTIOMETER_START_VOLTAGE;
    private final double POTENTIOMETER_END_VOLTAGE;
    private double POTENTIOMETER_VOLTAGE_TO_ANGLE_DEG;

    private double POTENTIOMETER_OFFSET_ANGLE = 0;
//    private double atlasToServoGearRatio = 0;

    public AtlasMgr(HardwareMap hardwareMap) {
        ConfMgr confMgr = ConfMgr.getInstance();

        this.hardwareMap = hardwareMap;

        this.atlasServo = hardwareMap.get(CRServo.class, SERVO_NAME);
        this.atlasPotentiometer = hardwareMap.get(AnalogInput.class, POTENTIOMETER_NAME);

        //TODO add reverse setting for servo
        atlasServo.setDirection(DcMotorSimple.Direction.REVERSE);

//        SERVO_START_POWER = confMgr.getDouble(this, "SERVO_END_POWER");
//        ATLAS_START_ANGLE = confMgr.getDouble(this, "ATLAS_START_ANGLE");
//        ATLAS_END_ANGLE = confMgr.getDouble(this, "ATLAS_END_ANGLE");
//        double SERVO_START_ANGLE = confMgr.getDouble(this, "SERVO_START_ANGLE");
//        double SERVO_END_ANGLE = confMgr.getDouble(this, "SERVO_END_ANGLE");

        POTENTIOMETER_START_ANGLE_DEG = confMgr.getDouble(this, "POTENTIOMETER_START_ANGLE_DEG");
        POTENTIOMETER_END_ANGLE_DEG = confMgr.getDouble(this, "POTENTIOMETER_END_ANGLE_DEG");
        POTENTIOMETER_START_VOLTAGE = confMgr.getDouble(this, "POTENTIOMETER_START_VOLTAGE");
        POTENTIOMETER_END_VOLTAGE = confMgr.getDouble(this, "POTENTIOMETER_END_VOLTAGE");
        updatePotentiometer();

        EPSILON = confMgr.getDouble(this, "EPSILON");

        //TODO find min and max power, save them, and simplify the calculations
    }

    //TODO use the calibrateAtlas() function in a <B>ConfigureAndCalibrate<//B> OpMode.
    public void calibrateAtlas() {
        updatePotentiometer();
    }

    private double updatePotentiometer() {
        return POTENTIOMETER_VOLTAGE_TO_ANGLE_DEG
                = (POTENTIOMETER_END_ANGLE_DEG - POTENTIOMETER_START_ANGLE_DEG) /
                  (POTENTIOMETER_END_VOLTAGE - POTENTIOMETER_START_VOLTAGE);
    }

    public double voltageToAngle_deg(double voltage){
        return POTENTIOMETER_VOLTAGE_TO_ANGLE_DEG * (voltage - POTENTIOMETER_START_VOLTAGE)
//        + POTENTIOMETER_START_ANGLE_DEG //commented out because it is currently 0
                ;
    }

    public double getCurrentAngle_deg(){
        double vvv = (int) (atlasPotentiometer.getVoltage() * 1000) / 1000.;
        System.out.println("AtAtAt voltage = "+ vvv + "current Angle= "+voltageToAngle_deg(vvv));
        return voltageToAngle_deg((int)(atlasPotentiometer.getVoltage()*1000)/1000.) - POTENTIOMETER_OFFSET_ANGLE;
    }

    public void gazeUpTo_deg(double targetTheta) {
        this.targetTheta_deg = targetTheta;
        //TODO complete the calculations after getting feedback and power and angle ranges

    }

    /**
     *
     * @param dTheta in the range [-180,180], effectively [0, 90]
     */
    public void gazeUpBy_deg(double dTheta) {
        gazeUpTo_deg(targetTheta_deg + dTheta);
    }

    public void start() {
        this.continousMovementThread.startThread();
    }
}
