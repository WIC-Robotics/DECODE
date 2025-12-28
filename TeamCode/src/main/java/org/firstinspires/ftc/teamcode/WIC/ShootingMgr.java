package org.firstinspires.ftc.teamcode.WIC;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.AtlasMgr;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.AxisMgr;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.HeadExceptionHandler;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.WheelMgr;
import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;
import org.firstinspires.ftc.teamcode.WIC.util.OutputHandler;

public class ShootingMgr {
    private final boolean throwErrors;

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

        this.throwErrors = Boolean.parseBoolean(ConfMgr.getInstance().get(this, "throwErrors"));

        try {
            this.atlasMgr = new AtlasMgr(hardwareMap);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.ERR, "can't create AtlasMgr!");
            if (throwErrors) {
                throw e;
            }
        }
        try {
            this.axisMgr = new AxisMgr(hardwareMap, headExceptionHandler);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.ERR, "can't create AxisMgr!");
            if (throwErrors) {
                throw e;
            }
        }
        try {
            this.wheelMgr = new WheelMgr(hardwareMap, headExceptionHandler);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.ERR, "can't create WheelMgr!");
            if (throwErrors) {
                throw e;
            }
        }
    }

//    public void gazeUpTo(double theta) {
//        atlasMgr.gazeUpTo(theta);
//    }
//
//    public void gazeUpBy(double dTheta) {
//        atlasMgr.gazeUpBy(dTheta);
//    }

    public void aim(double r, double theta) {
        if (axisMgr != null) {
            axisMgr.turnHeadTo(theta);
        }

        //TODO find the gazeUp and wheelSpeed best for r, guided by PatternTracker and AxisManager
        double targetThetaUp = r, targetSpeed= r*r; //TODO temp values
        //TODO ***** Find the correct combination for targetThetaUp and targetSpeed ️ ******
        if (atlasMgr != null) {
            atlasMgr.gazeUpTo_deg(targetThetaUp);
        }

        if(!ConfMgr.isTesting()){
            wheelMgr.speedupTo(targetSpeed); //TODO revert this temp
        }

        //NOTE: if the axis fails (e.g. motor cable cut), this mat result in both
        // cannot_turn_left and cannot_turn_right

        // if can't reach enough distance
        //    ret |= NEED_TO_MOVE_CLOSER;
        // else if too close to shoot
        //    ret |= NEED_TO_MOVE_FURTHER;
    }

    public void shoot() {
        //TODO turn rubber intake
    }

    public void stop() {
        if(atlasMgr != null){
            atlasMgr.stop();
            atlasMgr = null;
        }
        if (axisMgr != null) {
            axisMgr.stop();
            axisMgr = null;
        }
        if (wheelMgr != null) {
            wheelMgr.stop();
            wheelMgr = null;
        }
    }
}
