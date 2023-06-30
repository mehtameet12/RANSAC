% Name: Meet Mehta
% Student ID: 300261159
% CSI 2120 - Project 4

% ----- Given Functions to read File ----- %

read_xyz_file(File, Points) :-
    open(File, read, Stream),
    read_line_to_string(Stream, _),             % discard first line since it has a header
    read_xyz_points(Stream,Points),
    close(Stream).

read_xyz_points(Stream, []) :-
    at_end_of_stream(Stream).

read_xyz_points(Stream, [Point|Points]) :-
    \+ at_end_of_stream(Stream),
    read_line_to_string(Stream,L),
    split_string(L, "\t", "\s\t\n",XYZ),
    convert_to_float(XYZ,Point),
    read_xyz_points(Stream, Points).

convert_to_float([],[]).

convert_to_float([H|T],[HH|TT]) :-
    atom_number(H, HH),
    convert_to_float(T,TT).

% ----- ThreeRandomPoints ----- %

random3points(Points, Point3) :-               % Random point function responsible to extract 3 random points from the list Points
    random_permutation(Points, Random),
    take(3, Random, Point3).

take(N, List, Taken) :-
    length(Taken, N),
    append(Taken, _, List).

% ----- Testing of ThreeRandomPoints is shown in the report ----- %


% ----- ValueOfABCD ----- %

plane([[X1, Y1, Z1], [X2, Y2, Z2], [X3, Y3, Z3]], [A, B, C, D]) :-
    A is Y1*(Z2-Z3) + Y2*(Z3-Z1) + Y3*(Z1-Z2),
    B is Z1*(X2-X3) + Z2*(X3-X1) + Z3*(X1-X2),
    C is X1*(Y2-Y3) + X2*(Y3-Y1) + X3*(Y1-Y2),
    D is -X1*(Y2*Z3-Y3*Z2) - X2*(Y3*Z1-Y1*Z3) - X3*(Y1*Z2-Y2*Z1).

% ----- TestPlanePredicate ----- %

test_plane :-
    Point3 = [[1,2,3], [4,5,6], [7,8,9]],   % Passing three points in Point3                                        
    A = -3,                                 % Defining the coefficients of the plane equation
    B = 6,
    C = -3,
    D = 0,
    plane(Point3, [A,B,C,D]).               % Check if the plane() predicate returns true

% ----- SupportPlane ----- %

support([A, B, C, D], Points, Eps, N) :-
    findall(P, (member(P, Points), dist_to_plane([A, B, C, D], P, D1), D1 =< Eps), PList),
    length(PList, Len),
    Len >= N,
    subset(PList, Points).
    
dist_to_plane([A, B, C, D], [X, Y, Z], Dist) :-
    Dist is abs(A*X + B*Y + C*Z - D) / sqrt(A*A + B*B + C*C).

% ----- TestSupportPlane ----- %

test_support :-
    
    Plane = [-2, 4, -2, 0],                                 % Define a plane and a list of points
    Points = [[0,0,0], [1,1,1], [2,2,2], [3,3,3], [4,4,4]],
    Eps = 0.1,                                              % Define the distance threshold
    N = 3,                                                  % Define the number of points to expect
    support(Plane, Points, Eps, N).                         % Call the support predicate and check if it succeeds

% ----- RansacNumberOfIterations ----- %

ransac_number_of_iterations(Confidence, Percentage, N) :-
    N is ceil(log(1 - Confidence) / log(1 - Percentage ** 3)).

% ----- TestRansacNumberOfIterations ----- %

test_ransac_number_of_iterations :-
    Confidence = 0.95,                                                  % Define the confidence 
    Percentage = 0.3,                                                   % Define percentage parameters
    ransac_number_of_iterations(Confidence, Percentage, N),             % Call the predicate and check if it returns the expected result
    ExpectedN is ceil(log(1 - Confidence) / log(1 - Percentage ** 3)),  % Calculating the result to check and see if the value is correct
    N =:= ExpectedN.























