package org.firstinspires.ftc.teamcode.WIC;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.HoodMgr;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.WheelMgr;

public class ShootingMgr {

    private Servo hood;
    private DcMotor flywheel;

    private HoodMgr hoodMgr;
    private WheelMgr wheelMgr;
    public ShootingMgr(HardwareMap hardwareMap) {
        this.hood = hardwareMap.get(Servo.class, "hood");
        this.flywheel = hardwareMap.get(DcMotor.class, "flywheel");

        this.hoodMgr = new HoodMgr(hood);
        this.wheelMgr = new WheelMgr(flywheel);
    }
}
