package org.firstinspires.ftc.teamcode.WIC.brain;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;


@TeleOp(name = "TeleOp")
public class Cortex extends OpMode {

    Midbrain midbrain = null;
    private boolean notStarted = true;

    @Override
    public void init() {
        this.midbrain = new Midbrain(hardwareMap, telemetry);
        this.midbrain.startStreaming();
    }

    @Override
    public void loop() {
        if(notStarted){
            this.midbrain.start();
            notStarted = false;
        }

        //TODO receive user's input
        Gamepad gp = ConfMgr.getInstance().getInt(this, "mainGamepadUser") == 0 ? gamepad1 : gamepad2;



        telemetry.update();

    }

    @Override
    public void stop() {
        this.midbrain.stop();
        this.midbrain = null;
        super.stop();
    }
}
