/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.commands.ToggleHatchCover;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI
{
  Joystick joystick;
  JoystickButton hatchButton;

  public OI()
  {
    joystick = new Joystick(0);
    hatchButton = new JoystickButton(joystick, RobotMap.hatchButton);

    hatchButton.toggleWhenPressed(new ToggleHatchCover());
  }
}
