package org.firstinspires.ftc.teamcode.WIC.brain;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.concurrent.FutureTask;


@Autonomous(name = "Autonomous NEAR")
public class AutonomousNS2 extends OpMode {
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
        midbrain.speedupTo(1000);
        midbrain.gazeUpTo_deg(15);
        resetRuntime();
        System.out.println("STARTING AT: " + System.currentTimeMillis());
        start_time = System.currentTimeMillis();
        midbrain.toggleIntake();
    }

    @Override
    public void loop() {
        //recede until you see the AprilTag, shoot, shoot, shoot, recede more.
        long currentTimeMillis = System.currentTimeMillis() - start_time;
        System.out.println(currentTimeMillis);
        if (currentState == 0 && currentTimeMillis - 1.e3 > EPSILON_TIME_MILLIS) {
            midbrain.drive(-1., 0, 0);
            currentState++;
            return;
        }
        if (currentState == 1 && currentTimeMillis - 3e3 > EPSILON_TIME_MILLIS) {
            midbrain.drive(0, 0, 0);
            currentState++;
            return;
        }
        if (currentState == 2 && currentTimeMillis - 11e3 > EPSILON_TIME_MILLIS) {
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            currentState++;
            return;
        }
        if (currentState == 2 && currentTimeMillis - 13e3 > EPSILON_TIME_MILLIS) {
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            currentState++;
            return;
        }
        if (currentState == 2 && currentTimeMillis - 15e3 > EPSILON_TIME_MILLIS) {
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            midbrain.shoot();
            currentState++;
            return;
        }
        if (currentState == 3 && currentTimeMillis - 17.e3 > EPSILON_TIME_MILLIS) {
            midbrain.drive(-1., 0, 0);
            currentState++;
            return;
        }
        if (currentState == 4 && currentTimeMillis - 18.e3 > EPSILON_TIME_MILLIS) {
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
