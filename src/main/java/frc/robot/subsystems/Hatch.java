/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
/**
 * Add your docs here.
 */
public class Hatch extends Subsystem 
{
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  DoubleSolenoid grabSolenoid, extendSolenoid;

  public Hatch()
  { 
    grabSolenoid = new DoubleSolenoid(RobotMap.grabPortExtend, RobotMap.grabPortRetract);
    extendSolenoid = new DoubleSolenoid(RobotMap.outPortExtend, RobotMap.outPortRetract);
  }

  public void grabHatchCover()
  {
    grabSolenoid.set(DoubleSolenoid.Value.kForward);
  }

  public void releaseHatchCover()
  {
    grabSolenoid.set(DoubleSolenoid.Value.kReverse);
  }

  public void extendHatchCover()
  {
    extendSolenoid.set(DoubleSolenoid.Value.kForward);
  }

  public void retractHatchCover()
  {
    extendSolenoid.set(DoubleSolenoid.Value.kReverse);
  }

  @Override
  public void initDefaultCommand() 
  {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
