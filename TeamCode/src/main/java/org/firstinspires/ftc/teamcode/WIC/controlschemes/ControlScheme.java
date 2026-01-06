package org.firstinspires.ftc.teamcode.WIC.controlschemes;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.WIC.brain.Midbrain;

public abstract class ControlScheme {

    protected float leftXBias = 0;
    protected float leftYBias = 0;
    protected float rightXBias = 0;
    protected float rightYBias = 0;

    /**
     * This function takes all decisions related to robot actions based on inputs.<br>
     * The inputs come from an {@code OpMode}, specifically {@link org.firstinspires.ftc.teamcode.WIC.brain.Cortex}. <br>
     * It returns a boolean that indicates whether to continue processing the inputs or not.
     *
     * @param gamepad          for input. User's hand gestures.
     * @param midbrain         for controlling the robot
     * @param confMgr          for input/output. to keep track of the settings.
     * @return {@code false} if no more control processing should be done, {@code true} otherwise.
     */
    public boolean control(Gamepad gamepad, Midbrain midbrain, org.firstinspires.ftc.teamcode.WIC.util.ConfMgr confMgr) {
        if (gamepad.a && gamepad.b && gamepad.x && gamepad.y) {
            setAllBiases(gamepad.left_stick_x, gamepad.left_stick_y, gamepad.right_stick_x, gamepad.right_stick_y);
            return false;
        }
//        boolean rightButton = gamepad.right_stick_button;
//        boolean leftButton = gamepad.left_stick_button;
//        if(rightButton){
//            midbrain.shoot();
//        }
//        if (leftButton) {
//                ascentController.descend();
//        }
//        if (gamepad.a) {
//            telemetry.addData("current shoulder angle: ", armController.getAngle());
//            telemetry.addData("Potentiometer voltage : ", armController.getVoltage());
//            telemetry.addData("the angleSetPoint is: ", armController.shoulderTargetAngle);
//            telemetry.addData("the current encoder TICKS OF THE SLIDER is: ", armController.slider.getCurrentPosition());
//            telemetry.addData("the base extension of the claw", armController.getExtensionInches());
//            telemetry.addData("this is the number of ticks moved: ", armController.getTicksPerInches());
//            telemetry.addData("the button is pressed: ", armController.isButtonPressed());
//        }
        return true;
    }

    public void setAllBiases(float leftXBias, float leftYBias, float rightXBias, float rightYBias) {
        this.leftXBias = leftXBias;
        this.leftYBias = leftYBias;
        this.rightXBias = rightXBias;
        this.rightYBias = rightYBias;
    }
}
