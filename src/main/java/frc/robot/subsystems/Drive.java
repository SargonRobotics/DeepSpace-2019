/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import frc.robot.RobotMap;

/*
 **   
 * An example subsystem.  You can replace me with your own Subsystem.
 */

public class Drive extends Subsystem
{
  Victor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;
  MecanumDrive drive;

  public Drive()
  {
    frontRightMotor = new Victor(RobotMap.frontRightMotorPort);
    frontLeftMotor = new Victor(RobotMap.frontLeftMotorPort);
    backRightMotor = new Victor(RobotMap.backRightMotorPort);
    backLeftMotor = new Victor(RobotMap.backLeftMotorPort);

    drive = new MecanumDrive(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor);
  }

  public void move(double sideVal, double forwardVal, double rotateVal)
  {
    drive.driveCartesian(sideVal, forwardVal, rotateVal);
  }

  public void stop()
  {
    drive.stopMotor();
  }


  @Override
  public void initDefaultCommand()
  {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
