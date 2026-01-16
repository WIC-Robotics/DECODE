package org.firstinspires.ftc.teamcode.WIC.brain;

import com.acmerobotics.dashboard.Mutex;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.teamcode.WIC.controlschemes.DefaultControlScheme;
import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;


@Autonomous(name = "Autonomous Script")
public class AutonomousNS extends OpMode {
    Midbrain midbrain = null;

    int EPSILON_TIME_MILLIS = 5;

    private long start_time;

    @Override
    public void init() {
        this.midbrain = new Midbrain(hardwareMap, telemetry);
        this.midbrain.startStreaming();
    }

    @Override
    public void start() {
        super.start();
        this.midbrain.start();
        resetRuntime();
        System.out.println("STARTING AT: " + System.currentTimeMillis());
        start_time = System.currentTimeMillis();
        midbrain.toggleIntake();
    }

    @Override
    public void loop() {
//        doAtTime(midbrain.drive(1, 0, 0), start_time, EPSILON_TIME_MILLIS);
//        doAtTime();
//        doAtTime();
//        doAtTime();
//        doAtTime();
//        doAtTime();
//        doAtTime();
//        doAtTime();
//        doAtTime();
//        doAtTime();
//        doAtTime();
//        doAtTime();
//        doAtTime();

        long currentTimeMillis = System.currentTimeMillis() - start_time;
        System.out.println(currentTimeMillis);
        if (currentTimeMillis - 5000 > EPSILON_TIME_MILLIS) {
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
        }
        if (currentTimeMillis - 8000 > EPSILON_TIME_MILLIS) {
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
        }
        if (currentTimeMillis - 10000 > EPSILON_TIME_MILLIS) {
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
        }
        if (currentTimeMillis - 12000 > EPSILON_TIME_MILLIS) {
            midbrain.drive(.5, 0, 0);
        }
        if (currentTimeMillis - 12250 > EPSILON_TIME_MILLIS) {
            midbrain.drive(0, 0, 0);
        }
    }

    @Override
    public void stop() {
        super.stop();
        midbrain.stop();
    }

    private void doAtTime(FutureTask<Midbrain> function, long targetTimeMillis, int epsilonTime) {
        if (System.currentTimeMillis() - targetTimeMillis <= epsilonTime) {
            function.run();
        }
    }
}
