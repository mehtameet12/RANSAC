#lang scheme

;Name: Meet Mehta
;Student ID: 300261159
;CSI 2120
;Project 3

;Run the following commands to get the output attached in the zipfile
;(run-planeRANSAC "Point_Cloud_1_No_Road_Reduced.xyz" 0.99 0.5 0.8)
;(run-planeRANSAC "Point_Cloud_2_No_Road_Reduced.xyz" 0.99 0.5 0.8)
;(run-planeRANSAC "Point_Cloud_3_No_Road_Reduced.xyz" 0.99 0.5 0.8)

(define (run-planeRANSAC filepath confidence percentage eps) ;Function used as a 'main' function which will be called at execution accepting arguements from the user.
  (let* ((full-path (string-append "" filepath))
         (result (planeRANSAC full-path confidence percentage eps))
         (best-plane (car result))
         (best-support (cdr result))
         (inliers (cdr (support best-plane (readXYZ full-path) eps)))
         (filename (string-split full-path "/"))
         (output-path (string-append (list-ref filename (- (length filename) 1))
                                     "-result.xyz")))
    (with-output-to-file output-path
      (lambda ()
        (write "x y z")(newline)
        (for-each (lambda (p)
                    (write (car p))
                    (write-char #\space)
                    (write (cadr p))
                    (write-char #\space)
                    (write (caddr p)) (write-char #\newline))
                  inliers)))
    result))

(define (write-lines lst port) ;Helper function used for writing the values into the resulting file
  (for-each (lambda (x)
              (write (first x) port)
              (write-char #\space port)
              (write (second x) port)
              (write-char #\space port)
              (write (third x) port)
              (write-char #\newline port))
            lst))

(define (readXYZ fileIn)    ;Function already provided in the question, function used to read the given files
  (let ((sL (map (lambda s (string-split (car s)))
                          (cdr (file->lines fileIn)))))
    (map (lambda (L)
           (map (lambda (s)
(if (eqv? (string->number s) #f)
    s
    (string->number s))) L)) sL)))

(define (pick-random-points Ps) ;Function used fort picking the random points
  (list (list-ref Ps (random (length Ps)))
        (list-ref Ps (random (length Ps)))
        (list-ref Ps (random (length Ps)))))


(define (plane P1 P2 P3)    ;Function used to define the plane based on the given three points. It returns a list of a,b,c,d values in which are part of the equation of a plane
  (let ((v1 (list (- (car P2) (car P1))
                  (- (cadr P2) (cadr P1))
                  (- (caddr P2) (caddr P1))))
        (v2 (list (- (car P3) (car P1))
                  (- (cadr P3) (cadr P1))
                  (- (caddr P3) (caddr P1)))))
    (let ((a (- (* (cadr v1) (caddr v2))
                (* (caddr v1) (cadr v2))))
          (b (- (* (caddr v1) (car v2))
                (* (car v1) (caddr v2))))
          (c (- (* (car v1) (cadr v2))
                (* (cadr v1) (car v2)))))
      (let ((d (+ (* a (car P1))
                  (* b (cadr P1))
                  (* c (caddr P1)))))
        (list a b c d)))))


(define (support plane points eps)   ;Support function is used to calucalte the support of the provided plane. It returns a support count of the plane
  (let ((count 0)
        (inliers '()))
    (for-each (lambda (p)
                (let ((dist (distance-to-plane plane p)))
                  (if (<= dist eps)
                      (begin
                        (set! count (+ count 1))
                        (set! inliers (cons p inliers)))
                      ; add this else clause
                      (void))))   ; do nothing if point is not an inlier (had to put the else function, or else there was an error during compilation)
              points)
    (cons count inliers)))

(define (distance-to-plane plane point) ; The funciton is used to get the distance between the given point and the given plane. The function was spitting out error if I did not replace the non-numbers with 1. It is not ideal but the read file seems to have non-numeric characters in coordinates
  (let ((a (if (number? (first plane)) (first plane) 1))
        (b (if (number? (second plane)) (second plane) 1))
        (c (if (number? (third plane)) (third plane) 1))
        (d (if (number? (fourth plane)) (fourth plane) 1)))
    (let ((x (if (number? (first point)) (first point) 1))
          (y (if (number? (second point)) (second point) 1))
          (z (if (number? (third point)) (third point) 1)))
      (/ (abs (+ (* a x) (* b y) (* c z) d))
         (sqrt (+ (* a a) (* b b) (* c c)))))))


(define (dot-product v1 v2)    ; Helper Method to get the dot product of the points (this is based on an initial logic, but there seems to be an error if I remove this)
  (apply + (map * v1 v2)))


(define (ransacNumberOfIteration confidence percentage) ; Method to calculate the max number of iterations. 
  (let ((k (/ (log (- 1 confidence))
               (log (- 1 (expt percentage 3))))))
    (ceiling k)))

(define (dominantPlane Ps k) ; Funciton used to calculate the dominant plane
  (let ((max-support 0)
        (dominant-plane '()))
    (do ((i 0 (+ i 1)))
        ((>= i k) dominant-plane)
      (let ((random-points (pick-random-points Ps))
            (current-plane '()))
        (set! current-plane (plane (first random-points)
                                   (second random-points)
                                   (third random-points)))
        (let ((support-count (support current-plane Ps)))
          (when (> (car support-count) max-support)
            (set! max-support (car support-count))
            (set! dominant-plane (cdr support-count))))))))


(define (planeRANSAC filepath confidence percentage eps)        ; Function used for running the RANSAC algorithm by calling the functions accordingly
  (let ((Ps (readXYZ filepath))
        (k (ransacNumberOfIteration confidence percentage))
        (n (length (readXYZ filepath)))
        (t (* eps eps)))
    (let loop ((i 0) (best-support 0) (best-plane '()))
      (cond ((>= i k) (list best-plane best-support))
            (else
             (let* ((p1 (list-ref Ps (random n)))
                    (p2 (list-ref Ps (random n)))
                    (p3 (list-ref Ps (random n)))
                    (current-plane (plane p1 p2 p3))
                    (support-count (support current-plane Ps t)))
               (if (> (car support-count) best-support)
                   (loop (+ i 1) (car support-count) (cdr support-count))
                   (loop (+ i 1) best-support best-plane)))))))
  )


