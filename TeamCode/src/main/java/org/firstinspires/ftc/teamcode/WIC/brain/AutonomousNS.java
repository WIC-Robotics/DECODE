package org.firstinspires.ftc.teamcode.WIC.brain;

import com.acmerobotics.dashboard.Mutex;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Func;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;


@Autonomous(name = "Autonomous Script")
public class AutonomousNS extends OpMode {
    Midbrain midbrain = null;

    int EPSILON_TIME_MILLIS = 5;

    boolean notStarted = false;
    private long start_time;

    @Override
    public void init() {
        midbrain = new Midbrain(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        if(notStarted){
            this.midbrain.start();
            notStarted = false;
            start_time = System.currentTimeMillis();
        }

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

        midbrain.aim(5, 0);

        if (start_time > 1000) {
            midbrain.shoot();
        }
        if (start_time > 3000) {
            midbrain.shoot();
        }
        if (start_time > 5000) {
            midbrain.shoot();
        }
        if (start_time > 7500) {
            midbrain.drive(1, 0, 0);
        }
    }

    private void doAtTime(FutureTask<Midbrain> function, long targetTimeMillis, int epsilonTime) {
        if (System.currentTimeMillis() - targetTimeMillis <= epsilonTime) {
            function.run();
        }
    }
}
