package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name="BlueSilver13475", group="Autonomous")
//@Disabled
public class BlueSilver13475 extends LinearOpMode {


    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    private DcMotor landerRiser = null;

    private ElapsedTime     runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 1220 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 2.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;
    static final double     RISER_SPEED              = 0.5;


    @Override
    public void runOpMode() {

        setUp();
        waitForStart();



        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed).
        //Timeout is how much time is alloted for the moter to get to a desired position
        //i am assuming 90 turn is 12'

     //this is code to disembark
       // encoderDrive(RISER_SPEED,  0,  0,  7,15.0); //riser
        //encoderDrive(DRIVE_SPEED,  4,  4,  0,5.0);//move after land
        //encoderDrive(TURN_SPEED,  12,  -12,  0,5.0); // spin 90

     //code to balls
        encoderDrive(DRIVE_SPEED, 4.5, -4.5,0, 5.0);//turn 60
        encoderDrive(DRIVE_SPEED, 32, 32,0, 5.0); //move to ball 1
        encoderDrive(DRIVE_SPEED, -7, 7,0, 5.0);//turn 52
        //check ball
        encoderDrive(DRIVE_SPEED, 16, 16,0, 5.0);//move to ball two
        //check
        encoderDrive(DRIVE_SPEED, 16, 16,0, 5.0);//move to ball three
        //check

    //code to sqver
        encoderDrive(DRIVE_SPEED, 16, 16,0, 5.0);//move to turn position
        encoderDrive(DRIVE_SPEED, 4.2, -4.2,0, 5.0);//35%(because Dovi. 125 deg) turn. note this turn is backing into itself
        encoderDrive(DRIVE_SPEED, -55, -55,0, 5.0);//move to square

     //to carter
        encoderDrive(DRIVE_SPEED, 12, -12,0, 5.0);//90 turn
        encoderDrive(DRIVE_SPEED, 80, 80,0, 5.0);



        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

    /*
     *  Method to perfmorm a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
    public void encoderDrive(double speed,
                             double leftInches, double rightInches, double riser,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;
        int newRiserTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = leftDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = rightDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);

            newRiserTarget = landerRiser.getCurrentPosition() + (int)(riser * COUNTS_PER_INCH);

            leftDrive.setTargetPosition(newLeftTarget);
            rightDrive.setTargetPosition(newRightTarget);

            landerRiser.setTargetPosition(newRiserTarget);

            // Turn On RUN_TO_POSITION
            leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            landerRiser.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            leftDrive.setPower(Math.abs(speed));
            rightDrive.setPower(Math.abs(speed));
            landerRiser.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (leftDrive.isBusy() && rightDrive.isBusy()||landerRiser.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d :%7d", newLeftTarget,  newRightTarget, newRiserTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d :%7d",
                        leftDrive.getCurrentPosition(),
                        rightDrive.getCurrentPosition(),
                        landerRiser.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            leftDrive.setPower(0);
            rightDrive.setPower(0);
            landerRiser.setPower(0);

            // Turn off RUN_TO_POSITION
            leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            landerRiser.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move
        }
    }
    private void setUp(){
     /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive  = hardwareMap.get(DcMotor.class, "right_drive");
        landerRiser  = hardwareMap.get(DcMotor.class, "lander_riser");


        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");
        telemetry.update();

        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        landerRiser.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        landerRiser.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0",  "Starting at %7d :%7d :%7d",
                leftDrive.getCurrentPosition(),
                rightDrive.getCurrentPosition(),
                landerRiser.getCurrentPosition());
        telemetry.update();
    }

}