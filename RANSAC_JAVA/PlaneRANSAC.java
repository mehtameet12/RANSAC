/*Name: Meet Mehta
 * ID: 300261159
 * CSI 2120: Project 1
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JSpinner.NumberEditor;
import javafx.scene.effect.Light.Point;

public class PlaneRANSAC{
	
	// Class variables
	private double eps;
	private double percentageOfPointsOnPlane;
	private String filename;
	private int maxIterations;
	private PointCloud pointCloudObject;


	/** Default constructor of the class that assigns the maxIterations and assigns the 
	 * values to the varaibles as passed in the parameters
	 */
	public PlaneRANSAC(double eps, double percentageOfPointsOnPlane, String filename){
		this.eps = eps;
		this.percentageOfPointsOnPlane = percentageOfPointsOnPlane;
        this.filename = filename;
		maxIterations = getNumberOfIterations(0.99, this.percentageOfPointsOnPlane);
		
	}

	/** Constructor of the class that takes point cloud as a parameter
	 * @param pc
	 */
	public PlaneRANSAC(PointCloud pc){
		this.pointCloudObject = pc;
	}

	/** Function to set the epsilon value
	 * @param eps
	 */
	protected void setEps (double eps){
		this.eps = eps;
	}

	/** Function to return the epsilon value
	 * @return eps
	 */
	protected double getEps(){
		return this.eps;
	}

	/** Function that returns the estimated number of iterations required to
	 * obtain a certain level of confidence to identify a plane
	 * @param confidence
	 * @param percentageOfPointsOnPlane
	 * @return 
	 */
	protected int getNumberOfIterations(double confidence, double percentageOfPointsOnPlane){
		double numberOfIterations;
		percentageOfPointsOnPlane = percentageOfPointsOnPlane/100;
		numberOfIterations = Math.ceil( (Math.log(1-confidence)/Math.log(2)) / (Math.log(1-Math.pow(percentageOfPointsOnPlane,3))/Math.log(2)) );
		maxIterations = (int) numberOfIterations;
		return maxIterations;
	}

	/** Function that runs the RANSAC algorithm to identify the dominant plane
	 * of a given point cloud based on the number of iterations defined and the filename
	 * @param numberOfIterations
	 * @param filename
	 * 
	 * The concept of RANSAC algorithm was referenced from the following websites:
	 * https://towardsdatascience.com/3d-model-fitting-for-point-clouds-with-ransac-and-python-2ab87d5fd363
	 * https://medium.com/@ajithraj_gangadharan/3d-ransac-algorithm-for-lidar-pcd-segmentation-315d2a51351
	 * 
	 */
	protected void run(int numberOfIterations, String filename){
		//Defining the local variables
		numberOfIterations = maxIterations;
		int bestModelInliers = 0;
		Plane3D bestModel = null;
		ArrayList<Point3D> pointsArrayList;
		ArrayList<Point3D> bestPointsArrayList = new ArrayList<Point3D>();
		
		//Running the loop based on the calculated max Iterartions
		for (int i = 0; i<numberOfIterations; i++){
			
			//getting 3 random points from the PointCloud
			Point3D p1 = pointCloudObject.getPoint();
			Point3D p2 = pointCloudObject.getPoint();
			Point3D p3 = pointCloudObject.getPoint();

			//Creating the plane using the three random points
			Plane3D model = new Plane3D(p1, p2, p3);

			//instantiating the local arraylist and counter
			pointsArrayList = new ArrayList<Point3D>();
			int modelInliers = 0;

			//adding the previously defined three random points to the local array
			pointsArrayList.add(p1);
			pointsArrayList.add(p2);
			pointsArrayList.add(p3);

			/** iterating through all the points in the original PointCloud to check
			 * if the point is less than the user defined eps value. If that is the case, 
			 * the function adds and updates the local ArrayList 
			 */
			
			for(Point3D p : pointCloudObject.getArrayList()){
				if(model.getDistance(p) < eps){
					modelInliers++; 
					pointsArrayList.add(p); 
				}
			}
			if(modelInliers > bestModelInliers){
				bestModel = model;
				bestModelInliers = modelInliers;
				
				if(bestPointsArrayList.isEmpty()){
					bestPointsArrayList = (ArrayList)pointsArrayList.clone(); 
				}else{
					bestPointsArrayList.removeAll(bestPointsArrayList);
					bestPointsArrayList = (ArrayList)pointsArrayList.clone(); 
				}
				pointsArrayList.removeAll(pointsArrayList);
			}
		}
		
		//saving the points stored in the local ArrayList into the file
		pointCloudObject.save(filename, bestPointsArrayList);	

		/** removing the points that were now saved and considered as the dominant plane
		 * since those will not be used again for the next run of the algorithm
		 */
		pointCloudObject.removePointsFromArrayList(bestPointsArrayList);	
	}

	/** Function that reads the file and adds the points as point3D from the provided file
	 * into the pointCloud array
	 */
	protected void readFile(){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine();
            String line = br.readLine();
			pointCloudObject = new PointCloud(filename);
            while (line != null) {
				line = line.replaceAll("\\s", " ");
                String[] values = line.split(" ");
                Point3D points = new Point3D(Double.valueOf(values[0]), Double.valueOf(values[1]), Double.valueOf(values[2]));
                pointCloudObject.addPoint(points);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}