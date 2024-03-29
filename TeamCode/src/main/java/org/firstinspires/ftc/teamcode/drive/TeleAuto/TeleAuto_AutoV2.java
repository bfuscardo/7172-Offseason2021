package org.firstinspires.ftc.teamcode.drive.TeleAuto;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.ProfileAccelerationConstraint;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.util.RobotHardwareOBV2;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

import java.util.ArrayList;
import java.util.Arrays;

@Autonomous
public class TeleAuto_AutoV2 extends LinearOpMode
{
    //Create elapsed time and robot hardware objects
    RobotHardwareOBV2 robot   = new RobotHardwareOBV2();
    ElapsedTime runtime = new ElapsedTime();

    //ArrayList<TeleAuto_Event> events = TeleAuto_PosStorage.lastEvents;

    ArrayList<TeleAuto_Event> events = new ArrayList<TeleAuto_Event>();

    @Override
    public void runOpMode() {
        //Init hardware map and set motor directions + servo postions
        robot.init(hardwareMap);

        //Set RR start Pose
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        com.acmerobotics.roadrunner.geometry.Pose2d startPose = events.get(0).getPos();
        drive.setPoseEstimate(startPose);

        try {
            File myTrajectories = new File("Trajectories.txt");
            Scanner myTrajReader = new Scanner(myTrajectories);
            int count = 0;

            while (myTrajReader.hasNextLine()) {
                String line = myTrajReader.nextLine();
                String[] peices = line.split(", ");
                events.add(new TeleAuto_Event(new Pose2d(Double.parseDouble(peices[0]), Double.parseDouble(peices[1]), Math.toRadians(Double.parseDouble(peices[2]))),
                        Integer.valueOf(peices[3]), Integer.valueOf(peices[4])));

                count++;
            }
            myTrajReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        for(int i = 1; i < events.size(); i++){
            Trajectory move = drive.trajectoryBuilder(events.get(i-1).getPos())
                    .lineToLinearHeading(
                            new com.acmerobotics.roadrunner.geometry.Pose2d(events.get(i).getPos().getX(), events.get(i).getPos().getY(),
                                    Math.toRadians(events.get(i).getPos().getHeading())),
                            new MinVelocityConstraint(
                                    Arrays.asList(
                                            new AngularVelocityConstraint(DriveConstants.MAX_ANG_VEL),
                                            new MecanumVelocityConstraint(events.get(i).getSpeed(), DriveConstants.TRACK_WIDTH)
                                    )
                            ),
                            new ProfileAccelerationConstraint(DriveConstants.MAX_ACCEL)
                    )
                    .build();

            drive.followTrajectory(move);

            sleep(1000);

            /*
            if(index == 1){
                etc.
            }

            sleep(1000);

             */
        }


    }



}



