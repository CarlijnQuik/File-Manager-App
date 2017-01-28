### Process book

*Week 1*

#### Day 1
- Decided to create the App "BliFi"
- Worked out the proposal specifying the functionality now aimed for

<img src="https://cloud.githubusercontent.com/assets/22945709/21845853/a353540a-d7f4-11e6-83f9-5da9875df450.png" width="400">
#### Day 2
- Set my personal goal for this week to finding out how to link the App with Google Drive and create the overview of files in a list
- Looked into how to create an activity with a menu, search and settings option in the toolbar
- Created the design document

<img src="https://cloud.githubusercontent.com/assets/22945709/21811271/610f8794-d74f-11e6-9336-8ef359f75a76.png" width="300">
<img src="https://cloud.githubusercontent.com/assets/22945709/21816695/5259cfb8-d761-11e6-930b-fdab08e85a54.png" width="400">
#### Day 3
- Had the first meeting with my peer group
- Set my goal for the day to finding out how to put the files that are in the internal storage in a list, because the entire project falls or stands with being able to use the data as pleased
- Found out how to get the path of files and reach them, still not sure how to open, edit and delete files from the App, but made the adapter of the listView anyway because I usually get stuck on the Adapter part
- Did indeed get stuck, the Adapter did not want to show the images, after trying it multiple ways together with Renske, the adapter finally showed the right images after putting them in the mipmap folder instead of the drawable folder and calling android:scr= in the xml file
- Wondering how to make the app be able to open folders in an effective way, will look into this tomorrow
- Goal for tomorrow: be able to view all files on phone and SD, and figure out how to open them

<img src="https://cloud.githubusercontent.com/assets/22945709/21855022/431ff736-d81d-11e6-8313-328bc2569183.png" width="200">

#### Day 4
- Continued editing the code to represent all files in an adapter and be able to click on folders to see the files in it
- Spent a lot of time on another adapter error, then decided to copy and paste the whole project in a new one which showed the mistake was in the layout xml file
- Tomorrow: finish adapterview with fileType icons etcetera and hopefully find out how to edit and open files

<img src="https://cloud.githubusercontent.com/assets/22945709/21895785/3a6d364a-d8e4-11e6-8906-33f29664cd38.png" width="200">

#### Day 5
- Presentations in the morning, main comment I could not give a straight forward answer to was: "Can you name a concrete problem the app solves?", will think about this for the future
- Succeeded in letting the user view what's in a folder and open it, back button is not working yet though

<img src="https://github.com/carlijnq/nl.mprog.blifi/blob/master/docs/file_open_1.png" width="200">
<img src="https://github.com/carlijnq/nl.mprog.blifi/blob/master/docs/file_open_2.png" width="200">
<img src="https://github.com/carlijnq/nl.mprog.blifi/blob/master/docs/file_open_3.png" width="200">
<img src="https://cloud.githubusercontent.com/assets/22945709/21933005/c29a96ec-d9a3-11e6-9eef-4b3a4462e30d.png" width="200">

*Week 2*

#### Day 6
- Decided to leave the back button not functioning and some other bugs within this implementation and to focus on creating a full version
- Decided to create the menus first so the prototype is complete, then create the Google Drive component
- Figured out how the navigation drawer works, next step is to figure out the differences between fragments and activities

#### Day 7
- Spent day 6 and all morning on figuring out how to let the fragment show the array list with file objects, after trying a lot of online sources decided to try it myself intuitively by just putting the whole activity in the fragment and change onCreate to onCreateView, which worked: thought me to try myself first instead of following the advices given online
- Figured out how to let the user sign in with Google, though he or she is not able to switch accounts yet, sign out in the menu drawer leads back to sign in activity
- Found out that there is a OneDrive API, changed the sign in activity to not using FireBase, also because no database will be needed for the app
- OneDrive will probably not be doable within the given time, but it is smart building the app in such a way features can be added later on
- Sign Out button does not work yet, will fix this later
- Need to figure out how to connect to the signed in user in multiple activities/fragments using the googleApiClient, so the Drive files can be accessed and the user can be logged out

<img src="https://cloud.githubusercontent.com/assets/22945709/22018520/7a3561de-dcaf-11e6-8bfd-e759f2d36328.png" width="200">
<img src="https://cloud.githubusercontent.com/assets/22945709/22018519/7a34e6b4-dcaf-11e6-9234-0cb8e1a1eed8.png" width="200">
<img src="https://cloud.githubusercontent.com/assets/22945709/22024429/409eaa50-dcca-11e6-8e48-3057521e0b1b.png" width="200">

#### Day 8
- Do not understand how to use the Google account of the signed in user yet, trying to figure that out in order to retreive their Google Drive files
- Found out that I need to use the Google REST API
- The example gave me the files as can be seen in the fourth picture below, now had to figure out how to integrate this into my own design and arraylist
- Figured it out as shown in the pictures, now have to use the files retreived effectively
- Liked some of the old designs better, but can still change that later on
- First version with Drive files ready, yet still buggy: opening files is not possible and the type is not right (see image .png shows as .txt)

<img src="https://cloud.githubusercontent.com/assets/22945709/22077448/85873cdc-ddb4-11e6-8bfe-78df5a3fa672.png" width="150">
<img src="https://cloud.githubusercontent.com/assets/22945709/22077447/8583c624-ddb4-11e6-9b44-8c347decd731.png" width="150">
<img src="https://cloud.githubusercontent.com/assets/22945709/22077449/85898d48-ddb4-11e6-96d4-15913dcfdf05.png" width="150">
<img src="https://cloud.githubusercontent.com/assets/22945709/22077450/858b8698-ddb4-11e6-88c0-f209d93e3378.png" width="150">
<img src= "https://cloud.githubusercontent.com/assets/22945709/22079150/10fdbf2e-ddbb-11e6-897d-22fdd8ece556.png" width="150">

