/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;

/**
 * Add your docs here.
 */
public class Cargo extends Subsystem 
{
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  Victor intakeMotor;
  
  // Private so we don't accidently fuck with it
  //private DigitalInput cargoLimitSwitch;

  public Cargo()
  {
    intakeMotor = new Victor(RobotMap.intakeMotorPort);

    //cargoLimitSwitch = new DigitalInput(RobotMap.cargoLimit);
  }

  public void startCargoMotors()
  {
    intakeMotor.set(-1);
  }

  public void stopCargoMotors()
  {
    intakeMotor.stopMotor();
  }

  public boolean getLimitSwitch()
  {
    //return cargoLimitSwitch.get();
    return false;
  }
  
  @Override
  public void initDefaultCommand()
  {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
