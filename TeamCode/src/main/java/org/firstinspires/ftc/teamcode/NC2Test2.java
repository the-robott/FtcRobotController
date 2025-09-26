package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import com.qualcomm.robotcore.hardware.VoltageSensor;


@TeleOp(name="NC2Test2_Dashboard")
//@Disabled
public class NC2Test2 extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx leftFrontDrive = null;
    private DcMotorEx leftBackDrive = null;
    private DcMotorEx rightFrontDrive = null;
    private DcMotorEx rightBackDrive = null;
    private DcMotorEx xEncoder = null;
    private DcMotorEx yEncoder = null;
    private VoltageSensor voltageSensor;

    @Override
    public void runOpMode() {

        // Initialize motors
        leftFrontDrive  = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftBackDrive   = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightFrontDrive = hardwareMap.get(DcMotorEx.class, "rightFront");
        rightBackDrive  = hardwareMap.get(DcMotorEx.class, "rightRear");
        xEncoder = hardwareMap.get(DcMotorEx.class, "leftRear");
        yEncoder = hardwareMap.get(DcMotorEx.class, "rightRear");
        voltageSensor = hardwareMap.voltageSensor.iterator().next();


        leftFrontDrive.setDirection(DcMotorEx.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotorEx.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotorEx.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotorEx.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        FtcDashboard dashboard = FtcDashboard.getInstance();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            double axial   =  -gamepad1.left_stick_y;
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;

            double leftFrontPower  = axial + lateral + yaw;
            double rightFrontPower = axial - lateral - yaw;
            double leftBackPower   = axial - lateral + yaw;
            double rightBackPower  = axial + lateral - yaw;

            double max = Math.max(
                    Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower)),
                    Math.max(Math.abs(leftBackPower), Math.abs(rightBackPower))
            );
            if (max > .5) {
                leftFrontPower  /= max;
                rightFrontPower /= max;
                leftBackPower   /= max;
                rightBackPower  /= max;
            }

            //leftFrontPower  = gamepad1.x ? 1.0 : 0.0;  // X gamepad
            //leftBackPower   = gamepad1.a ? 1.0 : 0.0;  // A gamepad
            //rightFrontPower = gamepad1.y ? 1.0 : 0.0;  // Y gamepad
            //rightBackPower  = gamepad1.b ? 1.0 : 0.0;  // B gamepad

            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightBackDrive.setPower(rightBackPower);

            double xPos = xEncoder.getCurrentPosition();
            double yPos = yEncoder.getCurrentPosition();
            double lfCurrent = leftFrontDrive.getCurrent(CurrentUnit.AMPS);
            double lbCurrent = leftBackDrive.getCurrent(CurrentUnit.AMPS);
            double rfCurrent = rightFrontDrive.getCurrent(CurrentUnit.AMPS);
            double rbCurrent = rightBackDrive.getCurrent(CurrentUnit.AMPS);
            double batteryVoltage = voltageSensor.getVoltage();
            double leftFrontVelocity = leftFrontDrive.getVelocity(AngleUnit.RADIANS);
            double rightFrontVelocity = rightFrontDrive.getVelocity(AngleUnit.RADIANS);
            double leftBackVelocity = leftBackDrive.getVelocity(AngleUnit.RADIANS);
            double rightBackVelocity = rightBackDrive.getVelocity(AngleUnit.RADIANS);

            telemetry.addData("Battery Voltage", "%.2f V", batteryVoltage);
            telemetry.addData("X Encoder", xPos);
            telemetry.addData("Y Encoder", yPos);
            telemetry.addData("Currents (LF, LB, RF, RB)", "%.2f, %.2f, %.2f, %.2f",
                    lfCurrent, lbCurrent, rfCurrent, rbCurrent);
            telemetry.addData("Velocity (LF, LB, RF, RB)", "%.2f, %.2f, %.2f, %.2f",leftFrontVelocity,rightFrontVelocity,leftBackVelocity,rightBackVelocity);
            telemetry.addData("Run Time", runtime.toString());
            telemetry.update();

            // FTC Dashboard logging
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("runtime", runtime.milliseconds());
            packet.put("x_encoder", xPos);
            packet.put("y_encoder", yPos);
            packet.put("leftFrontCurrent", lfCurrent);
            packet.put("leftBackCurrent", lbCurrent);
            packet.put("rightFrontCurrent", rfCurrent);
            packet.put("rightBackCurrent", rbCurrent);
            packet.put("leftFrontVelocity",leftFrontVelocity);
            packet.put("rightFrontVelocity",rightFrontVelocity);
            packet.put("leftBackVelocity",leftBackVelocity);
            packet.put("rightBackVelocity",rightBackVelocity);
            packet.put("LF Power", leftFrontPower);
            packet.put("LB Power", leftBackPower);
            packet.put("RF Power", rightFrontPower);
            packet.put("RB Power", rightBackPower);
            packet.put("Battery Voltage", batteryVoltage);
            dashboard.sendTelemetryPacket(packet);
        }
    }
}
