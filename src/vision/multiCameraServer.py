#!/usr/bin/env python3
#----------------------------------------------------------------------------
# Copyright (c) 2018 FIRST. All Rights Reserved.
# Open Source Software - may be modified and shared by FRC teams. The code
# must be accompanied by the FIRST BSD license file in the root directory of
# the project.
#----------------------------------------------------------------------------

import json
import time
import sys
import time

from cscore import CameraServer, VideoSource
from networktables import NetworkTablesInstance, NetworkTables
import cv2
import numpy as np
import visionDetect

#   JSON format:
#   {
#       "team": <team number>,
#       "ntmode": <"client" or "server", "client" if unspecified>
#       "cameras": [
#           {
#               "name": <camera name>
#               "path": <path, e.g. "/dev/video0">
#               "pixel format": <"MJPEG", "YUYV", etc>   // optional
#               "width": <video mode width>              // optional
#               "height": <video mode height>            // optional
#               "fps": <video mode fps>                  // optional
#               "brightness": <percentage brightness>    // optional
#               "white balance": <"auto", "hold", value> // optional
#               "exposure": <"auto", "hold", value>      // optional
#               "properties": [                          // optional
#                   {
#                       "name": <property name>
#                       "value": <property value>
#                   }
#               ]
#           }
#       ]
#   }

# The config file is autogenerated using the FRC dashboard located on the raspberry pi, to change the values to use a browser to
# login to it (frcvision.local as the address) and add new cameras and change properties
configFile = "/boot/frc.json"

# Most of this is straight from the example code provided by first

class CameraConfig: pass

# Empty object for variables we fill in later
team = None
server = False
cameraConfigs = []

# Literally just prints an error
"""Report parse error."""
def parseError(string):
    print("config error in '" + configFile + "': " + string, file=sys.stderr)

# This just reads the camera config file and sets the team number and camera values
"""Read single camera configuration."""
def readCameraConfig(config):
    cam = CameraConfig()

    # name
    try:
        cam.name = config["name"]
    except KeyError:
        parseError("could not read camera name")
        return False

    # path
    try:
        cam.path = config["path"]
    except KeyError:
        parseError("camera '{}': could not read path".format(cam.name))
        return False

    cam.config = config

    cameraConfigs.append(cam)
    return True

# More config stuff, I honestly don't even know what most of this does because I'm too lazy to find out
"""Read configuration file."""
def readConfig():
    global team
    global server

    # parse file
    try:
        with open(configFile, "rt") as f:
            args = json.load(f)
    except OSError as err:
        print("could not open '{}': {}".format(configFile, err), file=sys.stderr)
        return False

    # top level must be an object
    if not isinstance(args, dict):
        parseError("must be JSON object")
        return False

    # team number
    try:
        team = args["team"]
    except KeyError:
        parseError("could not read team number")
        return False

    # ntmode (optional)
    if "ntmode" in args:
        string = args["ntmode"]
        if string.lower() == "client":
            server = False
        elif string.lower() == "server":
            server = True
        else:
            parseError("could not understand ntmode value '{}'".format(string))

    # cameras
    try:
        cameras = args["cameras"]
    except KeyError:
        parseError("could not read cameras")
        return False
    for camera in cameras:
        if not readCameraConfig(camera):
            return False

    return True

# Starts camera(s)
"""Start running the camera."""
def startCamera(config):
    print("Starting camera '{}' on {}".format(config.name, config.path))
    camera = CameraServer.getInstance().startAutomaticCapture(name=config.name, path=config.path) # Actual starting of cameras on camera server
    camera.setConfigJson(json.dumps(config.config))
    return camera

# ------------------------------------ This is my code ------------------------------------
# Runs cv2 image processing for camera image
def processDataFromImage(image, stream):
    hsvMask = visionDetect.hsvFilter(image) # Gets hsv filtered mask
    contours = visionDetect.getContours(hsvMask) # Gets contours
    filteredContours = visionDetect.filterContours(contours) # Filters contours by various methods

    # Debug, this uses the output stream made below (commented) to put the debug filtered image onto the dashboard
    # NOTE: if you want to do that, you need to pass in the outputStream object as a parameter so you can use it
    # I have removed it because I'm no longer using it
    debugFrame = visionDetect.drawBoxes(hsvMask, filteredContours)
    stream.putFrame(hsvMask)

    # Only does the next step if it has any filtered contours
    if filteredContours is not None:
        lowerPoints, upperPoints = visionDetect.getLowerAndUpperPoints(filteredContours) # Gets important points

        # TODO: Fix this import
        #if len(upperPoints) > 2:
            #DriverStation.reportError("More than two tape object visible, may not be accurate", True)

        # Important note, lowerPoints list is not used here, but there is use for having those power points
        return visionDetect.calculateDistanceFromCamera(upperPoints), visionDetect.getCenterOffset(upperPoints)

