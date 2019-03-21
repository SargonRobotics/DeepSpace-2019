/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.commands.ExtendHatchCover;
import frc.robot.commands.GrabHatchCover;
import frc.robot.commands.RunCargo;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI
{
  Joystick driverJoystick, coJoystick;
  JoystickButton cargoButton, grabButton, extendButton;
  JoystickButton coCargoButton, coGrabButton, coExtendButton;

  public OI()
  {
    // Configured new button, added button to activate cargo motors
    // These should be constants but I don't honestly give enough of a fuck so leave me alone
    driverJoystick = new Joystick(0);
    coJoystick = new Joystick(1);

    grabButton = new JoystickButton(driverJoystick, RobotMap.grabButton);
    extendButton = new JoystickButton(driverJoystick, RobotMap.extendButton);
    cargoButton = new JoystickButton(driverJoystick, RobotMap.cargoButton);

    coGrabButton = new JoystickButton(coJoystick, RobotMap.coGrabButton);
    coExtendButton = new JoystickButton(coJoystick, RobotMap.coExtendButton);
    coCargoButton = new JoystickButton(coJoystick, RobotMap.coCargoButton);

    grabButton.whenPressed(new GrabHatchCover());
    extendButton.whenPressed(new ExtendHatchCover());
    cargoButton.whileHeld(new RunCargo());

    // Will this work?
    // Please help me
    coGrabButton.whenPressed(new GrabHatchCover());
    coExtendButton.whenPressed(new ExtendHatchCover());
    coCargoButton.whenPressed(new RunCargo());
  }

  public double getAxis(int port)
  {
    return deadzone(driverJoystick.getRawAxis(port));
  }

  // Deadzone method for bad controller joysticks
  private double deadzone(double axis)
  {    
    // If it is outside of the deadzone but a negative value, it returns the function with a changed slope
    if(axis < -RobotMap.deadzone && axis < 0)
    {
      return (1/(1 - RobotMap.deadzone) * axis) + RobotMap.deadzone;
    }
    // If it is outside of the deadzone but a positive value, it returns the funtion with a changed slope
    else if(axis > RobotMap.deadzone && axis > 0)
    {
      return (1/(1 - RobotMap.deadzone) * axis) - RobotMap.deadzone;
    }
    // If it is neither, than return 0
    else
    {
      return 0;
    }
  }
}
