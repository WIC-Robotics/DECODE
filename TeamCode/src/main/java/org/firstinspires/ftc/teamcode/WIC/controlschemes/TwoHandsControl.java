package org.firstinspires.ftc.teamcode.WIC.controlschemes;

import com.qualcomm.robotcore.hardware.Gamepad;

public interface TwoHandsControl {

    float getDominantStickX(Gamepad gamepad, ControlScheme controlScheme);

    float getDominantStickY(Gamepad gamepad, ControlScheme controlScheme);
    boolean getDominantStickButton(Gamepad gamepad);

    float getDominantTrigger(Gamepad gamepad);

    boolean getDominantBumper(Gamepad gamepad);

    float getRecessiveStickX(Gamepad gamepad, ControlScheme controlScheme);

    float getRecessiveStickY(Gamepad gamepad, ControlScheme controlScheme);
    boolean getRecessiveStickButton(Gamepad gamepad);

    float getRecessiveTrigger(Gamepad gamepad);

    boolean getRecessiveBumper(Gamepad gamepad);


    interface RightHandedControl extends TwoHandsControl {
        @Override
        default float getDominantStickX(Gamepad gamepad, ControlScheme controlScheme){
            return gamepad.right_stick_x - controlScheme.rightXBias;
        }
        @Override
        default float getDominantStickY(Gamepad gamepad, ControlScheme controlScheme){
            return gamepad.right_stick_y - controlScheme.rightYBias;
        }
        @Override
        default float getDominantTrigger(Gamepad gamepad){
            return gamepad.right_trigger;
        }
        @Override
        default boolean getDominantBumper(Gamepad gamepad){
            return gamepad.right_bumper;
        }
        @Override
        default float getRecessiveStickX(Gamepad gamepad, ControlScheme controlScheme){
            return gamepad.left_stick_x - controlScheme.leftXBias;
        }

        @Override
        default boolean getDominantStickButton(Gamepad gamepad) {
            return gamepad.right_stick_button;
        }

        @Override
        default float getRecessiveStickY(Gamepad gamepad, ControlScheme controlScheme){
            return gamepad.left_stick_y - controlScheme.leftYBias;
        }

        @Override
        default boolean getRecessiveStickButton(Gamepad gamepad) {
            return gamepad.left_stick_button;
        }

        @Override
        default float getRecessiveTrigger(Gamepad gamepad){
            return gamepad.left_trigger;
        }
        @Override
        default boolean getRecessiveBumper(Gamepad gamepad){
            return gamepad.left_bumper;
        }
    }

    interface LeftHandedControl extends TwoHandsControl {
        @Override
        default float getDominantStickX(Gamepad gamepad, ControlScheme controlScheme){
            return gamepad.left_stick_x - controlScheme.leftXBias;
        }
        @Override
        default float getDominantStickY(Gamepad gamepad, ControlScheme controlScheme){
            return gamepad.left_stick_y - controlScheme.leftYBias;
        }

        @Override
        default boolean getDominantStickButton(Gamepad gamepad) {
            return gamepad.left_stick_button;
        }

        @Override
        default float getDominantTrigger(Gamepad gamepad){
            return gamepad.left_trigger;
        }
        @Override
        default boolean getDominantBumper(Gamepad gamepad){
            return gamepad.left_bumper;
        }
        @Override
        default float getRecessiveStickX(Gamepad gamepad, ControlScheme controlScheme){
            return gamepad.right_stick_x - controlScheme.rightXBias;
        }
        @Override
        default float getRecessiveStickY(Gamepad gamepad, ControlScheme controlScheme){
            return gamepad.right_stick_y  - controlScheme.rightYBias;
        }

        @Override
        default boolean getRecessiveStickButton(Gamepad gamepad) {
            return gamepad.right_stick_button;
        }

        @Override
        default float getRecessiveTrigger(Gamepad gamepad){
            return gamepad.right_trigger;
        }
        @Override
        default boolean getRecessiveBumper(Gamepad gamepad){
            return gamepad.right_bumper;
        }
    }
}
