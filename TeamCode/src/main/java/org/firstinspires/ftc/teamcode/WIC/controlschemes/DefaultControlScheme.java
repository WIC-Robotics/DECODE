package org.firstinspires.ftc.teamcode.WIC.controlschemes;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.WIC.brain.Midbrain;
import org.firstinspires.ftc.teamcode.WIC.util.ConfMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents first ergonomic trial.<br>
 * There are 2 subclasses of the #ErgonomicControlScheme,
 * namely {@link RightHandedDefaultControlScheme} and {@link LeftHandedDefaultControlScheme}. Both
 * are implemented as inner classes of ErgonomicControlScheme that implement either
 * {@link TwoHandsControl.RightHandedControl} or
 * {@link TwoHandsControl.LeftHandedControl} respectively.
 * The control goes this way:
 * <table>
 *     <tr> <td>toggle intake</td> <td>B/Circle</td> </tr>
 *     <tr> <td>shoot</td> <td>A/Cross</td> </tr>
 *     <tr> <td>translate</td> <td>Recessive Joystick Y</td> </tr>
 *     <tr> <td>rotate</td> <td>Recessive Joystick x</td> </tr>
 *     <tr> <td>manual head control</td> <td>Dominant & Recessive bumpers</td> </tr>
 *     <tr> <td>manual flywheel control</td> <td>Recessive trigger</td> </tr>
 *     <tr> <td>manual flywheel lock</td> <td>Recessive joystick button</td> </tr>
 *     <tr> <td>manual hood control</td> <td>Dominant trigger</td> </tr>
 *     <tr> <td>manual hood lock</td> <td>Dominant joystick button</td> </tr>
 * </table>
 */
public abstract class DefaultControlScheme extends ControlScheme implements TwoHandsControl{

    private static final Logger log = LoggerFactory.getLogger(DefaultControlScheme.class);
    final double maxHoodAngle = 28;
    final int maxFlywheelRPM = 3500;

    boolean intakeNotAlreadyPressed = true;

    boolean shootNotAlreadyPressed = true;

    boolean turnAxisLeft = true;
    boolean turnAxisRight = true;


    /**
     * @param gamepad          for input. User's hand gestures.
     * @param midbrain         for controlling the robot
     * @param confMgr          for input/output. to keep track of the settings.
     */
    @Override
    public boolean control(Gamepad gamepad, Midbrain midbrain, ConfMgr confMgr) {
        if (! super.control(gamepad, midbrain, confMgr))
            return false;

        if (gamepad.aWasPressed()) {
            if(shootNotAlreadyPressed){
                midbrain.shoot();
                shootNotAlreadyPressed = false;
            }
        }
        if (gamepad.aWasReleased()) {
            shootNotAlreadyPressed = true;
        }
        if (gamepad.bWasPressed()) {
            if (intakeNotAlreadyPressed){
                midbrain.toggleIntake();
                intakeNotAlreadyPressed = false;
            }
        }
        if (gamepad.bWasReleased()) {
            intakeNotAlreadyPressed = true;
        }

        double axisOffset = Math.toRadians(1);
        if (gamepad.left_bumper) {
            midbrain.turnHeadBy(axisOffset);
        } else if (gamepad.right_bumper) {
            midbrain.turnHeadBy(-axisOffset);
        }


        double translation = -getRecessiveStickX(gamepad, this);
        double rotation = -getRecessiveStickY(gamepad, this);
        midbrain.drive(translation, rotation, 0);

        boolean unlockShooter = getDominantStickButton(gamepad);
        if (unlockShooter) {
            midbrain.speedupTo((int) (maxFlywheelRPM * getRecessiveTrigger(gamepad)));
            midbrain.gazeUpTo_deg(maxHoodAngle * getDominantTrigger(gamepad));
        }else{
//            midbrain.gazeUpTo_deg();
        }

        return true;
    }

    public static class RightHandedDefaultControlScheme extends DefaultControlScheme implements RightHandedControl {}
    public static class LeftHandedDefaultControlScheme extends DefaultControlScheme implements LeftHandedControl {}
}
