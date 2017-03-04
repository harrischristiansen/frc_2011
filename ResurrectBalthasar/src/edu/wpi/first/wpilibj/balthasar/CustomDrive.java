package edu.wpi.first.wpilibj.balthasar;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SmartDashboard;
import edu.wpi.first.wpilibj.Encoder;

//this class controls the motors from joystick input, like RobotDrive
public class CustomDrive {

    Joystick stick;
    final int fastBtn = 11, slowBtn = 12, leftAxis = 2, rightAxis = 5;
    final double slowSpeed = 0.7, fastSpeed = 2.2, smoothFactor = 0.999, half = 0.5, driveStraight = 0.95;
    double speedC = 1;
    Jaguar leftMotor, rightMotor;
    DigitalInput[] lightsensors = new DigitalInput[3];
    final String[] positions = {"Left", "Center", "Right"};
    Encoder leftEncoder, rightEncoder;
    boolean runEncoders = true;

    public CustomDrive(Joystick controller) {
        SmartDashboard.init();
        stick = controller;
        leftMotor = new Jaguar(1);
        rightMotor = new Jaguar(2);
        leftEncoder = new Encoder(1, 2);
        leftEncoder.start();
        rightEncoder = new Encoder(3, 4);
        rightEncoder.start();
        lightsensors[0] = new DigitalInput(5);//left
        lightsensors[1] = new DigitalInput(6);//center
        lightsensors[2] = new DigitalInput(7);//right
    }

    public void update() {
        double leftSpeed = 0, rightSpeed = 0;

        if (stick.getRawButton(slowBtn)) {
            if (speedC >= slowSpeed) {
                speedC = speedC * smoothFactor;
            }
        }
        if (stick.getRawButton(fastBtn)) {
            if (speedC <= fastSpeed) {
                speedC = speedC / smoothFactor;
            }
        }
        if (!stick.getRawButton(slowBtn) && !stick.getRawButton(fastBtn)) {
            speedC = 1;
        }

        // Slows motor, multiplies by speed buttons pressed
        leftSpeed = stick.getRawAxis(rightAxis) * half * speedC * driveStraight;
        rightSpeed = stick.getRawAxis(leftAxis) * half * speedC;

        //////////////////// Encoder Straight System /////////////////////////
        // Going relativly straight
        
        /*if (stick.getRawButton(9)) {
            runEncoders = false;
        }
        if (leftSpeed>=rightSpeed-0.05&&leftSpeed<=rightSpeed+0.05&&runEncoders) {
            // Resets if large distace between encoders OR if encoders have completed 3 full turns
            double leftEncoderValue = leftEncoder.get();
            if (leftEncoderValue==0) {
                leftEncoderValue=1;
            }
            double rightEncoderValue = rightEncoder.get();
            if (rightEncoderValue==0) {
                rightEncoderValue=1;
            }
            if(leftEncoderValue>rightEncoderValue+100||rightEncoderValue>leftEncoderValue+100||leftEncoderValue>=1080) {
                leftEncoder.reset();
                rightEncoder.reset();
            }
            double encoderSpeed = (rightEncoderValue)/(leftEncoderValue);
            if (encoderSpeed == 0) {
                encoderSpeed = 1;
            }
            leftSpeed=leftSpeed*(encoderSpeed);
        }*/

        rightMotor.set(rightSpeed);//motor reversed
        leftMotor.set(-leftSpeed);

        SmartDashboard.log(leftEncoder.get(), "Left encoder");
        SmartDashboard.log(rightEncoder.get(), "Right encoder");
        SmartDashboard.log(leftSpeed, "Left speed");
        SmartDashboard.log(rightSpeed, "Right speed");
    }

    public void autoUpdate() {
        //Line tracking
        boolean output[] = new boolean[3];
        for (int i = 0; i < 3; i++) {
            output[i] = lightsensors[i].get();
            SmartDashboard.log(output[i], positions[i] + " light");
        }

        double leftSpeed = .4, rightSpeed = .4;
        if (output[0] && output[1] && output[2]) {
            leftSpeed = 0;
            rightSpeed = 0;
        }
        if (!output[0]) {
            leftSpeed = 0;
        }
        if (!output[2]) {
            leftSpeed = 0;
        }
        rightMotor.set(-rightSpeed);
        leftMotor.set(leftSpeed);
    }
}
