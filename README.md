EECE496
=======

<br><H3>funf framework data collect</H3>
<p>
<br><H3>When adding a new probe, remember the following:</H3>
<br>1) Add new probe to MainActivity>>res>>AndroidManifest.xml
<br>2) Add new probe to MainActivity>>res>>values>>strings.xml
<br>3) Add new probe default settings to MainActivity>>res>>values>>defaults.xml (IN EXACT ORDER as strings.xml)
<br>4) Add new Preference to MainActivity>>res>>layout>>pref_activity_settings.xml
<p>
<br>If you've done all 4 steps above, you should see the new probe when you open up CONFIG on the Android app.
<br>Check the checkbox to enable/disable.

<br><H3>Editting Default Sampling Periods/Durations</H3>
<br>Edit step 3 of above

<br><H3>Changing Archive/Upload settings</H3>
<br>1)Open MainActivity>>src>>edu.mit.media.funf.funftowotk>>MainActivity
<br>2)Find init(), change the settings.

