EECE496 - Android Application
=============================

Introduction
------------
This is an Android application written for

* Android APK 7+
* framework FunF v0.3. 
 
For in depth documentation as to how FunF works please visit http://code.google.com/p/funf-open-sensing-framework/wiki/GettingStarted

Managing Probes
--------------------
###Adding New Probes
1. Add new probe to MainActivity>>res>>AndroidManifest.xml
2. Add new probe to MainActivity>>res>>values>>strings.xml
3. Add new probe default settings to MainActivity>>res>>values>>defaults.xml (IN EXACT ORDER as strings.xml)
4. Add new Preference to MainActivity>>res>>layout>>pref_activity_settings.xml

If you've done all 4 steps above, you should see the new probe when you open up CONFIG on the Android app.
Check the checkbox to enable/disable.

###Editting Default Sampling Periods/Durations
1. Edit step 3 of above

###Changing Archive/Upload settings
1.Open MainActivity>>src>>edu.mit.media.funf.funftowotk>>MainActivity
2.Find init(), change the settings.

General Configurations
----------------------
General configurations can be found at the top of MainActivity.java.  There you can edit:

* Which server to upload to by changing ROOT_URL
* How often data is archived by changing archive_period
* How often data is uploaded in batches by changing upload_period
* How often the app polls the server to see if it's alive by changing server_check_interval
