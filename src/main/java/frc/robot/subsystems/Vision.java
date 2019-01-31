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
import edu.wpi.first.wpilibj.command.Subsystem;

public class Vision extends Subsystem
{
  private NetworkTable visionTable;
  private NetworkTableEntry distanceEntry, xOffestEntry;

  public Vision()
  {
    // Gets vision table on NetworkTables server
    visionTable = NetworkTableInstance.getDefault().getTable("Vision");

    // Gets the entries so we can access their values
    distanceEntry = visionTable.getEntry("distance:");
    xOffestEntry = visionTable.getEntry("xOffset");
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

  public boolean isCentered(double pixelError)
  {
    // Returns true if the center offset is within a certain pixel error value set by the command
    return (getCenterOffset() > -pixelError && getCenterOffset() < pixelError) ? true : false;
  }

  @Override
  public void initDefaultCommand()
  {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
