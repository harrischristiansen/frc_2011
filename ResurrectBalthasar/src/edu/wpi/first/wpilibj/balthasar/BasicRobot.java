package edu.wpi.first.wpilibj.balthasar;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.SmartDashboard;
import edu.wpi.first.wpilibj.Jaguar;

//this class provides a simple way to drive a two-motor robot with Jaguar motor controllers
//Iterative: the autononmous, teleoperated, and disabled methods are called every .01 second
public class BasicRobot extends IterativeRobot {

    Joystick stick;
    // in/mid/out/reverse
    final int armBtns[] = {7, 6, 8, 5}, armDwnBtn = 1, armDwnSpins=450,
            transportDwnBtn = 2, transportUpBtn = 3, bridgeBtn = 2, sliderBtn = 3;
    int  armDwnCycles=0;
    CustomDrive cDrive;
    AxisCamera cam;
    Victor transp;
    Relay magBridge, magSlide;
    boolean bridgeReleased = false;
    Jaguar jagLiftOut = new Jaguar(5);
    Jaguar jagLiftMid = new Jaguar(6);
    Victor vicLiftIn = new Victor(7);
    //Timer[] timers = {new Timer(), new Timer(), new Timer()};

    public void robotInit() {
        Watchdog.getInstance().setEnabled(false);//kill the watchdog
        stick = new Joystick(1);
        //Motor controllers connected to PWM 1,2
        cDrive = new CustomDrive(stick);//driving controller
        //simply getting an instance of the camera sends video to driver station
        //cam = AxisCamera.getInstance();
        transp = new Victor(3);
        magBridge = new Relay(1);
        magSlide = new Relay(2);
        SmartDashboard.init();
    }

    public void teleopContinuous() {
        cDrive.update();

        //Transporter arm
        if (stick.getRawButton(transportUpBtn)) {
            transp.set(1);
        } else if (stick.getRawButton(transportDwnBtn)) {
            transp.set(-1);
        } else {
            transp.set(0);
        }

        //Deployer electromagnets
        if (stick.getRawButton(bridgeBtn) || bridgeReleased) { //No accidental releases
            magBridge.set(Relay.Value.kOff);
            bridgeReleased = true;
        } else {
            magBridge.set(Relay.Value.kForward);
        }
        if (stick.getRawButton(6)) {
            bridgeReleased = false;
        }
        if (stick.getRawButton(sliderBtn)) {
            magSlide.set(Relay.Value.kOff);
        } else {
            magSlide.set(Relay.Value.kForward);
        }

        //Arm relays
        double direction;
        if (stick.getRawButton(armBtns[3])) {
            direction = 1;
        } else {
            direction = -1;
        }
        // Inner
        if (stick.getRawButton(armBtns[0])) {
            vicLiftIn.set(direction);
        } else { vicLiftIn.set(0); }

        // Middle
        if (stick.getRawButton(armBtns[1])) {
            jagLiftMid.set(direction);
        } else { jagLiftMid.set(0); }

        // Outer
        if (stick.getRawButton(armBtns[2])) {
            jagLiftOut.set(direction);
        } else { jagLiftOut.set(0); }

        // Quick Down
        if(stick.getRawButton(armDwnBtn)) {
            armDwnCycles+=1;
            jagLiftOut.set(1);
        } else {
            if(armDwnCycles<=armDwnSpins&&armDwnCycles>0) {
                jagLiftOut.set(1);
                armDwnCycles+=1;
            } else {
                armDwnCycles=0;
            }
        }

    }

    public void autonomousContinuous() {
        cDrive.autoUpdate();
    }

    public void autonomousInit() {
    }

    public void autonomousPeriodic() {
    }

    public void teleopInit() {
    }

    public void teleopPeriodic() {
    }

    public void disabledInit() {
    }

    public void disabledPeriodic() {
    }

    public void disabledContinuous() {
    }
}
