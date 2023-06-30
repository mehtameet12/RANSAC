/*Name: Meet Mehta
 * ID: 300261159
 * CSI 2120: Project 1
 */

class Point3D{

    private double x,y,z;

    /** Constructor of Point3D class with values 0 for each variable
     */
    Point3D(){
        this.x = 0;
        this.y = 0;
        this.z = 0; 
    }

    /** Constructs a point containing 3d position of the point 
     * @param x
     * @param y
     * @param z 
     */
    Point3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /** return the value of x
     * @return x
     */
    public double getX(){
        return this.x;
    }

    /** return the value of y
     * @return y
     */
    public double getY(){
        return this.y;
    }

    /** return the value of z
     * @return z
     */
    public double getZ(){
        return this.z;
    }

    /** Set the value of x
     * @param x
     */
    public void setX(double x){
        this.x = x;
    }

    /** Set the value of y
     * @param y
     */
    public void setY(double y){
        this.y = y;
    }

    /** Set the value of z
     * @param z
     */
    public void setZ(double z){
        this.z = z;
    }
}