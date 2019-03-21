/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap
{
  // Deadzone constant
  public static final double deadzone = 0.15;

   // Button ports
   public static final int cargoButton = 1, grabButton = 3, extendButton = 4;

   // Button ports for codrive
   public static final int coCargoButton = 3, coGrabButton = 4, coExtendButton = 1;
   
   // Limit switch ports
   public static final int cargoLimit = 0;
  
  // Solenoid ports
  public static final int grabPort = 0, extendPort = 1;

  // Motor port constants
  public static final int frontRightMotorPort = 0, frontLeftMotorPort = 1;
  public static final int backLeftMotorPort = 2, backRightMotorPort = 3;
  public static final int intakeMotorPort = 4;

  // Controller port constants
  public static final int xAxis = 0, yAxis = 1, zAxis = 4;
}
