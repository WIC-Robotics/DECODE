package org.firstinspires.ftc.teamcode.WIC.peripherals.shooting;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class WheelMgr {

    DcMotor flywheel;

    public WheelMgr(HardwareMap hardwareMap) {
        this.flywheel = hardwareMap.get(DcMotor.class, "flywheel");
    }
}
