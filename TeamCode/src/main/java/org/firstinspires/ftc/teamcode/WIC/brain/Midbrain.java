package org.firstinspires.ftc.teamcode.WIC.brain;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.WIC.ShootingMgr;

public class Midbrain {

    private HardwareMap hardwareMap;

    ShootingMgr shootingMgr;

    public Midbrain(HardwareMap hardwareMap, ShootingMgr shootingMgr) {
        this.hardwareMap = hardwareMap;
        this.shootingMgr = new ShootingMgr(hardwareMap);
    }



}

