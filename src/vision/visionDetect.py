import cv2
import numpy as np
import math

# Parameters for filtering out tape contours
# TODO: Move parameters to a seperate file, maybe import them from the robot?
tapeAngle = 15
angleError = 8
cameraViewAngle = 29.4

# These are all set to null so that if we try to run vision processing without setting the resolution
# it returns an error and not a false value
thresholdArea = None
maxWidth = None
minHeight = None
camWidth = None
camHeight = None
centerSetPosition = None

camera = None

# This sets all the specific values for the resolution of the camera
# All of the values were fine tuned using 480p resolution, so this just converts them using dimensional analysis
def setCameraResolution(width, height):
    # This part is awful, so if you try to set camWidth to something, it will create a new variable that only works in this method
    # To avoid this being useless, we use "global" to grab the variables defined up top
    global camWidth, camHeight, thresholdArea, maxWidth, minHeight, centerSetPosition
    camWidth = width
    camHeight = height
    thresholdArea = round(width * (400 / 640))
    maxWidth = round(width * (100 / 640))
    minHeight = round(height * (40 / 480))
    centerSetPosition = round(width / 2)

# This sets the camera object, and starts the capture in the specified path
def startCamera(path):
    camera = cv2.VideoCapture(path)

# This gets the image from the camera object, read() returns two variables, so this one gets rid of the useless (to us) ret var
def getImage():
    ret, image = camera.read()
    return image

# Returns the distance between two points (preferably use tuples cause that's what opencv uses)
def distance(pointA, pointB):
    return math.sqrt(((pointB[0] - pointA[0]) ** 2) + ((pointB[1] - pointA[1]) ** 2))

# Since we have a super bright light, we will use some HSV filtering to get rid of most of the image
# HSV stands for hue, saturation, and value (value refers to brightness), so we need a high value value (yeah it sounds weird)
# And hue values that corrospond to green, and saturation values that are below a certain value (150 in my case)
def hsvFilter(frame):
    # Converts the frame to hsv to easily identify the vision tape
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)

    # Numpy arrays for the upper and lower bounds of the HSV values for the vision tape
    # Numpy arrays are just regular arrays with more features
    lowerTape = np.array([60, 100, 100])
    upperTape = np.array([120, 255, 255])

    # Gets all pixels in range of the values, creates a mask of pixel values to keep
    mask = cv2.inRange(hsv, lowerTape, upperTape)

    # Outputs the original frame but only bits inside the mask
    return cv2.bitwise_and(frame, frame, mask=mask)

