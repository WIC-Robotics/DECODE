package org.firstinspires.ftc.teamcode.WIC.brain;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.concurrent.FutureTask;


@Autonomous(name = "Autonomous FAR")
public class AutonomousNS extends OpMode {
    Midbrain midbrain = null;

    int EPSILON_TIME_MILLIS = 5;

    private long start_time;
    private int currentState = 0;

    @Override
    public void init() {
        this.midbrain = new Midbrain(hardwareMap, telemetry);
        this.midbrain.startStreaming();
    }

    @Override
    public void start() {
        super.start();
        this.midbrain.start();
        midbrain.speedupTo(2700);
        midbrain.gazeUpTo_deg(29);
        resetRuntime();
        System.out.println("STARTING AT: " + System.currentTimeMillis());
        start_time = System.currentTimeMillis();
        midbrain.toggleIntake();
    }

    @Override
    public void loop() {
        long currentTimeMillis = System.currentTimeMillis() - start_time;
        System.out.println(currentTimeMillis);
        if (currentState == 0 && currentTimeMillis - 8e3 > EPSILON_TIME_MILLIS) {
            midbrain.shoot();
            midbrain.shoot();
            currentState++;
            return;
        }
        if (currentState == 1 && currentTimeMillis - 10e3 > EPSILON_TIME_MILLIS) {
            midbrain.shoot();
            midbrain.shoot();
            currentState++;
            return;
        }
        if (currentState == 2 && currentTimeMillis - 12e3 > EPSILON_TIME_MILLIS) {
            midbrain.shoot();
            midbrain.shoot();
            currentState++;
            return;
        }
        if (currentState == 3 && currentTimeMillis - 14e3 > EPSILON_TIME_MILLIS) {
            midbrain.drive(1., 0, 0);
            currentState++;
            return;
        }
        if (currentState == 4 && currentTimeMillis - 15e3 > EPSILON_TIME_MILLIS) {
            midbrain.drive(0, 0, 0);
            currentState++;
            return;
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
