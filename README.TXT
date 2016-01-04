Version 1.4-beta2 (Version Code=6)
============================
  * Supports Android SDK versions v4 through v23
  * Bug Fixed (#10) - Some screens using the sensor list where text as too large for the item.

Version 1.4-beta (Version Code=5)
============================
  * Supports Android SDK versions v4 through v23
  * Added sensor view logging (#9) to a file which can be retrieved by a host PC through the device USB drive.
  * Added the ability to log all found sensors to a file (MySensors.txt).
  * Added the ability to view and delete all collected sensor view logs.

Version 1.3 (Version Code=4)
============================
  * Bug Fixed (#3) - Sensor List Memory Leak
  * Bug Fixed (#7) - Application crashes after it has been in the 
  background for sometime 
  * Fixed various Android Lint issues; all minor 
  * Prevents screen timeout when viewing a sensor.
  * Added a sensor found counter at the top of the main screen banner. Let's 
  you easily know how many sensors have been found.
  * Added settings persistence so that your settings chosen for each sensor
  view are persistent through usage of the application.
  * Added the ability to reset all sensor views back to their default settings.
  Also added a menu option to reset each sensor view itself.
  * Added a menu option to show all reported values from a sensor and not 
  just those which are documented. Android tends to report at least 3 values
  for each sensor even if only one value is defined.
  * Added a menu option to turn on LogCat output of each sensor update. For
  sensors that update at a high rate this can cause quite a bit of traffic
  to LogCat.
  * Added a start/stop button to allow the user the ability to freeze the 
  sensor view at any time. Another button may be used to reset the event
  counter to zero.
  * Supports Android SDK versions v4 through v20
  * As always, NO ads, NO special permissions and still FREE!

Version 1.2 (Version Code=3)
============================
  * Fixed reported GYROSCOPE units as rad/sec and not degrees.
  * Changed PRESSURE sensor units to hPa (hectopascal) instead of KP.
  * Added new Sensor.TYPE_AMBIENT_TEMPERATURE and Sensor.TYPE_RELATIVE_HUMIDITY
  * Updated 'Uses SDK' to target SDK 'Revision- 15' per suggestions in post: 
  Say Goodbye To Menu Button
  * Keep sensor display from rotating when displaying sensor data per user 
  * feedback request. Sensor menu still allows screen rotation however.

Version 1.1 (Version Code=2)
============================
Bug Fix Update!

  * Apparently when Froyo came out, Android added 3 more sensors and many 
  devices have them. Normally a good thing, but due to a bug in my app 
  when it discovered those sensors it indexed beyond an array! Bummer, 
  poor programming on my part. But now it is fixed and hopefully will 
  adapt if more sensors are added.

Version 1.0 (Version Code=1)
============================
Original release

  