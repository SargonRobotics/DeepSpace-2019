/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
/**
 * Add your docs here.
 */
public class Hatch extends Subsystem 
{
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  Solenoid grabSolenoid, extendSolenoid;
  
  // These store the states for the pistons
  private boolean grab = false;
  private boolean extend = false;

  public Hatch()
  { 
    // Initialize solenoid object
    grabSolenoid = new Solenoid(RobotMap.grabPort);
    extendSolenoid = new Solenoid(RobotMap.extendPort);
  }

  public void toggleGrabber()
  {
    // Changes state of piston and sets it to the new value
    if(extend)
    {
      grab = !grab;
      grabSolenoid.set(grab);
    }
  }

  public void toggleExtender()
  {
    // TODO: Make it so when we retract it the grabber is releasing the hatch
    // Changes state of piston and sets it to the new value
    extend = !extend;

    if(!extend && grab)
    {
      grab = false;
      grabSolenoid.set(grab);
    }

    extendSolenoid.set(extend);
  }

  @Override
  public void initDefaultCommand() 
  {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
