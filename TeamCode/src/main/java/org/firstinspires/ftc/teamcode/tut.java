package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "motor_1")
public class tut extends OpMode
{

    DcMotor motor;

    @Override
    public void init() {

        motor = hardwareMap.dcMotor.get("test_motor");

    }

    @Override
    public void loop() {

        motor.setPower(1);
    }
}

