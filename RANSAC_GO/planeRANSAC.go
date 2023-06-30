/* Name: Meet Mehta
 * ID: 300261159
 * CSI 2120: Project 2
 * ------------------------------
 * READ ME:
 * In order to run the program, enter the following: go run planeRANSAC.go nameoftestfile .99 xx yy
 * Where xx and yy represent the percentage and eps respectively
 *
 * I used the following code to run the program: go run planeRANSAC.go PointCloud1 .99 30 .2
 * Replace the value of PointCloud1 file with other test file name and modify the value of Confidence percentage and eps accordingly
 *
 */

package main

import (
	"bufio"
	"fmt"
	"log"
	"math"
	"math/rand"
	"os"
	"strconv"
	"strings"
	"time"
)

type Point3D struct {
	X float64
	Y float64
	Z float64
}
type Plane3D struct {
	A float64
	B float64
	C float64
	D float64
}
type Plane3DwSupport struct {
	Plane3D
	SupportSize int
}
type InlierResult struct {
	ModelInliers int
	Inliers      []Point3D
}

var points []Point3D
var eps float64
var counter int = 0
var inputFilename string

// Reads an XYZ file and returns a slice of Point3D
func ReadXYZ(filename string) []Point3D {
	// Open the file
	file, err := os.Open(filename)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	// Read the data from the file
	scanner := bufio.NewScanner(file)
	var points []Point3D
	var skippedFirstLine bool
	for scanner.Scan() {
		line := scanner.Text()
		if !skippedFirstLine {
			skippedFirstLine = true
			continue
		}
		fields := strings.Fields(line)
		if len(fields) != 3 {
			continue
		}
		x, err := strconv.ParseFloat(fields[0], 64)
		if err != nil {
			continue
		}
		y, err := strconv.ParseFloat(fields[1], 64)
		if err != nil {
			continue
		}
		z, err := strconv.ParseFloat(fields[2], 64)
		if err != nil {
			continue
		}
		points = append(points, Point3D{X: x, Y: y, Z: z})
	}
	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}
	return points
}

// saves a slice of Point3D into an XYZ file
func SaveXYZ(filename string, points []Point3D) {
	// Open the file for writing
	file, err := os.Create(filename)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	// Write each point to the file in XYZ format
	for _, point := range points {
		_, err = fmt.Fprintf(file, "%.5f %.5f %.5f\n", point.X, point.Y, point.Z)
		if err != nil {
			log.Fatal(err)
		}
	}
}

// computes the distance between points p1 and p2
func (p1 *Point3D) GetDistance(p2 *Point3D) float64 {
	return math.Sqrt(math.Pow(p1.X-p2.X, 2) + math.Pow(p1.Y-p2.Y, 2) + math.Pow(p1.Z-p2.Z, 2))
}

// computes the plane defined by a set of 3 points
func GetPlane(points []Point3D) Plane3D {
	if len(points) != 3 {
		return Plane3D{}
	}

	// Compute the normal vector of the plane
	v1 := Point3D{X: points[1].X - points[0].X, Y: points[1].Y - points[0].Y, Z: points[1].Z - points[0].Z}
	v2 := Point3D{X: points[2].X - points[0].X, Y: points[2].Y - points[0].Y, Z: points[2].Z - points[0].Z}
	n := Point3D{X: v1.Y*v2.Z - v1.Z*v2.Y, Y: v1.Z*v2.X - v1.X*v2.Z, Z: v1.X*v2.Y - v1.Y*v2.X}
	norm := math.Sqrt(n.X*n.X + n.Y*n.Y + n.Z*n.Z)
	if norm == 0 {
		return Plane3D{}
	}
	n.X /= norm
	n.Y /= norm
	n.Z /= norm

	// Compute the plane coefficients
	plane := Plane3D{A: n.X, B: n.Y, C: n.Z}
	plane.D = -1 * (plane.A*points[0].X + plane.B*points[0].Y + plane.C*points[0].Z)

	return plane
}

// computes the number of required RANSAC iterations
func GetNumberOfIterations(confidence float64, percentageOfPointsOnPlane float64) float64 {
	percentageOfPointsOnPlane = percentageOfPointsOnPlane / 100.0
	numberOfIterations := math.Ceil((math.Log(1-confidence) / math.Log(2)) / (math.Log(1-math.Pow(percentageOfPointsOnPlane, 3)) / math.Log(2)))

	return numberOfIterations
}

// computes the support of a plane in a set of points
func GetSupport(plane Plane3D, points []Point3D, eps float64) Plane3DwSupport {
	var supportSize int
	for _, point := range points {
		dist := point.GetDistance(&Point3D{X: -plane.D / plane.A, Y: -plane.D / plane.B, Z: -plane.D / plane.C})
		if dist <= eps {
			supportSize++
		}
	}
	return Plane3DwSupport{Plane3D: plane, SupportSize: supportSize}
}

// extracts the points that supports the given plane and returns them as a slice of points
func GetSupportingPoints(plane Plane3D, points []Point3D, eps float64) []Point3D {
	var supportingPoints []Point3D
	for _, point := range points {
		p1 := &Point3D{X: plane.A, Y: plane.B, Z: plane.C}
		p2 := &Point3D{X: point.X, Y: point.Y, Z: point.Z}
		if p1.GetDistance(p2) <= eps {
			supportingPoints = append(supportingPoints, point)
		}
	}
	return supportingPoints
}

