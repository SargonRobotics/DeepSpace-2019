/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class RunCargo extends Command
{
  boolean initialLimitSwitchValue, currentLimitSwitchValue;
  
  public RunCargo() 
  {
    requires(Robot.cargo);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() 
  {
    initialLimitSwitchValue = Robot.cargo.getLimitSwitch();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() 
  {
    Robot.cargo.setCargoMotors();
    currentLimitSwitchValue = Robot.cargo.getLimitSwitch();
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() 
  {
    return (!initialLimitSwitchValue && currentLimitSwitchValue) ? true : false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() 
  {
    Robot.cargo.stopCargoMotors();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() 
  {
    end();
    DriverStation.reportError("Command CargoStop has been interrupted", true);
  }
}