if __name__ == "__main__":
    if len(sys.argv) >= 2:
        configFile = sys.argv[1] # If we don't specify a config file it uses the default one above

    # read configuration
    if not readConfig():
        print('----- Failed config read; exiting. -----')
        sys.exit(1)

    # start NetworkTables
    ntinst = NetworkTablesInstance.getDefault()
    if server:
        print("Setting up NetworkTables server")
        ntinst.startServer()
    else:
        print("Setting up NetworkTables client for team {}".format(team))
        ntinst.startClientTeam(team)

    print('----- Starting Cameras... ----')

    # start cameras
    cameras = []
    for cameraConfig in cameraConfigs:
        cameras.append(startCamera(cameraConfig)) # Adds camera object made into an array to use later

    processingCam = None
    processingCamJson = None

    if cameras is not None:
        # This sets the resolution of the image processing camera
        # NOTE: Without this, the visionDetect code will throw errors
        # this was made intentionally so that it doesn't return false values
        # TODO: Fix this cause it broke
        '''for camera in cameras:
            cameraJson = json.loads(camera.getConfigJson())
            print(cameraJson["properties"][-1]["value"])
            if camera["properties"]["processing"] == 1:
                processCam = camera
                visionDetect.setCameraResolution(camera["width"], camera["height"])'''
        processingCam = cameras[0]
        processingCamJson = json.loads(processingCam.getConfigJson())
        visionDetect.setCameraResolution(processingCamJson["width"], processingCamJson["height"])

    # Starts network tables to send values into tohe roboRIO
    NetworkTables.initialize(server="10.23.35.2")
    table = NetworkTables.getTable("Vision") # Sets the table to be called "Vision" so we know exactly where our values are

    # This part is confusing, we basically need to make an empty frame object so that we can later grab the image
    # This is just an empty frame, it's a 3D array (yeah, I know) with the shape of (height, width, 3)
    # The three is for the blue, red and green values of the specific pixel in the frame
    sourceFrame = np.zeros(shape=(processingCamJson["height"], processingCamJson["width"], 3), dtype=np.uint8)

    # This is the object that will link the camera server into the cv2 progcessing
    sink = CameraServer.getInstance().getVideo(camera=processingCam)

    # This is the output stream, we can put custom images into here
    # For example, if we wanted to change the camera image before we sent it out to the driver states
    outStream = CameraServer.getInstance().putVideo("Debug", processingCamJson["width"], processingCamJson["height"])

    # loop forever, always sending values to the camera
    while True:
        # This grabs the frame, you need to get each variable or else it won't work correctly
        # We won't use the timeout, but you can print it if you want to
        timeout, sourceFrame = sink.grabFrame(sourceFrame)

        # If we can get the frame, the timeout will not be zero, it will only be zero if there's an error
        if timeout == 0:
            # TODO: Fix this import
            #DriverStation.reportError(sink.grabError(), True)
            continue
        else:
            # Gets starting time for image processing
            startTime = time.time()

            # Gets values from image processing
            distanceToTape, centerOffset = processDataFromImage(sourceFrame, outStream)

            # Gets time it took to process image
            timeToProcessImage = time.time() - startTime

            if distanceToTape is not None:
                # This puts the distance value on the table with they key "distance"
                table.putNumber('distance', distanceToTape)
            else:
                # If we don't see anything, we put -1 so the code knows that we don't see anything
                table.putNumber('distance', -1)

            if centerOffset is not None:
                table.putNumber('xOffset', centerOffset)
            else:
                # Since the center offset can actually equal -1, se put 0 so that it thinks we're on target
                table.putNumber('xOffset', 0)

            # Puts time it took to capture and process the image so we can account for that in our PID control
            table.putNumber('delay', timeToProcessImage + timeout)
        
        time.sleep(0.01) # Loop will only run every 0.5 seconds