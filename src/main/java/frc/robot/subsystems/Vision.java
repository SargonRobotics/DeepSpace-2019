/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Vision extends Subsystem
{
  private NetworkTable visionTable;
  private NetworkTableEntry distanceEntry, xOffestEntry, frameDelayEntry;
  Relay lightRelay;

  public Vision()
  {
    // Gets vision table on NetworkTables server
    visionTable = NetworkTableInstance.getDefault().getTable("Vision");

    // Gets the entries so we can access their values
    distanceEntry = visionTable.getEntry("distance:");
    xOffestEntry = visionTable.getEntry("xOffset");
    frameDelayEntry = visionTable.getEntry("delay");

    lightRelay = new Relay(0);
  }

  public double getDistanceToTape()
  {
    // Gets the distance value, with a default value of 0 so if it doesn't get the value it doesn't do anything
    return distanceEntry.getDouble(0);
  }

  public double getCenterOffset()
  {
    // Gets the xOffset value, with a default value of 0 so if it doesn't get the value it doesn't do anything
    return xOffestEntry.getDouble(0);
  }

  public double getFrameDelay()
  {
    // Gets the amount of time it took to grab and process the frame
    return frameDelayEntry.getDouble(0);
  }

  public boolean isCentered(double pixelError)
  {
    // Returns true if the center offset is within a certain pixel error value set by the command
    return (getCenterOffset() > -pixelError && getCenterOffset() < pixelError) ? true : false;
  }

  public void turnOnLight()
  {
    lightRelay.set(Relay.Value.kForward);
  }

  public void turnOffLight()
  {
    lightRelay.set(Relay.Value.kOff);
  }

  @Override
  public void initDefaultCommand()
  {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
