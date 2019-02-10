/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class CenterToHatch extends Command
{
  private double centerOffest = 0;

  public CenterToHatch()
  {
    requires(Robot.vision);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize()
  {
    centerOffest = Robot.vision.getCenterOffset();
    Robot.drivePID.drivePIDController.setAbsoluteTolerance(4);
    Robot.drivePID.turnCorrectionPIDController.setAbsoluteTolerance(2);
    Robot.drivePID.drivePIDController.setSetpoint(centerOffest * Robot.drive.inchesToEncoderTurns);
    Robot.drivePID.turnCorrectionPIDController.setSetpoint(0);
    Robot.drivePID.drivePIDController.enable();
    Robot.drivePID.drivePIDController.enable();
    Robot.drive.resetEncoders();
    Robot.drive.resetGyro();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute()
  {
    
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished()
  {
    return Robot.drivePID.drivePIDController.onTarget();
  }

  // Called once after isFinished returns true
  @Override
  protected void end()
  {
    Robot.drivePID.drivePIDController.disable();
    Robot.drivePID.turnCorrectionPIDController.disable();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted()
  {
    end();
  }
}
