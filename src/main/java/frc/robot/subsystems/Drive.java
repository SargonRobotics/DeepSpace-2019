/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotMap;

/*
 **   
 * An example subsystem.  You can replace me with your own Subsystem.
 */

public class Drive extends Subsystem
{
  Spark frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;
  MecanumDrive drive;

  Encoder leftEncoder, rightEncoder;
  ADXRS450_Gyro gyro;

  public double inchesToEncoderTurns = 20.8;

  public Drive()
  {    
    // Encoder objects
    //leftEncoder = new Encoder(RobotMap.leftEncoderA, RobotMap.leftEncoderB);
    rightEncoder = new Encoder(RobotMap.rightEncoderA, RobotMap.rightEncoderB);

    // Motor controller objects
    frontRightMotor = new Spark(RobotMap.frontRightMotorPort);
    frontLeftMotor = new Spark(RobotMap.frontLeftMotorPort);
    backRightMotor = new Spark(RobotMap.backRightMotorPort);
    backLeftMotor = new Spark(RobotMap.backLeftMotorPort);
    
    drive = new MecanumDrive(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor);
  }

  // Main drive method
  public void driveRobot(double sideVal, double forwardVal, double rotateVal)
  {
    // Build messed up the motor ports
    drive.driveCartesian(sideVal, -forwardVal, rotateVal);
  }

  // If you need comments for this I'm sorry
  public void stop()
  {
    drive.stopMotor();
  }

  public double getAverageSpeed()
  {
    return Math.abs(rightEncoder.get());
    //return (Math.abs(frontLeftMotor.get()) + Math.abs(frontRightMotor.get()) + Math.abs(backLeftMotor.get()) + Math.abs(backLeftMotor.get())) / 4;
  }

  public double getAverageEncoderValue()
  {
    //SmartDashboard.putNumber("Encoder Left: ", leftEncoder.getDistance());
    SmartDashboard.putNumber("Encoder Right: ", rightEncoder.get());
    return rightEncoder.get();
    //return (Math.abs(leftEncoder.get()) + Math.abs(rightEncoder.get())) / 2;
  }

  public double getGyro()
  {
    return gyro.getAngle();
  }

  public void resetEncoders()
  {
    //leftEncoder.reset();
    rightEncoder.reset();
  }

  public void resetGyro()
  {
    gyro.reset();
  }

  @Override
  public void initDefaultCommand()
  {
    
  }
}