#### Day 9
- New todo's: look into style guides and versions of Android
- Right now I create a fragment to manage the Google Drive account, in which I pass the files through a parcable array to a file fragment I create: this is not the right way to do it because fragments should be reusable, have to look into how to do this in a different way
- Found out that if I want more functionality within my list view, I need to convert it to a recycler view, so I did
- Back button functions with internal files and the list is converted to a recyclerview
- Drive files are in the same list, yet cannot be opened yet

<img src="https://cloud.githubusercontent.com/assets/22945709/22123486/0f6d31fe-de8c-11e6-8f26-7624fe6ad8ff.png" width="200">
<img src= "https://cloud.githubusercontent.com/assets/22945709/22123485/0f633a8c-de8c-11e6-95b2-1a6b1a2d08d2.png" width="200">

#### Day 10
- Presentations in the morning, no particular feedback, yet I asked about the check boxes in the recycler view and how to display them and decided together to only show them on "Select" clicked
- Looked into Google Drive documentation and found it very difficult to make sense of the data type retreived
- Got feedback on how to naviagte to sign in, so wanted to enable the user to switch account by a spinner in the menu, but because it turned out to be difficult decided to let go of the idea and move on to understanding the Drive Files
- Enabled the user to sign in in different activity, for now the best way to handle this, because only one account can be added, and in the future because this activity does not need the settings menu
- Challenge for next week: fully understand the Google Drive API
- In the last week the design can be changed if that turns out to be better, now focus on functionality!

*Week 3*

#### Day 4
- Created a week 3 and 4 TODO list
- Discovered the scope of the Google Account Credential was set to READ_ONLY which should be DRIVE
- Discovered how to get all file info instead of only id and name
- Was misguided by the info on the Drive API site saying: "in the majority of cases production Drive apps will strongly benefit by making use of the Drive API for Android, which is integrated with Google Play Services and offers better performance." (https://developers.google.com/drive/v3/web/quickstart/android))
- Discovered some differences between the Drive REST and GDAA API
- Connected to Drive and it seems I can finally edit the files
- Now have to log in twice, once with the rest api and once with the normal api to use the different functionalities (creating a file is nicer with GDAA API)

#### Day 5
- Tried all morning to enable the user to only log in once, which worked in the end but still buggy
- Enabled user to log in once, but now the "create file" button is not working, probably because the api client is in another fragment
- Creating files works again with the floating action button in the file fragment (GDAA)
- Trying to get the search function right, still struggling with retaining the signed in user and his or her files

#### Day 6
- The problem is that the Google Drive API (GDAA) only lets you work with the files or folders the user has created within your App and not the ones they create via the Drive itself, now looking for a way to list files within a folder created somewhere else
- Also, it is not adviced to use both APIs in one project, so I need to choose one
- Filetype of Google files can be recognized: so folders too
- Opening Google Drive files in the Drive App or internet explorer works (except for folders)

#### Day 7
- Found out downloading seemed to work but the files could not be opened/read
- It turned out the auth token needed to be retreived using the web client ID instead of Android, now a token can be retreived to be sent with the HTTP request
- The project broke down after retreiving the token, I tried to get the project back to the original and find the bug, which did not solve the problem
- Copied the project to a new project and created this new repository, then fixed the error
- Tried to get the project even further back to only the DRIVE REST V3 API demo which enables to list the files

#### Day 8
- Presented my struggles to the class and they brainstormed with me how to solve it, adviced me to go to VIA to ask a computer science student (because the token could not be retreived for Google Drive REST API)
- Someone pointed out the SHA 1 key I use is probably only for testing purposes, will look into that if there is time
- Together with the Computer Science student enabled retreiving a token for Google Drive REST by using an asynctask
- With the token, enabled my old downloading code to download: and finally: it works!
- The table below sums up the differences of the APIs I found, finally the differences are clear to me

| GDAA (newest)                                           | REST V3                                           | 
| ------------------------------------------------------- | ------------------------------------------------- | 
| Google Play Services                                    | Different permissions due to deeper control       |
| Login with Google API client and sign In Options        | Log in by Google Account Credential               |   
| Can only work with files created in the app itself      | Can work with all files                           |    
| RequestToken by Web Client ID (API console)             | Token by credential.getToken AsyncTask            |    
| Log out is possible                                     | Log out / multiple accounts not clear             |    
| Easier way to create files with its predefined intent   | Handle all connections yourself (asynctasks etc.) |

#### Extra day
- Decided to code for an extra day, because some bugs took me a bit too long, yet I learned to solve problems more efficiently
- Decided that it is best to start the app with a sign in activity which redirects to the navigation drawer activity on success, so for this project the app can only handle one account
- Downloading is now working in the recyclerview

*Week 4*
TODO list;
- Design / code style 
- Debugging: think about what happens when a user for instance loses connection

- Enable the user to sort files 
- Show the trash can of files 
- Enable the user to search through files 
- Work with Drive folders
- Extra: enable the user to edit a file
- Extra: enable the user to upload 
- Enable user to create
- Enable the user to remove files 
- Enable the user to rename files
- Enable the user to select (all) files 
- Stay signed in / log out