# A contour is basically just the bounds around the area left in the image
# Everything not taken out by the mask will have an edge on each "blob"
# The contour is just the edge of the blob, there will be many contours in addition to the vision tape
# We will need to filter them later
def getContours(frame):
    # Converts frame to gray cause it's easier to process
    grayFrame = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # Gets all contours from the filtered image, only useful var to us is unFilteredContours
    # No clue what cv2.RETR_TREE and cv2.CHAIN_APPROX_SIMPLE specifically do, but it works so hey I'll leave it
    im2, unFilteredContours, hierarchy = cv2.findContours(grayFrame, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

    return unFilteredContours # Returns an array of contours

# This is a wrapper for filtering the contours, it goes through each individual step of the contour filtering
def filterContours(contours):
    # If I have to explain this to you, there is no hope
    filteredByArea = areaFilter(contours)
    filteredByAngle = filterByAngle(filteredByArea)
    filteredBySize = filterBySize(filteredByAngle)
    return filteredBySize

# This first method takes out all small contours, any tiny noise we get will be taken out because it is below our threshold area
def areaFilter(contours):
    # Empty filtered contours list
    filteredContours = []

    # Filters out contours by a minimum area, gets rid of basic noise
    # This can also be done through the erode method, but that makes the contours of the tape less accurate
    for contour in contours:
        if cv2.contourArea(contour) > thresholdArea:
            filteredContours.append(contour)
    
    return filteredContours

# According to the game manual, all the vision tape will be at a plus or minus angle of 15 degrees with respect to the y-axis
# Lucky for us, that's the value we get with the minAreaRect function, so we see if the read angle from the bounding box
# of the contours is within 5 degrees of either side of 15 (or 90 - 15 because the other piece of tape is the inverse)
# and gets rid of any contours that aren't at said angle
def filterByAngle(contours):
    # Empty filtered contours list
    filteredContours = []

    for contour in contours:
        minRect = cv2.minAreaRect(contour) # Gets points to surround the contour with a rectangle resulting in minimum area

        # According to the game manual, the tape sohuld be angled at 14.5 degrees, so we filter out all contours not at that angle
        # Goes it with the angle and it's inverse, minRect[2] returns angle with respect to x-axis
        # Obviously this won't be perfect, so there's about 10 degrees of room for error
        if abs(minRect[2]) < (tapeAngle + angleError) and abs(minRect[2]) > (tapeAngle - angleError):
            filteredContours.append(contour)
        elif abs(minRect[2]) < (90 - tapeAngle + angleError) and abs(minRect[2]) > (90 - tapeAngle - angleError):
            filteredContours.append(contour)
    
    return filteredContours

# Since the bounding box of the tape is always going to be thin, we have a max width is has to fit under
# We also have a minimum height since the tape will be (relatively) tall
def filterBySize(contours):
    # Empty filtered contours list
    filteredContours = []

    # We need to get all 4 variables, because boundingRect() returns 4 variables
    # If we only tried to recover 2 it would return an array or tuple
    for contour in contours:
        x, y, width, height = cv2.boundingRect(contour)

        # Filters contours with a minimum height and a maximum width
        if width < maxWidth and height > minHeight:
            filteredContours.append(contour)
    
    return filteredContours
        
# This will get important points of the reflective tape we need for later processing
def getLowerAndUpperPoints(contours):
    lowerPoints = []
    upperPoints = []

    for contour in contours:
        minRect = cv2.minAreaRect(contour) # Gets points to surround the contour with a rectangle resulting in minimum area
        box = cv2.boxPoints(minRect) # Turns the rect object into usable points
        boxPoints = np.int0(box) # Converts all the points into integer values
        # TODO: See if maybe using the decimal values makes the result more accurate

        # boxPoints is a 2D array, which means it's basically an array of arrays
        # This example is an array of points, so each point has an X and a Y
        # So boxPoints[0, 0] is the x value of the first point, boxPoints[3, 0] is the x value of the 4th point 
        # Finally, boxPoints[2, 1] is the y value of the 3rd point

        # The lowest point in the boxPoints list is at index 0
        bottomInnerPoint = (boxPoints[0, 0], boxPoints[0, 1])

        # Gets posssible points for the upper inner point which we need for pixel to inch comparison
        possiblePoint1 = (boxPoints[1, 0], boxPoints[1, 1])
        possiblePoint2 = (boxPoints[3, 0], boxPoints[3, 1])

        # Gets the distance using the distance formula (method below)
        # The more greater distance indicates the point is height-wise instead of width-wise
        distanceBottom1 = distance(bottomInnerPoint, possiblePoint1)
        distanceBottom2 = distance(bottomInnerPoint, possiblePoint2)

        # Python ternary, picks the upper point
        # This makes sure the point furthest out of the two (which will be lengthwise) is picked as the upper inner point
        upperInnerPoint = possiblePoint1 if distanceBottom1 > distanceBottom2 else possiblePoint2

        # Append points into respective lists
        lowerPoints.append(bottomInnerPoint)
        upperPoints.append(upperInnerPoint)

    return lowerPoints, upperPoints # Return lists

# Okay this one's a doozy, so now we have the two upper inner points of the tape (facing inwards towards each other)
# Now we are going to compare this to the specified distance between these two points, being exactly (to FRC standards) 8 inches
# Knowing that, we can figure out how many pixels on the camera relate to one inch
# With that knowledge, we can start to set up out trigonometry, if we know the camera angle (provided in the data sheet)
# and we know the whole back view length of the frame, we can find the distance from us to the back (the back being at the tape)
# If you draw out the triangle, one of the legs is half of the whole back, we use this with tangent of the camera angle
# and find the distance
def calculateDistanceFromCamera(upperPoints):
    # Checks if it sees more than 2 tape objects
    if len(upperPoints) >= 2:
        pixelDistance = distance(upperPoints[0], upperPoints[1]) # Gets distance from two most inner points of the tape
        inchPixelRatio = 8 / pixelDistance # 8 inches is the real distance between these two points

        # Trig time, gets total back view in inches, divides it by two and finds distance using trig magic
        camViewDistance = camWidth * inchPixelRatio
        return ((camViewDistance / 2) / (math.tan(math.radians(cameraViewAngle)))) # Note: math.tan() uses radians

# This one is pretty easy, it gets the amount of pixels the camera needs to move to be perfectly centered
def getCenterOffset(upperPoints):
    if len(upperPoints) >= 2:
        centerBetweenTape = (max(upperPoints[0][0], upperPoints[1][0]) - min(upperPoints[0][0], upperPoints[1][0])) / 2
        centerValue = centerBetweenTape + np.minimum(upperPoints[0][0], upperPoints[1][0])
        return centerValue - centerSetPosition

# This draws out the contours it sees along with the hsv filtering, this is purely for debug, no reason to use this during comp
def drawBoxes(frame, contours):
    # Now do math with tape rects 
    for contour in contours:
        minRect = cv2.minAreaRect(contour) # Gets points to surround the contour with a rectangle resulting in minimum area
        box = cv2.boxPoints(minRect) # Turns the rect object into usable points
        boxPoints = np.int0(box) # Converts all the points into integer values

        # Debug point drawing, draws points at the corners of the contour
        cv2.circle(frame, (boxPoints[0, 0], boxPoints[0, 1]), 5, (255, 0, 0), thickness=6)
        cv2.circle(frame, (boxPoints[1, 0], boxPoints[1, 1]), 5, (0, 255, 0), thickness=6)
        cv2.circle(frame, (boxPoints[2, 0], boxPoints[2, 1]), 5, (0, 0, 255), thickness=6)
        cv2.circle(frame, (boxPoints[3, 0], boxPoints[3, 1]), 5, (255, 0, 255), thickness=6)

        # Draws a green rect around the tape
        cv2.drawContours(frame, [boxPoints], 0, (0, 255, 0), 4)

    cv2.line(frame, (centerSetPosition, 0), (centerSetPosition, 240), (0, 0, 255))
    return frame
