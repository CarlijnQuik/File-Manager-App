### File Manager Report
*File Manager enables the user to find files more efficiently.
It does so by integrating Google Drive, internal and SD card files into one UI.
In this way, the user can search for files in all places at once and open or download files to view them or edit them with applications on their phone that support the file type.*

#### Screenshot of most important UI
<img src="https://cloud.githubusercontent.com/assets/22945709/22566235/5df4aa78-e98b-11e6-8f82-c365e129b37f.png" width="200">

#### Technical design
This paragraph describes the technical design of the App. The diagram below given an overview of the different components of the app.

<img src="https://cloud.githubusercontent.com/assets/22945709/22568993/34a35858-e996-11e6-9a52-460b01525a58.png" width="600">

*Sign In*
To begin with, the **Credential Activity** enables the user to sign in to Google Drive. When the user hits the "Sign In" button, the app explains the user about the deletion of files before moving on so the user can still decide not to use the app. The alert dialog build explains to the user that Android does not have a trash can by default (due to memory saving) so File Manager creates this for them. This decision was made so the chance a user loses their files becomes smaller, for they cannot delete their file by accident. The user signs in by choosing an account with an intent that pops up and giving the app permission to use their Drive account.

*Menu drawer and search*
Second, the user enters the app by an intent that leads to the **Navigation Activity**. He or she has a few options from there. They can control the menu drawer by clicking it or swiping and open the trash can or sign out. Also, they can click the search view and enter a search request of 4+ words that will search through all trashed or untrashed files. By swiping the list down it refreshes by searching for all relevant files again in case some have been changed, removed or added.

*List of files shown*
Which files the app should retrieve can be split up into four categories; searchrequest, trashcan, folder or all. This is why four parameters are sent with the **File List Fragment** created in a bundle so the steps to follow can be derived accordingly. In this way a lot of duplicate code is evaded for the retrieval of files often involves doing similar tasks for different requests. Also, because a fragment is used the previously retrieved file list can be added as a seperate fragment on the back stack, that is recalled when a user presses the back button.

*Download*
If the user decides to download a file by clicking the download image button he or she is notified by a toast that pops up giving the name of the file that is being downloaded. At the same time, a new **Download Async Task** will start to download the file in the background. When finished, a notification appears in the notification list that can be swiped down from the top of the phone and from there the user can open the file. If a default application for the type of file is set, it will open it with that application and otherwise it will let the user choose the application from which to open it. This file and type are sent to the navigation activity to do this, because the navigation activity can be reached from both the download async task and the **File Adapter**, and a file can also be opened from there (the adapter). In this way duplicate code can be avoided and the right context is always there to open the intent from. 

*Deletion*
If the user decides to delete a file, he or she can long click it. The file will be moved to the trashcan and removed from the adapter. The **Update Async Task** handles this movage if the file is a Drive file. The user can also decide to click the trash can and view the deleted files. From here a file can be permanently deleted, yet an alertdialog will ask the user first so it is certain the deletion will not be regretted by him or her. The **Delete Async Task** handles this deletion for Drive files.









