/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import frc.robot.RobotMap;

/*
 **   
 * An example subsystem.  You can replace me with your own Subsystem.
 */

public class Drive extends Subsystem
{
  Spark frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;
  MecanumDrive drive;

  public Drive()
  {
    // Motor controller objects
    frontRightMotor = new Spark(RobotMap.frontRightMotorPort);
    frontLeftMotor = new Spark(RobotMap.frontLeftMotorPort);
    backRightMotor = new Spark(RobotMap.backRightMotorPort);
    backLeftMotor = new Spark(RobotMap.backLeftMotorPort);
    drive = new MecanumDrive(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor);

    backLeftMotor.setInverted(true);
  }

  // Main drive method
  public void drive(double sideVal, double forwardVal, double rotateVal)
  {
    drive.driveCartesian(sideVal, -forwardVal, rotateVal);
  }

  // If you need comments for this I'm sorry
  public void stop()
  {
    drive.stopMotor();
  }


  @Override
  public void initDefaultCommand()
  {
    
  }
}
