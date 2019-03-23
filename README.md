# Program # 2
Name:  Mickale Bush
Cosc 4735

Description:  (how to run the program, phone/emulator screen size, android version ie 7.0)
-Press the fab to send one to the camera to take a picture
-Click a marker to pull the image associated with it up
-press the back button to get back to map

Phone Resolution: 2560x1312 (505 ppi) Phone: Essential PH-1 (Phone) Android Version: 9.0 (Pie)

Github Repo: https://github.com/cosc4735sp19/am_program2-MickaleB

Anything that doesn't work:

# Graded: 47/50 #

* Last location updates are handled intially when the app is opened and then subsequently when the camera is opened, which can create issues with false marker location. *(-3 points)*

**For example:**

**Test Case:**
Open app at the Engineering Building. Walk over to the Union and take a picture. Walk over to the Ben Franklin statue and take a picture.

**Result:**
Picture at the Union shows as though it was taken at Engineering and picture of the Ben Franklin statue shows as though it was taken at the Union (essentially everything is one location behind).

While it may seem intuitive to get the user's inital location and then only get a location when it is needed (i.e. when you want to take a picture and place a marker), it is best just to recieve a constant stream of updating locations on a set interval. For some reason it still wants to use a location even if you are updating it again the next time you open the camera.

Overall, everything works perfectly if you are only testing for the ideal use case (i.e. the user will take a picture immediately after opening the app), but it is important to account for any strange things the user might do when using the app. This location tracking method also creates issues when the camera is opened, but no picture is taken and then the user goes somewhere else to take a picture afterwords. In the future, try to test for strange use cases. These are the weird sort of things I will be looking at when testing your apps.
