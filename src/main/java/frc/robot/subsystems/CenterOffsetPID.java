/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import frc.robot.Robot;

// This is the PID Controller for the center offset for the vision tape
public class CenterOffsetPID
{
    // PID objects needed for the controller
    public PIDController centerPIDController;
    private PIDSource centerPIDSource;
    private PIDOutput centerPIDOutput;

    public double kP = 0.0, kI = 0.0, kD = 0.0, kF = 0.0;

    public CenterOffsetPID()
    {
        // Setting up the PID source
        centerPIDSource = new PIDSource()
        {
            // Locally stores source type, we shouldn't need to change it but here it is anyways
            PIDSourceType sourceType = PIDSourceType.kDisplacement;

            // Getter and setter methods for the source type
            @Override
            public void setPIDSourceType(PIDSourceType pidSource)
            {
                sourceType = pidSource;
            }
        
            @Override
            public PIDSourceType getPIDSourceType()
            {
                return sourceType;
            }

            // This is the actual value that the PID controller will use
            @Override
            public double pidGet()
            {
                return Robot.vision.getCenterOffset();
            }
        };

        // Sets up the PID output
        centerPIDOutput = new PIDOutput()
        {
            // Method to do the thing with the output of the PID loop
            @Override
            public void pidWrite(double output)
            {
                Robot.drive.move(output, 0.0, 0.0);
            }
        };

        // Sets up the PID controller
        centerPIDController = new PIDController(kP, kI, kD, kF, centerPIDSource, centerPIDOutput);
    }
}
