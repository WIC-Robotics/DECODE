package org.firstinspires.ftc.teamcode.WIC.peripherals.movement;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;

public class DeadwheelMgr {

    GoBildaPinpointDriver pinpoint;

    public void stop() {}

    public void start() {
        while (pinpoint.getDeviceStatus() != GoBildaPinpointDriver.DeviceStatus.READY){
            System.out.println("Device status = "+pinpoint.getDeviceStatus()+", waiting for the pinpoint device");
        }
    }

    public DeadwheelMgr(HardwareMap hardwareMap) {
        this.pinpoint = hardwareMap.get(GoBildaPinpointDriver.class,  "pinpoint");
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        ConfMgr confMgr = ConfMgr.getInstance();
        pinpoint.setOffsets(confMgr.getDouble(this, "xOffset"), confMgr.getDouble(this, "yOffset"), DistanceUnit.INCH);
//        pinpoint.resetPosAndIMU();
        pinpoint.initialize();
    }

    //TODO actually implement them lol
    public Pose2D getCurrentPosition() {
        if (pinpoint != null) {
            pinpoint.update();
            return pinpoint.getPosition();
        }
        throw new IllegalStateException("I am impressed u got my code to break, but sadly, I cannot allow that");
    }
}
