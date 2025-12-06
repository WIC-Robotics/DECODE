package org.firstinspires.ftc.teamcode.WIC.peripherals.shooting;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;

/**This class manages the hood up and down movement as if the robot is gazing up or down.
 * The name is analogous to the <B>Atlas vertebrum (C1)</B> which carries the skull and allows it to
 * look up and down.
 */
public class AtlasMgr {

    Servo atlasServo;

    private double SERVO_START_POWER = 0;
    private double ATLAS_START_ANGLE = 0;

    private double atlasToServoGearRatio = 0;

    //TODO create a <B>ConfigureAndCalibrate</B> activity. Create a calibrateAtlas() function in it.

    public AtlasMgr(Servo atlasServo) {
        this.atlasServo = atlasServo;

        ConfMgr confMgr = ConfMgr.getInstance();
        SERVO_START_POWER = confMgr.getDouble(this, "SERVO_START_POWER");
        double SERVO_END_POWER = confMgr.getDouble(this, "SERVO_END_POWER");
        double SERVO_START_ANGLE = confMgr.getDouble(this, "SERVO_START_ANGLE");
        double SERVO_END_ANGLE = confMgr.getDouble(this, "SERVO_END_ANGLE");
        ATLAS_START_ANGLE = confMgr.getDouble(this, "ATLAS_START_ANGLE");
        double ATLAS_END_ANGLE = confMgr.getDouble(this, "ATLAS_END_ANGLE");
        //TODO find min and max power, save them, and simplify the calculations
        atlasToServoGearRatio = (SERVO_END_ANGLE - SERVO_START_ANGLE) / (ATLAS_END_ANGLE - ATLAS_START_ANGLE);
    }

    public void gazeUpTo(double theta) {
        //TODO complete the calculations after getting feedback and power and angle ranges
        double servoPowerFromStart = (theta - ATLAS_START_ANGLE) * atlasToServoGearRatio;
        double servoPower = servoPowerFromStart + SERVO_START_POWER;
        atlasServo.setPosition(servoPower);
    }

    /**
     *
     * @param dTheta in the range [-180,180], effectively [0, 90]
     */
    public void gazeUpBy(double dTheta) {
        gazeUpTo(dTheta - atlasServo.getPosition());
    }
}
