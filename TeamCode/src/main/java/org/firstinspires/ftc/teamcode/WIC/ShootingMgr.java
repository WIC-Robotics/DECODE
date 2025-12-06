package org.firstinspires.ftc.teamcode.WIC;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.AtlasMgr;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.WheelMgr;
import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;

public class ShootingMgr {

    private Servo hood;
    private DcMotor flywheel;

    private AtlasMgr atlasMgr;
    private WheelMgr wheelMgr;
    public ShootingMgr(HardwareMap hardwareMap) {
        this.hood = hardwareMap.get(Servo.class, "hood");
        this.flywheel = hardwareMap.get(DcMotor.class, "flywheel");

        this.atlasMgr = new AtlasMgr(hood);
        this.wheelMgr = new WheelMgr(flywheel);
    }
}
