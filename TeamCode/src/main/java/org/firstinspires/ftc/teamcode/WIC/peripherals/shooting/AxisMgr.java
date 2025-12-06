package org.firstinspires.ftc.teamcode.WIC.peripherals.shooting;

import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**This class manages the neck left and right movement as if the lazy suzan is looking right or left.
 * The name is analogous to the <B>Axis vertebrum (C2)</B> which articulates with the Atlas vertebum
 * and allows it to look left and right.
 */
public class AxisMgr {

    DcMotor axis;
    RevTouchSensor minBound;
    RevTouchSensor maxBound;

    HardwareMap hardwareMap;

    public AxisMgr(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;

        axis = this.hardwareMap.get(DcMotor.class, "Axis");
        minBound = this.hardwareMap.get(RevTouchSensor.class, "MaxBound");
        maxBound = this.hardwareMap.get(RevTouchSensor.class, "MinBound");
    }

//    public void


}
