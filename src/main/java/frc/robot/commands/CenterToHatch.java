/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class CenterToHatch extends Command
{
  private double centerOffest = 0;
  private double latencyDelay = 0.01; //TODO: Get this value
  private ArrayList<Double> errorArray;

  public CenterToHatch()
  {
    requires(Robot.vision);
    errorArray = new ArrayList<Double>();
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
    // This bit is a little more complex than we had before, but it's doing the same thing
    // When we capture a new error on a frame, we set a new setpoint
    // But here, we account for the time it took to process our frame, so that way we can be more precice in our control

    // If the captured frame has a different center error than previously captured, change the set point
    if(Robot.drivePID.drivePIDController.getSetpoint() != Robot.vision.getCenterOffset())
    {
      // Gets the index based off the delay of processing the frame
      double totalDelay = Robot.vision.getFrameDelay() + latencyDelay;

      // Gets the index by dividing the delay by the scheduler time and adding previously known value
      int posIndex = errorArray.size() - (int) Math.floor(totalDelay / RobotMap.delayTime);

      // Sets new setpoint for PID controller using the known error at the time we captured the frame
      Robot.drivePID.drivePIDController.setSetpoint(errorArray.get(posIndex));
    }

    // Adds our current error to the array
    errorArray.add(Robot.drivePID.drivePIDController.getError());
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
