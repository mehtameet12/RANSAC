


# **RANSAC Algorithm application using JAVA, GO, SCHEME, and PROLOG**
# **Problem to Solve**
The intelligent vehicles of the future will be equipped with a multitude of sensors to capture information about the surrounding scene and thus being able to autonomously navigate. One of these sensors is the Laser Scanner or LiDAR (Light Detection And Ranging). Using a LiDAR, the vehicle can scan the scene in front by sweeping few laser beams (typically between 8 to 64 lasers). 

Each time a laser beam hit an object, the laser light bounces back to the LiDAR from which a precise distance can be estimated. A complete scan of the scene with these lasers will therefore generate a large set of 3D points (also called point cloud) that correspond to the structure of the scene. The figure below shows a typical point cloud captured by a car equipped with a LiDAR. A view of the same scene captured by a camera is also shown. 
# **Original Picture**
![](Photos/Aspose.Words.484a949d-ba19-46cc-9e5b-052fe440a649.002.png)

*Figure: Capture of parking lot from a vehicle equipped with LIDAR.*
# **Original Point Cloud**
Below is the 3D image of the point cloud that we will be working with. 

![](Photos/Aspose.Words.484a949d-ba19-46cc-9e5b-052fe440a649.003.png)

*Figure: PointCloud of the original image.*

As it can be seen, the objects of the scene will be represented by clusters of 3D points. Some of the laser beams will also hit the road and the buildings’ facades. These later are generally large planes. The objective of the project is to identify the main planar structures in the captured scene. 
# <a name="_3at9u9s4e0vp"></a><a name="_au51mny0sx6"></a>**Overview**
We will implement an algorithm that will detect the dominant planes in a cloud of 3D points. The dominant plane of a set of 3D points is the plane that contains the largest number of points. A 3D point is contained in a plane if it is located at a distance less than eps (ε) from that plane. To detect these planes, you will use the RANSAC algorithm. 
# **RANSAC**
RANSAC is an iterative algorithm that is used to identify a geometric entity (or model) from a set of data that contains a large number of outliers (data that does not belong to the model). It proceeds by randomly drawing the minimum number of samples required to estimate the parameters of a model instance and then validate it by counting the number of additional samples that support the computed model. 

In our case, we are looking for a planar structure, made of several points, while most of the points in the set are outside that plane. The seek geometric entity is therefore a plane of the form: ax+by+cz=d. A minimum of 3 points is required to compute the equation of a plane.
# **Algorithm**
The algorithm used in this experiment is a plane fitting algorithm that takes a plane and a slice of 3D points as input and returns the slice of points that are closest to the plane within a given tolerance (eps: value entered by the user). The algorithm uses the RANSAC algorithm to fit a plane to the input points, and the number of iterations used by RANSAC is proportional to the number of threads used. 
# <a name="_4p7xi5bvhxdr"></a>**Experimental Design**
In this experiment, we explore the relationship between the number of threads and the runtime of RANSAC algorithm. We aim to find the optimal number of threads to create to minimize the runtime of the algorithm. To do this, we measure the runtime for different configurations of threads and plot the results.

To determine the optimal number of threads to create, we run the algorithm for different numbers of threads, from 1 to 12. For each configuration, we measure the runtime of the algorithm and record the results. We repeat the experiment multiple times to ensure the results are consistent.
# <a name="_yyrhu7ml5bea"></a>**Result Analysis**
We plot the results of the experiment on a graph showing the runtime versus the number of threads for each configuration.
## <a name="_qaujy5oisq9y"></a>
## <a name="_41nsol7cxmib"></a>Raw Data:


|**Threads**|**Run Time**|
| :-: | :-: |
|1|7\.944042|
|2|3\.942458|
|3|2\.774459|
|4|2\.084583|
|5|2\.137375|
|6|1\.816958|
|7|1\.614958|
|8|1\.929958|
|9|1\.776458|
|10|1\.766166|
|11|1\.532416|
|12|1\.385291|
|13|1\.664041|
|14|1\.666708|
|15|2\.337375|
|16|1\.462208|

## <a name="_vwvwuj6kjugb"></a>
## <a name="_r51c98n9i1xt"></a>Graph showing Threads VS Run Time

![](Photos/Aspose.Words.484a949d-ba19-46cc-9e5b-052fe440a649.004.png)

*Figure: Graph showing the number of threads VS Run Time for each thread.*
# <a name="_ppksk499tgsv"></a>**Discussion**
The results of the experiment show that the runtime of the plane fitting algorithm decreases as the number of threads increases, up to the point where the number of threads is 12 after which it further increases or has diminishing returns. The graph shows that the optimal number of threads for this algorithm is between 11 and 12, where the runtime is minimized. Beyond the 12th thread, the performance gains are minimal if there are any.
# <a name="_7wljxqequ51z"></a>**Conclusion**
In conclusion, this project implemented the RANSAC algorithm to accurately detect the dominant planes in 3D point clouds obtained from LiDAR scans. The algorithm efficiently handled outliers and iteratively refined plane estimation by randomly sampling subsets of points. The implementation showcased proficiency in Java, Go, Scheme, and Prolog, highlighting its versatility. The desired confidence level was achieved by calculating the optimal number of iterations based on the percentage of points supporting the dominant plane. The output files included the identified dominant planes and the original point cloud with the plane points removed. Overall, this project successfully demonstrated the ability to detect dominant planes in 3D point clouds using RANSAC, showcasing understanding of the algorithm's principles and efficient programming skills in multiple languages.

