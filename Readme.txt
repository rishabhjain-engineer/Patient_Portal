 ANDROID PROJECT IMPORT AND IMPLEMENTATION SUMMARY
 =================================================

 Regarding Gradle:
 -----------------
 Following should be added as the Project :

 1) FacebookSDK
 2) SegmentedControlLibrary
 3) Simpl3r
 4) SlidingMenuLibrary
 5) ViewPagerIndicatorlibrary
 6) demo_cloud_tagview

 syntax:: compile project (':<project_name>')
 NOTE:: If any MODULE PROJECT (AS Above) have any MANIFEST XML Tag Like "<application>......</application>" then DELETE IT.

 Following should be added as the Gradle script Links:

 1) play-services-maps
 2) play-services-analytics
 3) play-services-location
 4) play-services-nearby
 5) swipelistview:1.0-SNAPSHOT@aar (Have to add separate maven)
 6) dev.dworks.libs:astickyheader
 7) se.emilsjolander:StickyScrollViewItems

 syntax:: compile'<name.VERSION>'

 Regarding DEX Problem :
 -----------------------

 1) Import "multidex" support library .
 2) Make javaMaxHeapSize to 4g for API 23.
 3) Exclude support-v4 from all the Links.

 Vitals ::
 =========
Only for this Project .

 1) make build tool as 23.0.1.
 2) make appcompat-v7 as 19.1.0.
 3) target sdk as 19.

 Regarding BUILD ::
 ==================
 1) Choose existing Key "KEY".
 2) Gave Alias Name as Patient Portal.
 3) Password For Key is: C!0udCh0wk.
    **0(ZERO)

 (This import summary is for your information only, and can be deleted
 after import once you are satisfied with the results.)

 thanks!

 ANDROID DEVELOPER.
 ZUREKA