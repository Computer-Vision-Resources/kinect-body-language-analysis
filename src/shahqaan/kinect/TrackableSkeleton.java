/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shahqaan.kinect;

import java.awt.Graphics2D;
import java.util.HashMap;
import org.OpenNI.Point3D;
import org.OpenNI.SkeletonJoint;
import org.OpenNI.SkeletonJointPosition;

/**
 *
 * @author Shahqaan Qasim
 */
public class TrackableSkeleton implements AbstractTrackable {
    
    
    /**
     * BODY STATE MATRIX
     * 
     * Body         0
     * Head         1
     * Shoulder     2
     * Chest        3
     * Arms         4
     * Knees        5
     * 
     * 
     * N/A                  0
     * forward              1
     * backward             2
     * bent                 3
     * straight             4
     * forward or upward    5
     * lifted upward        6
     * crossed              7
     * opened               8
     */
    public int[] bodyState = new int[6];
    
    /**
     * Variables for arm and hands
     */
    private double armAllowedAngle = 0.35; 
    private double armSlightExtension = 150.0;
    private double armFullExtension = 200.0;
    private double armForwardThreshold = -250.0;
    private double armBackwardThreshold = 150.0;
    
    
    private double kneeBendThreshold = -70.0;
    
    private double headBendThreshold = -35.0;
    private double headBackThreshold = 50.0;
    
    private double chestBendThreshold = -30.0;
    private double chestBackThreshold = 40.0;
    
    public TrackableSkeleton() {
        for (int i = 0 ; i < 6 ; i++) {
            bodyState[i] = 0;
        }
    }
      

    /**
     * 
     * @param x
     * @param y
     * @param z
     * @param a
     * @param b
     * @param c
     * @param p
     * @param q
     * @param r
     * @return
     *      angle in radians
     */
    private double findAngle(
            float x, float y, float z,
            float a, float b, float c,
            float p, float q, float r) {
        
        float i, j, k;
        i = ((y - b) * (r - z)) - ((z - c) * (q - y));
        j = ((x - a) * (r - z)) - ((z - c) * (p - x));
        k = ((x - a) * (q - y)) - ((y - b) * (p - x));

        i = i * i;
        j = j * j;
        k = k * k;

        double numerator = Math.sqrt(i + j + k);

        float v1 = (x-a)*(x-a);
        float v2 = (y-b)*(y-b);
        float v3 = (z-c)*(z-c);

        float u1 = (p-x)*(p-x);
        float u2 = (q-y)*(q-y);
        float u3 = (r-z)*(r-z);

        double denom1 = Math.sqrt(v1 + v2 + v3);
        double denom2 = Math.sqrt(u1 + u2 + u3);

        double finalV = denom1 * denom2;
        finalV = numerator / finalV;

        double angle = Math.sinh(finalV);
        
        return angle;

    }
    
