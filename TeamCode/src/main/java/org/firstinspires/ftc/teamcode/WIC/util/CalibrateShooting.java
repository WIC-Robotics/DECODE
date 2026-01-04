package org.firstinspires.ftc.teamcode.WIC.util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.AtlasMgr;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.ShootingExceptionHandler;
import org.firstinspires.ftc.teamcode.WIC.peripherals.shooting.WheelMgr;

import java.util.ArrayList;

@TeleOp(name = "Calibrate Shooting", group = "Calibration")
public class CalibrateShooting extends OpMode implements ShootingExceptionHandler {

    String[] optionNames = {
            "hood angle",
            "flywheel rpm",
            "rpm factor",
    };
    ArrayList<Object[]> alternatives = new ArrayList<Object[]>();
    private boolean tooFar;
    private boolean tooClose;

    {
        alternatives.add(new Object[]{0.f, 5.f, 10.f, 15.f, 20.f, 25.f, 29.f});
        alternatives.add(new Object[]{0, 250, 500, 750, 1000, 1250, 1500, 1750, 2000, 2250, 2500, 2750, 3000,
                3250, 3500, 3750, 4000, 4250, 4500, 4750, 5000, 5250, 5500, 5750, 6000, 6250, 6500, 6750, 7000});
        alternatives.add(new Object[]{0.2, 0.25, 0.33, 0.5, 0.66, 0.75, 0.8, 1.});
    }

    int optionIdx = 0;
    int[] selectedAlternatives = {0, 0, 1};
    int currentOptionAlternativeIdx = selectedAlternatives[optionIdx];


    AtlasMgr atlas;
    WheelMgr flywheel;

    @Override
    public void init() {
        AppContextProvider.setAppContext(hardwareMap.appContext);
        atlas = new AtlasMgr(hardwareMap);
        flywheel = new WheelMgr(hardwareMap, this);

        atlas.start();
        flywheel.start();
    }

    @Override
    public void loop() {
        Gamepad gp = gamepad1;

        if (gp.yWasReleased()) {
            optionIdx--;
        } else if (gp.aWasReleased()) {
            optionIdx++;
        }

        optionIdx += optionNames.length;
        optionIdx %= optionNames.length;

        currentOptionAlternativeIdx = selectedAlternatives[optionIdx];

        if (gp.bWasReleased()) {
            currentOptionAlternativeIdx++;
        } else if (gp.xWasReleased()) {
            currentOptionAlternativeIdx--;
        }
        int alternativesLength = alternatives.get(optionIdx).length;
        currentOptionAlternativeIdx += alternativesLength;
        currentOptionAlternativeIdx %= alternativesLength;
        selectedAlternatives[optionIdx] = currentOptionAlternativeIdx;

        int temp = -1;
        float hoodAngle = (float) alternatives.get(++temp)[selectedAlternatives[temp]];
        int flywheelSpeed = (int) alternatives.get(++temp)[selectedAlternatives[temp]];
        double rpmFactor = (double) alternatives.get(++temp)[selectedAlternatives[temp]];

        flywheel.setRPM_ADJUSTMENT_FACTOR(rpmFactor);

        telemetry.clearAll();
        telemetry.addData("SELECTION is", optionNames[optionIdx]);
        telemetry.addLine("--------------");
        telemetry.addData("Target  Hood Angle", hoodAngle);
        telemetry.addData("Current Hood Angle", atlas.getCurrentAngle_deg());
        telemetry.addLine();
        telemetry.addData("Target  Flywheel RPM", flywheelSpeed);
        telemetry.addData("Current Flywheel RPM", flywheel.getCurrentSpeedRPM());
        telemetry.addLine();
        telemetry.addData("RPM Adjustment Factor", rpmFactor);
        telemetry.addLine();
        if (tooFar){
            telemetry.addLine("TOO FAR");
            tooFar = false;
        }
        if (tooClose){
            telemetry.addLine("TOO CLOSE");
            tooClose = false;
        }

        atlas.gazeUpTo_deg(hoodAngle);
        flywheel.speedupTo(flywheelSpeed);

        telemetry.update();
//        Thread.yield();
    }

    @Override
    public void stop() {
        flywheel.stop();
    }

    @Override
    public void robotTooClose() {
        this.tooClose = true;
    }

    @Override
    public void robotTooFar() {
        this.tooFar = true;
    }
}