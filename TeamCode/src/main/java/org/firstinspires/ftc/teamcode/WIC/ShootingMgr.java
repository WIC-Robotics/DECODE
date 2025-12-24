package org.firstinspires.ftc.teamcode.WIC;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.AtlasMgr;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.AxisMgr;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.HeadExceptionHandler;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.WheelMgr;
import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;
import org.firstinspires.ftc.teamcode.WIC.util.OutputHandler;

public class ShootingMgr {

//    public static final int OK = 0;
//    public static final int NEED_TO_TURN_LEFT = 1;
//    public static final int NEED_TO_TURN_RIGHT = 1<<1;
//    public static final int NEED_TO_MOVE_CLOSER = 1<<2;
//    public static final int NEED_TO_MOVE_FURTHER = 1<<3;

    private OutputHandler outputHandler;
    private HeadExceptionHandler headExceptionHandler;
    private AtlasMgr atlasMgr;
    private WheelMgr wheelMgr;
    private AxisMgr axisMgr;
    public ShootingMgr(HardwareMap hardwareMap, HeadExceptionHandler headExceptionHandler, OutputHandler outputHandler) {
        this.headExceptionHandler = headExceptionHandler;
        this.outputHandler = outputHandler;

        try {
            this.atlasMgr = new AtlasMgr(hardwareMap);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.ERR, "can't create AtlasMgr!");
        }
        try {
            this.wheelMgr = new WheelMgr(hardwareMap);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.ERR, "can't create WheelMgr!");
        }
        try {
            this.axisMgr = new AxisMgr(hardwareMap, headExceptionHandler);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.ERR, "can't create AxisMgr!");
        }
    }

//    public void gazeUpTo(double theta) {
//        atlasMgr.gazeUpTo(theta);
//    }
//
//    public void gazeUpBy(double dTheta) {
//        atlasMgr.gazeUpBy(dTheta);
//    }

    //TODO 🗣️🗣️🗣️🗣️🗣️🗣️🗣️🗣️🗣️ IMPLEMENT IMMEDIATELY 🗣️🗣️🗣️🗣️🗣️🗣️🗣️🗣️🗣️
    public void aim(double r, double theta) {
        if (axisMgr != null) {
            axisMgr.turnHead(theta);
        }
        //TODO find the gazeUp and wheelSpeed best for r, guided by PatternTracker and AxisManager
        double targetThetaUp = 0, targetspeed= 0;
        if (atlasMgr != null) {
            atlasMgr.gazeUpTo(targetThetaUp);
        }
//        wheelMgr.speedupTo(targetspeed); //TODO revert this temp

        //NOTE: if the axis fails (e.g. motor cable cut), this mat result in both
        // cannot_turn_left and cannot_turn_right

        // if can't reach enough distance
        //    ret |= NEED_TO_MOVE_CLOSER;
        // else if too close to shoot
        //    ret |= NEED_TO_MOVE_FURTHER;
    }
}
