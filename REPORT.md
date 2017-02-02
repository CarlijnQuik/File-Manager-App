### File Manager Report
*File Manager enables the user to find files more efficiently.
It does so by integrating Google Drive, internal and SD card files into one UI.
In this way, the user can search for files in all places at once and open or download files to view them or edit them with applications on their phone that support the file type.*

### Resources
The most important resources used can be found in RESOURCES.md.

#### Screenshot of most important UI
<img src="https://cloud.githubusercontent.com/assets/22945709/22566235/5df4aa78-e98b-11e6-8f82-c365e129b37f.png" width="300">

#### Technical design
This paragraph describes the technical design of the App. The diagram below given an overview of the different components of the app.

<img src="https://cloud.githubusercontent.com/assets/22945709/22568993/34a35858-e996-11e6-9a52-460b01525a58.png" width="800">

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

#### Challenges
The details of these activities will now be set forth according to the challenges I have faced during creation.

*Credential Activity*
The greatest challenge tackled during the project was enabling the app to authenticate with Google Drive. It took a long time before I fully understood the differences between the available Google APIs. Once I made these differences clear as can be seen in the table below, it was easier to understand how to implement its functionality and authentication.


The first mistake I made was mixing the two APIs up, which got me confused for I was not able to understand which functionality I could use. Creating a file with GDAA for instance worked at one point, yet I had to keep both versions of the API in the App in order to keep it. To keep a clear overview and because I could not oversee the consequences in therms of for instance memory and speed when using two versions at once, I decided to only keep one. Because the REST V3 version gives deeper control over the users Drive this is the one I chose. 


The second mistake I made was not checking all the users requirements before signing him or her in. I had once made a sign in activity within the app that this ask for the GET_ACCOUNTS and Drive permission, which is why the app kept on functioning even after being removed. When both APIs where still installed in the app, I figured out how to request a token with the Google API client. This made the App malfunction due to an error I could not resolve at first. This is when I figured out the usage of both Google API Client and Google Account Credential was not necessary and removed the API client from the project. The error (Token: Unknown Source) only resolved when I asked the user for permission again ánd put the token request in an async task so it would not cause a deadlock (conflicting processes) anymore. 

| GDAA                                                    | REST V3                                                    | 
| ------------------------------------------------------- | ---------------------------------------------------------- | 
| Newest API                                              | Different permissions due to deeper control                |
| Login with Google API client and sign In Options        | Log in by Google Account Credential                        |   
| Can only work with files created in the app itself      | Can work with all files                                    |    
| RequestToken by Web Client ID (API console)             | Token by credential.getToken AsyncTask                     |    
| Log out clearly handled                                 | Log out not clearly handled                                |    
| Easier way to create files with its predefined intent   | Handle all connections yourself (e.g. asynctasks & checks) |

One of the things that are still unclear about the connection to this API, is that according to the Google documentation, the following formula should enable the user to download a file using the Drive service initialized with the Google Account Credential:


OutputStream outputStream = new ByteArrayOutputStream();
driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);


Yet, after trying, it did not deliver any result (though no error too). This seems strange due to the fact that Updating, Deleting and Listing files dóes work using the same driveService (com.google.api.services.drive.Drive). To download, I now sent a HTTP request myself so I can sent the authentication token with it. If I would have had more time, I would have figured out completely what causes this difference in using the API and enabled the user to upload files as well. Also, I would have sent the type of async task in the params so I wouldn't have needed 4 different async tasks for the different types of requests.

*File List Fragment*
In the beginning, I used two different singletons: one for the internal files and one for the Drive files. After making the file list fragment more efficient, the internal files singleton was not needed anymore. Now, to retreive the list that results from the Drive files async task from the file list activity I use the **Drive Files Singleton**, for this task has to run in the background and so the current list of Drive files, whether the async task has finished or not, can be retreived. If I would have had more time I would have adapted it in such a way a select and sort function could be enabled (by for instance passing it another parameter stating whether to sort or select). Also, I could have looked into async tasks more and figure out whether I could pass the Drive files in a different way instead of a singleton.

*File Object*
The **File Object** is composed by setting either the Java or Drive file to null and adding a location. At first, the file's extension was decided in a function in the navigation activity, but later on I figured it would be more logical to add this to the file object so the seperation of concerns is more logical. If I would have had more time, I would have made the file object recognize the file's location by path or file type, so this would not have to be passed to the object.

*Working with internal files*
The app contains one final bug but I had no time left to revise it. It concerns the deletion of Java files by calling:

File file = new File(path);
context.deleteFile(file.getName());


Several online sources state that this should work, yet trying different ways did not result in success. I decided to keep the code in the project, because it does not cause any bugs. If I had more time I would have fixed this (probably two lines of code when the reason of failure is found). For now I notify the user that the file cannot be removed.

*Layout*
If there had been more time, I would have wanted to change some small things about the layout. Right now the recyclerview layout does not respond to click events, the drawer menu item pressed can be wrong due to the use of the backstack of fragments that does not recognize that the previous fragment is from a different menu option and the sign in button does not have any gravity that makes it feel less like a real button.

*Search function*
The search function can cause a bug when a user deliberately sents a lot of search requests at once that cause a memory error. More time would have enabled me to look into this further. Also, I do not know for sure what amounts of data the app can handle before the search function and other functions in general start to mal function. 

#### Conclusion
To sum up, the major things I have learned are as follows. To begin with, I have a better idea of which aspects to take into account when building an app. Examples of these are API/SDK version, error checking, separation of concerns (MVC) and authentication. Second, the way in which I solve problems has developed. At first I tended to assume too quickly that I knew what the problem was (while I didn't). Now I write down an overview of possible options, check them in the most logical order and only move on to the next option when I am sure the current one is not working. I have lost a lot of time on errors for which I already had a solution that I oversaw because I did not check it seriously first. I just assumed it was more complicated and the solution I had in mind therefore could not be the right one. Also, I learned to search more effectively. I spent a lot of time reading and trying out answers online that turned out to be unhelpful. Specifying the problem and searching for that exact one has helped me save time reading documentation or blog posts that were not of use for the problem I was facing.



