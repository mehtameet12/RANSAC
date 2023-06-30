/*Name: Meet Mehta
 * ID: 300261159
 * CSI 2120: Project 1
 */

import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PointCloud{
	
	// Class variables
	private ArrayList<Point3D> pointCloud;
	private String filename;
	private int counter;

	/** Default constructor of the class that creates an empty point cloud
	 */
	public PointCloud(){
		pointCloud = new ArrayList<Point3D>();
		counter=1;
	}

	/** Constructor of the class with a filename as parameter
	 * @param filename
	 */
	public PointCloud(String filename){
		pointCloud = new ArrayList<Point3D>();
		this.filename = filename;
		counter=1;
	}

	/** Adding a 3DPoint to the point cloud array
	 * @param pt
	 */
	protected void addPoint(Point3D pt){
		pointCloud.add(pt);
	}

	/** Getting a random point from the cloud
	 * @param pt
	 */
	protected Point3D getPoint(){
		Random rand  = new Random();
		int index = rand.nextInt(pointCloud.size());
		return pointCloud.get(index);
	}

	/** Method that returns the arrayslist containing the cluster
	 * @return pointArrayList
	 */
	protected ArrayList<Point3D> getArrayList(){
		return this.pointCloud;
	}

	/** Method that removes specified points, passed in an array list from the main pointCluster
	 * @param pointArrayList
	 */
	protected void removePointsFromArrayList(ArrayList<Point3D> pointArrayList){
		for(Point3D p: pointArrayList){
			pointCloud.remove(p);
		}
	}

	/** Method that saves the points to its respective file in the directory
	 * @param pt
	 * @param pointArrayList
	 */
	protected void save(String filename, ArrayList<Point3D> pointArrayList){
		//Creating a new folder called 'Output' to segregate the files
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		s = s+"/Output";
		File f = new File(s);
		f.mkdir();

		//Deciding the filename, based on the filename input
		String[] file = filename.split("\\.");
		filename = file[0]+"_p"+counter+".xyz";
		
		//Writing the P1, P2, and P3 files the represent the most dominant planes of the cluster
		try{
			FileWriter myWriter = new FileWriter(new File(f, filename));
			myWriter.write("x,y,z \n");
			for (int i =0; i<pointArrayList.size(); i++){
				myWriter.write(String.valueOf(pointArrayList.get(i).getX())+" "+String.valueOf(pointArrayList.get(i).getY())+" "+String.valueOf(pointArrayList.get(i).getZ())+"\n");
			}
			myWriter.close();
		}catch(IOException e){
			System.out.println("Error while writing the file");
			e.printStackTrace();
		}
		
		//Writing the P0 file, that represents the remaining points of the cluster
		if(counter==3){
			filename = file[0]+"_p0.xyz";
			try{
				FileWriter myWriter = new FileWriter(new File(f,filename));
				myWriter.write("x,y,z \n");
				for(int i=0; i<pointCloud.size(); i++){
					myWriter.write(String.valueOf(pointCloud.get(i).getX())+" "+String.valueOf(pointCloud.get(i).getY())+" "+String.valueOf(pointCloud.get(i).getZ())+"\n");
				}
				myWriter.close();
			}catch(IOException e){
				System.out.println("Error while writing the file");
				e.printStackTrace();
			}
		}
		counter++;
	}

	/** Iterator method to loop over the points in the point cloud
	 */
	protected Iterator<Point3D> iterator(){
		return new MyIterator<Point3D>();
	}
	/** MyIterator class that implements the Iterator class which will
	 * loop over the list of points present in the arraylist when called 
	 * upon
	 */
	public class MyIterator<Point3D> implements Iterator<Point3D>{
		private int index;
		
		public MyIterator() {
			index=0;
		}

		@Override
		public boolean hasNext() {
			if(index >= pointCloud.size()) {
				return false ;
			}else {
				return true ;
			}
		}

		@Override
		public Point3D next() {
			return (Point3D)pointCloud.get(index++);
		}

		@Override
        public void remove() {
            pointCloud.remove(index);
        }
	}
}