    private float xFromTorso(Skeletons skeletons, HashMap<SkeletonJoint, SkeletonJointPosition> skel, float x) {
        Point3D point = skeletons.getJointPos(skel, SkeletonJoint.TORSO);
        if (point != null) {
            return (x - point.getX());
        }
        else {
            return 0;
        }
    }
    private float yFromTorso(Skeletons skeletons, HashMap<SkeletonJoint, SkeletonJointPosition> skel, float y) {
        Point3D point = skeletons.getJointPos(skel, SkeletonJoint.TORSO);
        if (point != null) {
            return (y - point.getX());
        }
        else {
            return 0;
        }
    }
    private float zFromTorso(Skeletons skeletons, HashMap<SkeletonJoint, SkeletonJointPosition> skel, float z) {
        Point3D point = skeletons.getJointPos(skel, SkeletonJoint.TORSO);
        if (point != null) {
            return (z - point.getZ());
        }
        else {
            return 0;
        }
    }

    
    private void testRightKnee(Skeletons skeletons, HashMap<SkeletonJoint, SkeletonJointPosition> skel) {
        float x = 0, y = 0, z = 0;
        float a = 0, b = 0, c = 0;
        float p = 0, q = 0, r = 0;
        float u = 0, v = 0, w = 0;
        
         
        try {
            /*
            Point3D point = skeletons.getJointPos(skel, SkeletonJoint.RIGHT_HIP);
            if (point == null) {
            }
            else {
                a = point.getX();
                b = point.getY();
                c = point.getZ();
            }
            * */
            Point3D point = skeletons.getJointPos(skel, SkeletonJoint.RIGHT_KNEE);
            if (point == null) {
            }
            else {
                x = point.getX();
                y = point.getY();
                z = point.getZ();
            }
            /*
            point = skeletons.getJointPos(skel, SkeletonJoint.RIGHT_FOOT);
            if (point == null) {
            }
            else {
                p = point.getX();
                q = point.getY();
                r = point.getZ();
            }*/
            
            // Angle will tell whether the arm is straight or not
            // double angle = this.findAngle(x, y, z, a, b, c, p, q, r);
       
            // since it's a right hand, more positive value means
            // it is extending outwards
            // and more negative value means it is crossed inwards
            // float xFromTorso = this.xFromTorso(skeletons, skel, p);
            
            // if negative then arm is forward
            // float zFromTorso = this.zFromTorso(skeletons, skel, r);
            
            float zFromTorso = this.zFromTorso(skeletons, skel, z);
            if (zFromTorso < this.kneeBendThreshold) {
                bodyState[5] = 3;
            }
            else {
                bodyState[5] = 4;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }        
        
        
    }
    
    private void testRightArm(Skeletons skeletons, HashMap<SkeletonJoint, SkeletonJointPosition> skel) {
        float x = 0, y = 0, z = 0;
        float a = 0, b = 0, c = 0;
        float p = 0, q = 0, r = 0;
        float u = 0, v = 0, w = 0;
         
        try {
            Point3D point = skeletons.getJointPos(skel, SkeletonJoint.RIGHT_SHOULDER);
            if (point == null) {
            }
            else {
                a = point.getX();
                b = point.getY();
                c = point.getZ();
            }
            point = skeletons.getJointPos(skel, SkeletonJoint.RIGHT_ELBOW);
            if (point == null) {
            }
            else {
                x = point.getX();
                y = point.getY();
                z = point.getZ();
            }
            point = skeletons.getJointPos(skel, SkeletonJoint.RIGHT_HAND);
            if (point == null) {
            }
            else {
                p = point.getX();
                q = point.getY();
                r = point.getZ();
            }
            
            // Angle will tell whether the arm is straight or not
            // double angle = this.findAngle(x, y, z, a, b, c, p, q, r);
           
            // since it's a right hand, more positive value means
            // it is extending outwards
            // and more negative value means it is crossed inwards
            float xFromTorso = this.xFromTorso(skeletons, skel, p);
            
            // if negative then arm is forward
            float zFromTorso = this.zFromTorso(skeletons, skel, r);
            
            
            // Check if arm is extended
            if (xFromTorso > this.armFullExtension) {
                bodyState[4] = 8;
            }
            else {
                
                // check if arm is crossed
                if (xFromTorso < 0.0) {
                    bodyState[4] = 7;
                }
                else {

                    // if not extended, check if they are forward
                    if (zFromTorso < this.armForwardThreshold) {
                        bodyState[4] = 1;
                    }
                    else {
                        bodyState[4] = 4;
                    }
                }
            }

        
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }        
        
    }
    private void testLeftArm(Skeletons skeletons, HashMap<SkeletonJoint, SkeletonJointPosition> skel) {
        float x = 0, y = 0, z = 0;
        float a = 0, b = 0, c = 0;
        float p = 0, q = 0, r = 0;
        float u = 0, v = 0, w = 0;
         
        try {
            Point3D point = skeletons.getJointPos(skel, SkeletonJoint.LEFT_SHOULDER);
            if (point == null) {
            }
            else {
                a = point.getX();
                b = point.getY();
                c = point.getZ();
            }
            point = skeletons.getJointPos(skel, SkeletonJoint.LEFT_ELBOW);
            if (point == null) {
            }
            else {
                x = point.getX();
                y = point.getY();
                z = point.getZ();
            }
            point = skeletons.getJointPos(skel, SkeletonJoint.LEFT_HAND);
            if (point == null) {
            }
            else {
                p = point.getX();
                q = point.getY();
                r = point.getZ();
            }
            
            double angle = this.findAngle(x, y, z, a, b, c, p, q, r);

            // find the x, y and z distances between body and arm
            point = skeletons.getJointPos(skel, SkeletonJoint.TORSO);
            if (point != null) {
                u = point.getX();
                v = point.getY();
                w = point.getZ();
            }
            // x increases if i move right
            // z increases if i move back backwards
            System.out.println("X = " + (p - u) + " Y = " + (q - v) + " Z = " + (r - w));
        
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }        
        
    }
  
    private void testChest(Skeletons skeletons, HashMap<SkeletonJoint, SkeletonJointPosition> skel) {
        float x = 0, y = 0, z = 0;
        Point3D point = skeletons.getJointPos(skel, SkeletonJoint.NECK);
        if (point != null) {
            x = point.getX();
            y = point.getY();
            z = point.getZ();
        }
        float zFromTorso = this.zFromTorso(skeletons, skel, 0);
        if (zFromTorso < this.chestBendThreshold) {
            bodyState[3] = 1;
        }
        else {
            if (zFromTorso > this.chestBackThreshold) {
                bodyState[3] = 2;
            }
            else {
                bodyState[3] = 0;
            }
        }
    }
   
    private void testHead(Skeletons skeletons, HashMap<SkeletonJoint, SkeletonJointPosition> skel) {
        float x = 0, y = 0, z = 0;
        Point3D point = skeletons.getJointPos(skel, SkeletonJoint.HEAD);
        if (point != null) {
            x = point.getX();
            y = point.getY();
            z = point.getZ();
        }
        float headFromTorso = this.zFromTorso(skeletons, skel, z);
        if (headFromTorso < this.headBendThreshold) {
            bodyState[1] = 3;
        }
        else {
            if (headFromTorso > this.headBackThreshold) {
                bodyState[1] = 2;
            }
            else {
                bodyState[1] = 4;
            }
        }
        /*
        point = skeletons.getJointPos(skel, SkeletonJoint.NECK);
        if (point != null) {
            x = point.getX();
            y = point.getY();
            z = point.getZ();
        }
        float neckFromTorso = this.zFromTorso(skeletons, skel, z);
        ExtendedSkeletons.MESSAGE_2 = "N " + (int)neckFromTorso;
        
        point = skeletons.getJointPos(skel, SkeletonJoint.RIGHT_COLLAR);
        if (point != null) {
            x = point.getX();
            y = point.getY();
            z = point.getZ();
        }        
        float collarFromTorso = this.zFromTorso(skeletons, skel, z);
        ExtendedSkeletons.MESSAGE_3 = "C " + (int)collarFromTorso;
        
        point = skeletons.getJointPos(skel, SkeletonJoint.RIGHT_SHOULDER);
        if (point != null) {
            x = point.getX();
            y = point.getY();
            z = point.getZ();
        }        
        float shoulderFromTorso = this.zFromTorso(skeletons, skel, z);        
        ExtendedSkeletons.MESSAGE_4 = "S " + (int)shoulderFromTorso;
        * */
        
        
    }
    @Override
    public void track(Skeletons skeletons, HashMap<SkeletonJoint, SkeletonJointPosition> skel) {
        this.testRightArm(skeletons, skel);
        this.testRightKnee(skeletons, skel);
        // this.testChest(skeletons, skel);
        this.testHead(skeletons, skel);
        
        ExtendedSkeletons.bodyState = this.bodyState;
    }


}