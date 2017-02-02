### Design 

#### Screen 1: Sign in activity
- The user is able to sign in using their Google account.
- This will be coupled to FireBase using the following code: https://github.com/firebase/quickstart-android/blob/master/auth/app/src/main/java/com/google/firebase/quickstart/auth/GoogleSignInActivity.java

*Extras that can be added if there is time*
- Ability to create a Google account.
- Ability to stay signed in and change password.
- Example: https://accounts.google.com/ServiceLogin?hl=en&passive=true&continue=https://www.google.nl/#identifier

**User Interface**
- The sign in button will sign the user in to their Google account.
- Some space is left in the layout for eventual extra buttons.

#### Screen 2: Overview of files activity
- With colors, the distinction between files in the cloud and on the phone is made.
- On short click a file or folder is opened (the screen that opens looks exactly like this screen).
- There is a search function to search through the files.
- The menu that appears when multiple items are selected is from now on referred to as menu D.
- For now, menu D has the same options as menu C (see image).

*Extras that can be added*
- As in Google Drive, selecting menu B enables the functions; sort by, select and select all.
- Menu C enables the user to move the file to the cloud or phone's hardware, remove and rename it.
- The adapter shows when the file was last modified.
- The gridview option is replaced by the small arrow that allows sorting the other way round.

*Extra's that can be added but are not that important*
- The option to show the files in a gridview is added to the toolbar.
- An icon shows the type of file or folder.
- A labelling function is build in and integrated into menu C and D.

*Extras that are probably too advanced to be build in within the given time*
- The App notices when a micro USB is added and distincts between internal storage and SD card.
- Icons show different offline places the files are in, like USB drive or SD card. 
- Icons show different online platforms the files are on, logging into more platforms is possible.
- The option to label WhatsApp photo's as "important" and automatically remove the rest.
- Backup and automatic saving to avoid files get lost.

**User Interface**
- The UI looks like the Google Drive UI.

<img src="https://cloud.githubusercontent.com/assets/22945709/21816695/5259cfb8-d761-11e6-930b-fdab08e85a54.png" width="500">

**Flow of activities**

<img src="https://cloud.githubusercontent.com/assets/22945709/21922991/e8b55236-d971-11e6-8307-d48c9f8b4ad6.png" width="300">