// creates a new slice of points in which all points belonging to the plane have been removed
func RemovePlane(plane Plane3D, points []Point3D, eps float64) []Point3D {
	var remainingPoints []Point3D
	for _, point := range points {
		dist := math.Abs(plane.A*point.X+plane.B*point.Y+plane.C*point.Z+plane.D) / math.Sqrt(plane.A*plane.A+plane.B*plane.B+plane.C*plane.C)
		if dist > eps {
			remainingPoints = append(remainingPoints, point)
		}
	}
	return remainingPoints
}

// runs the RANSAC algorithm on a set of points to find the best plane
func Ransac(points []Point3D, confidence float64, eps float64, percentageOfPointsOnPlane float64, numIterations float64) {
	// Initialize variables to hold the best plane and its support size
	var bestModelInliers int = 0
	iterations := int(numIterations)
	var temp []Point3D
	var bestPoints []Point3D
	// Loop over the number of iterations
	for i := 0; i < iterations; i++ {

		// Select three random points
		rand.Seed(time.Now().UnixNano())
		randIndexes := rand.Perm(len(points))

		//Creating three Random Points
		threePoints := []Point3D{points[randIndexes[0]], points[randIndexes[1]], points[randIndexes[2]]}

		//Creating a plane using the three randomly selected points
		plane := GetPlane(threePoints)

		//creating a temp array to hold the three random points
		temp = append(temp, threePoints...)

		//Finding the inliers based on the eps value and assigning it to the temp array
		inliersResult := GetInliers(plane, points, eps)
		inliers := inliersResult.Inliers
		modelInliers := inliersResult.ModelInliers
		temp = append(temp, inliers...)

		// Assigning the temp array which holds the inliers, to the bestPoints array if it satisfies the condition
		if modelInliers > bestModelInliers {
			bestModelInliers = modelInliers
			if len(bestPoints) == 0 {
				bestPoints = temp
			} else {
				bestPoints = []Point3D{}
				bestPoints = temp
			}
		}
		// Reset temp array
		temp = nil

		// Number of threads determined from experimental analysis. Please see the report for details
		for i := 1; i <= 12; i++ {
			start := time.Now()
			processPoints(plane, points, i)
			time.Since(start)
			//The line below was used to print the time of execution of each thread to plot the graph.
			//Based on the graph, we deduced that the most optimal number of threads is 12

			//fmt.Printf("Number of threads: %d, Runtime: %s\n", i, elapsed)
		}
	}

	// Saving three files with different names based on requirements
	switch counter {
	case 0:
		SaveXYZ(inputFilename+"_p1.xyz", bestPoints)
		counter++
	case 1:
		SaveXYZ(inputFilename+"_p2.xyz", bestPoints)
		counter++
	case 2:
		SaveXYZ(inputFilename+"_p3.xyz", bestPoints)
		counter++
	default:
		SaveXYZ(inputFilename+"_p0.xyz", bestPoints)
		counter++
	}

	//remove the the best points from the Global Point Cloud
	removePoints(bestPoints)

}

// Remove the points passed in the arguement of the functtion from the global variable
func removePoints(bestPoints []Point3D) {
	for _, bp := range bestPoints {
		for i, p := range points {
			if bp == p {
				points = append(points[:i], points[i+1:]...)
				break
			}
		}
	}
}

// Computes the inliers of a plane in a set of points
func GetInliers(plane Plane3D, points []Point3D, eps float64) InlierResult {
	var inliers []Point3D
	var modelInliers int
	for _, point := range points {
		dist := math.Abs(plane.A*point.X+plane.B*point.Y+plane.C*point.Z+plane.D) / math.Sqrt(plane.A*plane.A+plane.B*plane.B+plane.C*plane.C)
		if dist <= eps {
			modelInliers++
			inliers = append(inliers, point)
		}
	}
	return InlierResult{ModelInliers: modelInliers, Inliers: inliers}
}

// process the points with the given plane and number of threads
func processPoints(plane Plane3D, points []Point3D, numThreads int) []Point3D {
	// divide the points into equal parts
	chunkSize := len(points) / numThreads
	results := make(chan []Point3D, numThreads)
	for i := 0; i < numThreads; i++ {
		start := i * chunkSize
		end := (i + 1) * chunkSize
		if i == numThreads-1 {
			end = len(points)
		}
		go func(p []Point3D) {
			results <- GetSupportingPoints(plane, p, eps)
		}(points[start:end])
	}

	// combine the results from all threads
	var result []Point3D
	for i := 0; i < numThreads; i++ {
		result = append(result, <-results...)
	}

	return result
}

func main() {

	// read the input arguments
	if len(os.Args) != 5 {
		log.Fatal("Usage: RANSAC <input file> <confidence> <percentage> <eps>")
	}
	inputFilename = os.Args[1]
	confidence, err := strconv.ParseFloat(os.Args[2], 64)
	if err != nil {
		log.Fatal(err)
	}
	percentage, err := strconv.ParseFloat(os.Args[3], 64)
	if err != nil {
		log.Fatal(err)
	}
	eps, err = strconv.ParseFloat(os.Args[4], 64)
	if err != nil {
		log.Fatal(err)
	}

	// Read the points from the input file
	points = ReadXYZ(inputFilename + ".xyz")
	fmt.Printf("Read %d points from file.\n", len(points))

	// Compute the required number of iterations
	numIterations := GetNumberOfIterations(confidence, percentage)

	// Run the RANSAC algorithm 4 times to print P1, P2, P3, P0 files for each test file
	Ransac(points, confidence, eps, percentage, numIterations)
	Ransac(points, confidence, eps, percentage, numIterations)
	Ransac(points, confidence, eps, percentage, numIterations)
	Ransac(points, confidence, eps, percentage, numIterations)

}
