package org.firstinspires.ftc.teamcode.control;

import static org.firstinspires.ftc.teamcode.control.PIDConfig.ticks;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.config.Config;

import android.util.Log;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@TeleOp(name="Motor Velocity PID", group="Control")
public class MotorVelocityPID extends LinearOpMode {

    DcMotorEx motor;
    VoltageSensor battery;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize hardware
        motor = hardwareMap.get(DcMotorEx.class, "sMotor");

        // Get first voltage sensor from the Control Hub
        battery = hardwareMap.voltageSensor.iterator().next();

        motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        // Example PIDF values (adjust for your motor)
        //PIDFCoefficients pidf = new PIDFCoefficients(15, 1, 0.5, 0);
        //motor.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidf);

        // Combine DS telemetry with FTC Dashboard telemetry
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        waitForStart();

        //double targetVel = ticks; // ticks/sec fixed target

        /**************
         * Encoder Ticks per second=RPM*PPR/60
         * PPR for 312: 537.6
         * 312*537.6/60== 2793.6
         */

        while (opModeIsActive()) {

            double targetVel = PIDConfig.rpm*((2*3.14)/60);

            motor.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER,
                    new PIDFCoefficients(PIDConfig.kP, PIDConfig.kI, PIDConfig.kD, PIDConfig.kF));

            // Command motor velocity
            motor.setVelocity(targetVel, AngleUnit.RADIANS);

            // Collect sensor data
            double velocity = motor.getVelocity(AngleUnit.RADIANS)*(60/(2*3.14));
            double current = motor.getCurrent(CurrentUnit.MILLIAMPS);
            double voltage  = battery.getVoltage();

            // Log to FTC Dashboard & DS telemetry
            telemetry.addData("Target Velocity", PIDConfig.rpm);
            telemetry.addData("Current Velocity", velocity);
            telemetry.addData("Current (A)", current);
            telemetry.addData("Voltage (V)", voltage);
            telemetry.update();

            // Logcat (optional)
            Log.i("FTC", "Phase=const Target=" + PIDConfig.rpm
                    + " Vel=" + velocity
                    + " V=" + voltage
                    + " A=" + current
                    + " Error=" + ((PIDConfig.rpm-velocity)/PIDConfig.rpm)*100);
            /*
            Log.i("FTC", "Phase=constant Target=" + targetVel +
                    " Vel=" + velocity +
                    " A=" + current +
                    " V=" + voltage);

             */
        }
    }
}
