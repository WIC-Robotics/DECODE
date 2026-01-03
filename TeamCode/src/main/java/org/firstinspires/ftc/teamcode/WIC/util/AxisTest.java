package org.firstinspires.ftc.teamcode.WIC.util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.AxisExceptionHandler;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.AxisMgr;


@TeleOp(name = "Axis Test", group = "Tests")
public class AxisTest extends OpMode implements AxisExceptionHandler {

    AxisMgr axisMgr;
    long startTime = System.currentTimeMillis();

    @Override
    public void init() {
        AppContextProvider.setAppContext(hardwareMap.appContext);
        axisMgr = new AxisMgr(hardwareMap, this);
    }

    @Override
    public void loop() {
        long currentTime = System.currentTimeMillis();
        int currentSecond = Math.toIntExact((currentTime - startTime) / 1000);
        double targetAngle;
//        double sign = currentSecond /10 % 2 == 0 ? 1. : -1.;
        targetAngle = Math.round(currentSecond / 10.) * /*sign * */1000;
        System.out.printf("TestTest time = %d targetAngle = %f\n", currentSecond, targetAngle);
        axisMgr.turnHeadTo(Math.toRadians(targetAngle));
    }

    @Override
    public void stop() {
        axisMgr.stop();
        super.stop();
    }

    @Override
    public void cannotTurnLeft() {
        System.out.println("can't turn left");
    }

    @Override
    public void cannotTurnRight() {
        System.out.println("can't turn right");
    }
}
