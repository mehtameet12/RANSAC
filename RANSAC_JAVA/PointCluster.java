/*Name: Meet Mehta
 * ID: 300261159
 * CSI 2120: Project 1
 */

import java.io.*;
import java.util.Scanner;

public class PointCluster{
	public static void main (String args[]) throws IOException{
		String filename1 = "PointCloud1.xyz";
        String filename2 = "PointCloud2.xyz";
        String filename3 = "PointCloud3.xyz";

        System.out.println("Enter the eps value: ");
        double eps = Double.parseDouble(new Scanner(System.in).nextLine());;
        System.out.println("Enter the Percentage of Points on a plane to make it a dominant plane:");
        double percentageOfPointsOnPlane = Double.parseDouble(new Scanner(System.in).nextLine());

        PlaneRANSAC ranSac1 = new PlaneRANSAC(eps, percentageOfPointsOnPlane, filename1);
        ranSac1.readFile();
        ranSac1.run(0,filename1);
        ranSac1.run(0,filename1);
        ranSac1.run(0,filename1);

        PlaneRANSAC ranSac2 = new PlaneRANSAC(eps, percentageOfPointsOnPlane, filename2);
        ranSac2.readFile();
        ranSac2.run(0,filename2);
        ranSac2.run(0,filename2);
        ranSac2.run(0,filename2);

        PlaneRANSAC ranSac3 = new PlaneRANSAC(eps, percentageOfPointsOnPlane, filename3);
        ranSac3.readFile();
        ranSac3.run(0,filename3);
        ranSac3.run(0,filename3);
        ranSac3.run(0,filename3);

	}
	
}