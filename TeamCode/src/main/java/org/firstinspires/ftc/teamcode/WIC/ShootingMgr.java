package org.firstinspires.ftc.teamcode.WIC;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.AtlasMgr;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.AxisMgr;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.HeadExceptionHandler;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.IntakeMgr;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.WheelMgr;
import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;
import org.firstinspires.ftc.teamcode.WIC.util.OutputHandler;

import java.util.Random;

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
    private IntakeMgr intakeMgr;

    public ShootingMgr(HardwareMap hardwareMap, HeadExceptionHandler headExceptionHandler, OutputHandler outputHandler, boolean throwErrors) {
        this.headExceptionHandler = headExceptionHandler;
        this.outputHandler = outputHandler;

        this.throwErrors = throwErrors;

        try {
            this.atlasMgr = new AtlasMgr(hardwareMap);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.ERR, "can't create AtlasMgr!");
            if (this.throwErrors) {
                throw e;
            }
        }
        try {
            this.axisMgr = new AxisMgr(hardwareMap, headExceptionHandler);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.ERR, "can't create AxisMgr!");
            if (this.throwErrors) {
                throw e;
            }
        }
        try {
            this.wheelMgr = new WheelMgr(hardwareMap, headExceptionHandler);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.ERR, "can't create WheelMgr!");
            if (this.throwErrors) {
                throw e;
            }
        }
        try {
            this.intakeMgr = new IntakeMgr(hardwareMap);
        } catch (Exception e) {
            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.ERR, "can't create IntakeMgr!");
            if (this.throwErrors) {
                throw e;
            }
        }
//        try {
//            this.intakeMgr = new IntakeMgr(hardwareMap);
//        } catch (Exception e) {
//            outputHandler.writeToTelemetry(OutputHandler.MSG_LEVEL.ERR, "can't create IntakeMgr!");
//            if (this.throwErrors) {
//                throw e;
//            }
//        }

    }

//    public void gazeUpTo(double theta) {
//        atlasMgr.gazeUpTo(theta);
//    }
//
//    public void gazeUpBy(double dTheta) {
//        atlasMgr.gazeUpBy(dTheta);
//    }

    public double getCurrentHeadPositionRad() {
        return axisMgr.getCurrentHeadPositionRad();
    }

    public void turnHeadBy(double dTheta) {
        axisMgr.turnHeadBy(dTheta);
    }

    public void aim(double r, double theta) {
        if (axisMgr != null) {
            axisMgr.turnHeadTo(theta);
        }

//        System.out.println("r = .............." + r);
        if (r < 0)
            return;
        //TODO find the gazeUp and wheelSpeed best for r, guided by PatternTracker and AxisManager
        double[] combo = findBestWheelAndHoodCombo(r);
        double targetThetaUp = combo[1], targetSpeed = combo[0]; //TODO temp values

        //TODO ***** Find the correct combination for targetThetaUp and targetSpeed ️ ******
        if (atlasMgr != null) {
            atlasMgr.gazeUpTo_deg(targetThetaUp);
        }

        if (wheelMgr != null) {
            wheelMgr.speedupTo((int) targetSpeed);
        }

        //NOTE: if the axis fails (e.g. motor cable cut), this mat result in both
        // cannot_turn_left and cannot_turn_right

        // if can't reach enough distance
        //    ret |= NEED_TO_MOVE_CLOSER;
        // else if too close to shoot
        //    ret |= NEED_TO_MOVE_FURTHER;
    }

    public void shoot() {
        intakeMgr.advance();
    }

    private double[] findBestWheelAndHoodCombo(double r) { //TODO undo temp
        Random random = new Random();
        double wheelSpeed = 4000;
        double hoodAngle = 25;
        if (!ConfMgr.isDemo()) {
            wheelSpeed = random.nextDouble();
            hoodAngle = Math.round(random.nextDouble() * 31);
        }
        return new double[]{wheelSpeed, hoodAngle};
    }

    public void stop() {
        if (atlasMgr != null) {
            System.out.println("going to stop AtlasMgr");
            atlasMgr.stop();
            atlasMgr = null;
        }
        if (axisMgr != null) {
            System.out.println("going to stop AxisMgr");
            axisMgr.stop();
            axisMgr = null;
        }
        if (wheelMgr != null) {
            System.out.println("going to stop WheelMgr");
            wheelMgr.stop();
            wheelMgr = null;
        }
        if (intakeMgr != null) {
            System.out.println("going to stop IntakeMgr");
            intakeMgr.stop();
            intakeMgr = null;
        }
    }

    public void turnHeadTo(double theta) {
        axisMgr.turnHeadTo(theta);
    }

    public void start() {
        if (axisMgr != null) {
            axisMgr.start();
        }
        if (atlasMgr != null) {
            atlasMgr.start();
        }
        if (wheelMgr != null) {
            wheelMgr.start();
        }
        if (intakeMgr != null) {
            intakeMgr.start();
        }
    }

    public void speedupTo(int rpm) {
        wheelMgr.speedupTo(rpm);
    }

    public void gazeUpTo_deg(double targetTheta) {
        atlasMgr.gazeUpTo_deg(targetTheta);
    }

    public void gazeUpBy_deg(double dTheta) {
        atlasMgr.gazeUpBy_deg(dTheta);
    }

    public void toggleIntake() {
        intakeMgr.toggleIntake();
    }

//    public void calibrate() {
//        if (axisMgr != null) {
//            axisMgr.calibrate();
//        }
//        if (atlasMgr != null) {
//            atlasMgr.calibrateAtlas();
//        }
//        if (wheelMgr != null) {
//            wheelMgr.calibrate();
//        }
//        if (intakeMgr != null) {
//            intakeMgr.calibrate();
//        }
//    }
}
