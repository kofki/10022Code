package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;



@TeleOp
public class Finale extends LinearOpMode {

    // Declare OpMode members.
    
    double ticks = 537.7;
    double angle = ticks/Math.PI;
    
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor leftArmMotor;
    private DcMotor rightArmMotor;
    private Servo servoWrist;
    private Servo servoClaw;
    
    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        leftArmMotor = hardwareMap.dcMotor.get("leftArmMotor");
        rightArmMotor = hardwareMap.dcMotor.get("rightArmMotor");
        servoWrist = hardwareMap.servo.get("servoWrist");
        servoClaw = hardwareMap.crservo.get("servoClaw");
        
        // Reverse motors
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        leftArmMotor.setDirection(DcMotorSimple.Direction.REVERSE);


        //ENCODERS SETUP
        leftArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        
        leftArmMotor.setTargetPosition(0);
        rightArmMotor.setTargetPosition(0);
        
        
        leftArmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightArmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        leftArmMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightArmMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        
        waitForStart();
        
        if (isStopRequested()) return;
        
        double yPower = 0;
        double xPower = 0;
        double xRotation = 0;
        boolean slow = false;
        boolean reversed = false;
        double armPower = 0;
        boolean reversing = false;
        boolean servoRotate = false;
        boolean closed = false;
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            yPower = -gamepad1.left_stick_y;
            xPower = gamepad1.left_stick_x;
            xRotation = gamepad1.right_stick_x;
            reversing = gamepad2.a;
            armPower = gamepad2.right_trigger - gamepad2.left_trigger;
            slow = gamepad1.right_bumper;
            
            
            if (gamepad2.right_bumper){
                servoRotate = true;
            }
            if (gamepad2.left_bumper ){
                servoRotate = false;  
            } 
            if (gamepad2.a){
               closed = true; 
            } else if (gamepad2.b){
                closed = false;
            }
            closeClaw(closed);
            
            clawMovement(servoRotate);
            
            armMovement(armPower);
            
            wheels(yPower, xPower, xRotation, slow);
            
            telemetry.update();
        }
    }
    public void wheels(double y, double x, double rx, boolean slow){
        double denom = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double slowness = 1;
        if (slow){
            slowness = 3;
        }

        // Wheels math :P
        frontLeftMotor.setPower(((y + x + rx) / denom) / slowness);
        frontRightMotor.setPower(((y - x - rx) / denom) / slowness);
        backLeftMotor.setPower(((y - x + rx) / denom) / slowness);
        backRightMotor.setPower(((y + x - rx) / denom) / slowness);

    }
    public void closeClaw(boolean closed){
        if (closed){
            servoClaw.setPower(1);
        }
        else if (!closed){
            servoClaw.setPower(-1);
        }
    }
    public void clawMovement(boolean claw){
        if (claw){
            servoWrist.setPosition(1.5);
        }
        else{
            servoWrist.setPosition(0.8);
        }
    }
    public void armMovement(double Power){
        int armTarget = 0;
        double armSpeed = (Power)*0.75;
        telemetry.addData("Position", leftArmMotor.getCurrentPosition());
        telemetry.addData("Position2", rightArmMotor.getCurrentPosition());
        telemetry.addData("Target", leftArmMotor.getTargetPosition());
        if (Power > 0){
            armTarget = 400;
        
        } else if (Power < 0){
            armTarget = 0;
        }
        leftArmMotor.setPower(armSpeed);
        rightArmMotor.setPower(armSpeed);
        leftArmMotor.setTargetPosition(armTarget);
        rightArmMotor.setTargetPosition(armTarget);
        
    }
}
