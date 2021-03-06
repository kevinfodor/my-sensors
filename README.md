This application is a very simple utility which allows you to explore each sensor available on an Android device. This is mainly intended to be an instructional and experimental application to help facilitate further exploration in application development using Android sensors. Each sensor is itemized in a list and when selected displays both informational and real-time data as well as the ability to search for more information on the selected sensor. Anyone is free to distribute, use and abuse this software as necessary. Enjoy!

The latest release version of MySensors is on [Google Play](https://play.google.com/store/apps/details?id=com.kfodor.MySensors) (Version 1.4)

Downloads can be found here: [MySensors\_v1.4](https://drive.google.com/folderview?id=0B8_Aotc206N6SUFfMXAyRXNEX1k&usp=sharing)

## Version 1.4 (Version Code=7) ##
Download: [MySensors\_v1.4](https://drive.google.com/folderview?id=0B8_Aotc206N6SUFfMXAyRXNEX1k&usp=sharing)

  * Supports Android SDK v4 thru v23
  * Added sensor view logging [issue #9](https://code.google.com/p/my-sensors/issues/detail?id=#9) to a file which can be retrieved by a host PC through the device USB drive.
  * Added the ability to log all found sensors to a file (MySensors.txt).
  * Added the ability to view and delete all collected sensor view logs.
  * Fixed [issue #10](https://code.google.com/p/my-sensors/issues/detail?id=#10) - Some screens using the sensor list where text as too large for the item.
  
## Version 1.3 (Version Code=4) ##
Download: [MySensors\_v1.3](https://drive.google.com/folderview?id=0B8_Aotc206N6em1uRzhyRVVncG8&usp=sharing)

  * Supports Android SDK v4 thru v20
  * Removed deprecated sensor type constants and replaced with explicit values. This was done to help maintain backwards compatibility with as many Android API versions as possible.
  * Removed use of managed dialogs from Activities as this has been deprecated by the API. Moved existing code to use [FragmentActivity](http://developer.android.com/reference/android/support/v4/app/FragmentActivity.html) and [DialogFragment](http://developer.android.com/reference/android/app/DialogFragment.html) as recommended by Android. Please see: http://android-developers.blogspot.com/2011/03/fragments-for-all.html for more details on the new API and how to incorporate a Android provided static library for backwards compatibility into APIv4. This does mean you now need to include a static library for this API as described in: http://developer.android.com/guide/topics/ui/dialogs.html
  * To support [FragmentActivity](http://developer.android.com/reference/android/support/v4/app/FragmentActivity.html) and [DialogFragment](http://developer.android.com/reference/android/app/DialogFragment.html), I took the opportunity to separate the 'about' and 'sensor rate change' dialog classes to their own separate modules. This doesn't change the operation of the app any, it just makes the code a bit more maintainable and readable.
  * In general fixed as many Android Lint issues as possible. Most were pretty minor and did not affect the code function.
  * Fixed [issue #1](https://code.google.com/p/my-sensors/issues/detail?id=#1) which requested a new feature that adds the ability to prevent the screen from timing out.
  * Fixed [issue #2](https://code.google.com/p/my-sensors/issues/detail?id=#2) which was to persist the sensor rate update setting as the sensor activity is changed. This way you can switch sensor views and the previously set rate will be maintained. This does not persist however in the file system. That doesn't seem like something people would want.
  * Added a new menu item to reset all sensor update rates back to the default rate of NORMAL as a convenience.
  * Fixed [issue #3](https://code.google.com/p/my-sensors/issues/detail?id=#3) by adding code to check if a sensor already exists in the sensor list. If it does, do not add it.
  * Added a sensor found counter at the top of the main screen banner. Let's you easily know how many sensors have been found.
  * Deferred [issue #4](https://code.google.com/p/my-sensors/issues/detail?id=#4) as I cannot see any error with how I am reporting the time stamp as reported by the sensor. It appears to only work with slower sensors such as the light/prox sensors. Faster ones  like the gravity report very very large numbers. Remains a mystery. There appears to be a lot of chatter about this on the Android forums.
  * Fixed [issue #5](https://code.google.com/p/my-sensors/issues/detail?id=#5) by adding 7 new sensor types; Magnetic Field (Uncalibrated), Game Rotation Vector, Gyroscope (Uncalibrated), Significant Motion Detector, Step Counter, Step Detector, and Geomagnetic Rotation Vector.
  * Added new 'sensor values' field to to the sensor static view to help indicate how many values to expect.
  * Added new sensor view menu option to set the event rate (just like the button does).
  * Refactored how the SensorView handles indexing into and defining interfaces for existing sensors. Hopefully made it more flexible to handle unknown sensors as well as being able to add new sensors more easily.
  * Fixed [issue #6](https://code.google.com/p/my-sensors/issues/detail?id=#6) by adding the ability to toggle screen rotation via the menu. Additionally the rotation selected is persisted.
  * Fixed [issue #7](https://code.google.com/p/my-sensors/issues/detail?id=#7) by no longer passing data from the main MySensors activity to the SensorView activity. Instead the SensorView activity learns which sensor to display by being passed an index which maps back to the sensor. Before this was fixed, whenever Android went through garbage collection to reclaim resources and the main MySensors activity was destroyed, it would release the memory which was being referenced to display the chosen sensor and then would crash.
  * Fixed [issue #8](https://code.google.com/p/my-sensors/issues/detail?id=#8) which was to add the ability to disable the logging to logcat so that sensor reporting is a quick as possible.
  * Few last minute additions. Added a new start/stop toggle button which allows you to freeze and run the sensor display much like a stop watch.
  * Added a Reset Counter button to allow the user to reset the accumulated event counter as needed.
  * Removed the set rate button since it can already be easily done via the android menu button.
  * Fixed up remaining lint issues.

## Version 1.2 (Version Code=3) ##
Download: [MySensors\_v1.2](https://drive.google.com/folderview?id=0B8_Aotc206N6V29FejlVTUJhMkE&usp=sharing)

  * Fixed reported GYROSCOPE units as rad/sec and not degrees.
  * Changed PRESSURE sensor units to hPa (hectopascal) instead of KP.
  * Added new Sensor.TYPE\_AMBIENT\_TEMPERATURE and Sensor.TYPE\_RELATIVE\_HUMIDITY
  * Updated 'Uses SDK' to target SDK 'Revision- 15' per suggestions in post: [Say Goodbye To Menu Button](http://android-developers.blogspot.com/2012/01/say-goodbye-to-menu-button.html)
  * Keep sensor display from rotating when displaying sensor data per user feedback request. Sensor menu still allows screen rotation however.

## Version 1.1 (Version Code=2) ##

Bug Fix Update!

  * Apparently when Froyo came out, Android added 3 more sensors and many devices have them. Normally a good thing, but due to a bug in my app when it discovered those sensors it indexed beyond an array! Bummer, poor programming on my part. But now it is fixed and hopefully will adapt if more sensors are added.

## Version 1.0 (Version Code=1) ##

Original release