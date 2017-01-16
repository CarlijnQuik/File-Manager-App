### Process book

*Week 1*

#### Day 1
- Decided to create the App "BliFi"
- Worked out the proposal specifying the functionality now aimed for

<img src="https://cloud.githubusercontent.com/assets/22945709/21845853/a353540a-d7f4-11e6-83f9-5da9875df450.png" width="400">
#### Day 2
- Had a job interview in the morning, started in the afternoon at 13:00
- Set my personal goal for this week to finding out how to link the App with Google Drive and create the overview of files in a list
- Started the design document: decided to immediately turn the desired sign in activity into a prototype, to give an idea of the style of the App
- Looked into how to create an activity with a menu, search and settings option in the toolbar
- First concept of the design document is done

<img src="https://cloud.githubusercontent.com/assets/22945709/21811271/610f8794-d74f-11e6-9336-8ef359f75a76.png" width="300">
<img src="https://cloud.githubusercontent.com/assets/22945709/21816695/5259cfb8-d761-11e6-930b-fdab08e85a54.png" width="400">
#### Day 3
- Had a meeting with my peer group
- Set my goal for the day to finding out how to put the files that are in the internal storage in a list, because the entire project falls or stands with being able to use the data as pleased
- Found out how to get the path of files and reach them, still not sure how to open, edit and delete files from the App, but made the adapter of the listView anyway because I usually get stuck on the Adapter part
- Did indeed get stuck, the Adapter did not want to show the images, after trying it multiple ways together with Renske, the adapter finally showed the right images after putting them in the mipmap folder instead of the drawable folder and calling android:scr= in the xml file
- Wondering how to make the app be able to open folders in an effective way, will look into this tomorrow
- Goal for tomorrow: be able to view all files on phone and SD, and figure out how to open them

<img src="https://cloud.githubusercontent.com/assets/22945709/21855022/431ff736-d81d-11e6-8313-328bc2569183.png" width="200">

#### Day 4
- Continued editing the code to represent all files in an adapter and be able to click on folders to see the files in it
- Spent 4 hours on fixing another weird adapter error, in the end decided to copy and paste the old working version to a new project, which showed the mistake was in the layout xml file
- It is still not clear what the error was, but it works now, will try find out how to push to this Github location from the new project
- Tomorrow: finish adapterview with fileType icons etcetera, find out how to regain access to Git from new project and hopefully find out how to edit and open files

<img src="https://cloud.githubusercontent.com/assets/22945709/21895785/3a6d364a-d8e4-11e6-8906-33f29664cd38.png" width="200">

#### Day 5
- Presentations in the morning, main comment I could not give a straight forward answer to was: "Can you name a concrete problem the app solves?", will think about this for the future
- Made this new Git repository, saved the old at: https://github.com/carlijnq/nl.mprog.blifi.backup
- Repository still not synchronizing with project, waiting for assistance on monday
- Succeeded in letting the user view what's in a folder and open it

<img src="https://github.com/carlijnq/nl.mprog.blifi/blob/master/docs/file_open_1.png" width="200">
<img src="https://github.com/carlijnq/nl.mprog.blifi/blob/master/docs/file_open_2.png" width="200">
<img src="https://github.com/carlijnq/nl.mprog.blifi/blob/master/docs/file_open_3.png" width="200">
<img src="https://cloud.githubusercontent.com/assets/22945709/21933005/c29a96ec-d9a3-11e6-9eef-4b3a4462e30d.png" width="200">

*Week 2*

#### Day 6
- Decided to leave the back button not functioning and some other bugs within this implementation and to focus on creating a full version
- Decided to create the menus first so the prototype is complete, then create the Google Drive component
- Created a functioning Git repository by creating a new android studio project and copying everything to it to synchronize


