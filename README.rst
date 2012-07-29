Android NICE guidelines
-----------------------

This app is the very first iteration of the NICE guidelines application for Android.  It is intended that at some point this application will be used for other guidelines so please try to keep any code quite abstract.

Currently the data is in assets/xml/guidelines.xml but ideally they should be downloaded.  Some other features are:

* Search
* Favourites/ Recent
* Refresh (i.e remove all downloaded and refetch)
* Show with an icon which have already been downloaded
* Download ALL

Implemented ready for 1.2

Neil McPhail implemented async download.


When building you need to git clone git://github.com/johannilsson/android-actionbar.git at the same level as your git repo so that the reference will work.