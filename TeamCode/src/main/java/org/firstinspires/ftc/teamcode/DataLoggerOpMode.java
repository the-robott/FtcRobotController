package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.List;

@TeleOp(name = "DataLoggerOpMode", group = "Logging")
public class DataLoggerOpMode extends LinearOpMode {

    private DcMotorEx leftFrontDrive = null;
    private DcMotorEx leftBackDrive = null;
    private DcMotorEx rightFrontDrive = null;
    private DcMotorEx rightBackDrive = null;
    private VoltageSensor voltageSensor;

    private StringBuilder csvData = new StringBuilder(); // To store all CSV log data

    @Override
    public void runOpMode() throws InterruptedException {

        // Initialize motors
        leftFrontDrive  = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftBackDrive  = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightFrontDrive = hardwareMap.get(DcMotorEx.class, "rightFront");
        rightBackDrive = hardwareMap.get(DcMotorEx.class, "rightRear");

        // Voltage sensor
        List<VoltageSensor> voltageSensors = hardwareMap.getAll(VoltageSensor.class);
        voltageSensor = voltageSensors.get(0);

        // FTC Dashboard instance
        FtcDashboard dashboard = FtcDashboard.getInstance();

        // CSV header
        csvData.append("Time(ms),BatteryVoltage(V),Motor1Current(A),Motor2Current(A)\n");

        telemetry.addLine("Ready to log data.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double time = getRuntime() * 1000;
            double batteryVoltage = voltageSensor.getVoltage();
            double motor1Current = leftFrontDrive.getCurrent(CurrentUnit.AMPS);
            double motor2Current = rightBackDrive.getCurrent(CurrentUnit.AMPS);

            String line = String.format("%.0f,%.2f,%.2f,%.2f\n", time, batteryVoltage, motor1Current, motor2Current);
            csvData.append(line);

            // Dashboard packet
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("Time(ms)", time);
            packet.put("BatteryVoltage(V)", batteryVoltage);
            packet.put("Motor1Current(A)", motor1Current);
            packet.put("Motor2Current(A)", motor2Current);
            dashboard.sendTelemetryPacket(packet);

            telemetry.addData("Voltage", "%.2f V", batteryVoltage);
            telemetry.addData("M1 Current", "%.2f A", motor1Current);
            telemetry.addData("M2 Current", "%.2f A", motor2Current);
            telemetry.update();

            sleep(100);
        }

        // After opmode ends, send CSV data as a packet string
        TelemetryPacket finalPacket = new TelemetryPacket();
        finalPacket.addLine("CSV Data:\n" + csvData.toString());
        dashboard.sendTelemetryPacket(finalPacket);
    }
}
