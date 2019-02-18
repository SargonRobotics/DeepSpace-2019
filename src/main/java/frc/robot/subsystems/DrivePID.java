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
public class DrivePID
{
    // PID objects needed for the controller
    public PIDController drivePIDController;
    private PIDSource driveSource;
    private PIDOutput driveOutput;

    public PIDController turnCorrectionPIDController;
    private PIDSource turnCorrectionSource;
    private PIDOutput turnCorrectionOutput;

    private double maxVoltage = 12;
    private double turnCorrection = 0;

    // TODO: Tune these
    public double kPDrive = 0.02, kIDrive = 0.001, kDDrive = 0.018, kF = (1 / maxVoltage);
    public double kPTurn = 0, kITurn = 0, kDTurn = 0;

    public DrivePID()
    {
        // Setting up the PID sources
        driveSource = new PIDSource()
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
                return Robot.drive.getAverageEncoderValue();
            }
        };

        turnCorrectionSource = new PIDSource()
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
                return Robot.drive.getAverageEncoderValue();
            }
        };

        // Sets up the PID output
        driveOutput = new PIDOutput()
        {
            // Method to do the thing with the output of the PID loop
            @Override
            public void pidWrite(double output)
            {
                System.out.println(-output); //TODO: Remote this print
                Robot.drive.driveRobot(0.0, -output, turnCorrection); //TODO: Test PID with turn correction
            }
        };

        turnCorrectionOutput = new PIDOutput()
        {
            @Override
            public void pidWrite(double output)
            {
                turnCorrection = output;
            }
        };

        // Sets up the PID controller
        drivePIDController = new PIDController(kPDrive, kIDrive, kDDrive, kF, driveSource, driveOutput)
        {
            @Override
            public double calculateFeedForward()
            {
                // Calculates the feedforward by using the voltages put into the motors
                return Robot.drive.getAverageSpeed() * this.getF();
            }
        };

        turnCorrectionPIDController = new PIDController(kPTurn, kITurn, kDTurn, kF, turnCorrectionSource, turnCorrectionOutput)
        {
            @Override
            public double calculateFeedForward()
            {
                // Calculates the feedforward by using the voltages put into the motors
                return Robot.drive.getAverageSpeed() * this.getF();
            }
        };
    }
}
