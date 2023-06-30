/*Name: Meet Mehta
 * ID: 300261159
 * CSI 2120: Project 1
 */

public class Plane3D{

	protected double a;
	protected double b;
	protected double c;
	protected double d;

	/** Constructor of the class that will accept the 
	 * points (with each point having predefined values
	 * of x, y, z) to generate a plane
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	public Plane3D(Point3D p1, Point3D p2, Point3D p3){
		a = (p2.getY() - p1.getY()) * (p3.getZ() - p1.getZ()) - (p2.getZ() - p1.getZ()) * (p3.getY() - p2.getY());
        b = (p2.getZ() - p1.getZ()) * (p3.getX() - p1.getX()) - (p2.getX() - p1.getX()) * (p3.getZ() - p2.getZ());
        c = (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p2.getY() - p1.getY()) * (p3.getX() - p2.getX());
        d = -1 * ((a * p1.getX()) + (b * p1.getY()) + (c * p1.getZ()));
	}

	/** Constructor of the class that will accept the four 
	 * values as parameters required to create a plane from 
	 * the equation ax + by + cz + d = 0
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 */
	public Plane3D(double a, double b, double c, double d){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	/** Getting the distance of the given point (p4) from the plane 
	 * @param p4
	 */
	public double getDistance(Point3D p4){
		double numerator, denominator, answer;
		numerator = (this.a * p4.getX()) + (this.b * p4.getY()) + (this.c * p4.getZ()) + this.d;
		denominator = Math.sqrt(Math.pow(this.a, 2) + Math.pow(this.b, 2) + Math.pow(this.c, 2));
		answer = Math.abs(numerator/denominator);
		return answer;
	}
}