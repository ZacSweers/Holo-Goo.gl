Holo-Goo.gl
===========

![alt text](https://raw.github.com/pandanomic/Holo-Goo.gl/master/src/main/res/drawable-xxhdpi/ic_launcher.png "Holo Goo.gl")

My first Android pet project, a Holo Goo.gl app! It's not by any means done, but I wanted to make this repo public to give some code examples for interviews.

I started working on this as a simple project to get some practice with Android development, Google APIs, using a RESTful API in general, HTTP requests, 3rd party libraries, admob, etc. I also wanted to make a nice Goo.gl app, because the apps currently on the play store look pretty outdated UI-wise.

It's been a ton of fun developing, albeit I've had to slow down since school started back up along with a part time internship. I plan on eventually releasing this on the play store, and maybe also taking out the actual Goo.gl portion of it and releasing it as an open source library for other developers to use in their apps.

I apologize in advance for how terrible my code probably looks.

### As of right now, it can do the following: 
* Authenticate with Google using Android's native account manager
* Shorten a new URL (anonymously or authenticated)
* Get user's URL history
* Share a shortened URL to other apps as well as receive (and shorten) URLs

### Some upcoming TODO's
* Detailed URL metrics activity
* Caching URL history (anonymous and authenticated)
* Load more URL history results when scrolled to the bottom

### I've use a few wonderful third party libraries, including the following:
* [ActiveAndroid](https://github.com/pardom/ActiveAndroid)
* [ACRA](https://github.com/ACRA/acra)
* [LicensesDialog](https://github.com/PSDev/LicensesDialog)
* [ActionbarPullToRefresh](https://github.com/chrisbanes/ActionBar-PullToRefresh)
* [Android-form-edittext](https://github.com/vekexasia/android-form-edittext)
* [ChangeLogLibrary](https://github.com/gabrielemariotti/changeloglib)

## License

     Copyright 2013 Henri Sweers
     
     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at
     
     http://www.apache.org/licenses/LICENSE-2.0
     
     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
