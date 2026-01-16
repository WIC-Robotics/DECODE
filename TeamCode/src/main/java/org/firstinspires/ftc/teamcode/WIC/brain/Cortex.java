package org.firstinspires.ftc.teamcode.WIC.brain;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.WIC.controlschemes.ControlScheme;
import org.firstinspires.ftc.teamcode.WIC.controlschemes.DefaultControlScheme;
import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;


@TeleOp(name = "TeleOp")
public class Cortex extends OpMode {

    Midbrain midbrain = null;
    ConfMgr confMgr;

    Gamepad gpGamepad;
    private ControlScheme controlScheme;

    @Override
    public void init() {
        this.midbrain = new Midbrain(hardwareMap, telemetry);
        this.midbrain.startStreaming();

        confMgr = ConfMgr.getInstance();
        gpGamepad = confMgr.getInt(this, "mainGamepadUser") == 0 ? gamepad1 : gamepad2;
        controlScheme = new DefaultControlScheme.RightHandedDefaultControlScheme();
        Object alliance = blackboard.get("Alliance");
        if (alliance == null) {
            throw new RuntimeException("SET THE ALLIANCE COLOR USING THE SET ALLIANCE OpMode");
        }
        midbrain.setAlliance(alliance.toString());
    }

    @Override
    public void start() {
        super.start();
        this.midbrain.start();
    }

    @Override
    public void loop() {
        controlScheme.control(gpGamepad, midbrain, confMgr);


        telemetry.update();

    }

    @Override
    public void stop() {
        this.midbrain.stop();
        this.midbrain = null;
        super.stop();
    }
}
