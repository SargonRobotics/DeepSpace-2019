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
  private ArrayList<Double> posArray;

  public CenterToHatch()
  {
    requires(Robot.vision);
    posArray = new ArrayList<Double>();
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize()
  {
    centerOffest = Robot.vision.getCenterOffset();
    Robot.drivePID.drivePIDController.setAbsoluteTolerance(4);
    Robot.drivePID.drivePIDController.setSetpoint(centerOffest * Robot.drive.inchesToEncoderTurns);
    Robot.drivePID.drivePIDController.enable();
    Robot.drive.resetEncoders();

    System.out.println(Robot.drivePID.drivePIDController.getSetpoint());
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute()
  {
    //TODO: This is broken, figure out what's wrong and fix it

    // This bit is a little more complex than we had before, but it's doing the same thing
    // When we capture a new position on a frame, we set a new setpoint
    // But here, we account for the time it took to process our frame, so that way we can be more precice in our control

    // If the captured frame has a different center position than previously captured, change the set point
    if(Robot.drivePID.drivePIDController.getSetpoint() != (Robot.vision.getCenterOffset() * Robot.drive.inchesToEncoderTurns))
    {
      // Gets the index based off the delay of processing the frame
      double totalDelay = Robot.vision.getFrameDelay() + latencyDelay;

      // This is the broken part
      // Gets the index by dividing the delay by the scheduler time and adding previously known value
      int posIndex = (int)Math.round(totalDelay / RobotMap.delayTime);

      //System.out.println(totalDelay + ", " + posIndex);

      // Sets new setpoint for PID controller using the known position at the time we captured the frame
      Robot.drivePID.drivePIDController.setSetpoint((posArray.get(posIndex) + Robot.drivePID.drivePIDController.getError()) * Robot.drive.inchesToEncoderTurns);
    }

    // Adds our current position to the array
    posArray.add(Robot.drive.getAverageEncoderValue());
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
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted()
  {
    end();
  }
}
