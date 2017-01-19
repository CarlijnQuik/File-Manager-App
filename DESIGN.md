### Design 

#### Screen 1: Google Drive Files Fragment (Accounts interface)
- The user is able to sign in using their Google account (done)

*Extras that can be added if there is time*
- Ability to create a Google account (done)
- Ability to stay signed in and change password (done -> user should do this using the account manager on the phone)
- Ability to log out and add more accounts
- Example: https://accounts.google.com/ServiceLogin?hl=en&passive=true&continue=https://www.google.nl/#identifier

**User Interface**
- The sign in button will sign the user in to their Google account
- There is enough space for eventual extra accounts

<img src="https://cloud.githubusercontent.com/assets/22945709/22077447/8583c624-ddb4-11e6-9b44-8c347decd731.png" width="250">

#### Screen 2: Internal Files Fragment (Recycler View interface)
- With pictures the file location can be recognized (done)
- On short click a file or folder is opened (done for internal files)
- There is a search function to search through the files
- The menus enable the user to select files and move them (MVP)

*Extras that can be added*
- As in Google Drive, selecting menu B enables the functions; sort by, select and select all
- Menu C enables the user to move the file to the cloud or phone's hardware, remove and edit it
- The adapter shows when the file was last modified
- The gridview option is replaced by the small arrow that allows sorting the other way round

*Extra's that can be added but are not that important*
- The option to show the files in a gridview is added to the toolbar.
- An icon shows the type of file or folder (done)
- A picture has a preview in the image view on the adapter
- A labelling function is build in and integrated into menu C and D

*Extras that are probably too advanced to be build in within the given time*
- The App notices when a micro USB is added and distincts between internal storage and SD card (done for internal storage and SD)
- Icons show different online platforms the files are on, logging into more platforms is possible (only done for Drive)
- The option to label WhatsApp photo's as "important" and automatically remove the rest
- Backup and automatic saving as described in the proposal

**User Interface**
- The UI looks like the Google Drive UI

<img src="https://cloud.githubusercontent.com/assets/22945709/21816695/5259cfb8-d761-11e6-930b-fdab08e85a54.png" width="500">
<img src="https://cloud.githubusercontent.com/assets/22945709/22123486/0f6d31fe-de8c-11e6-8f26-7624fe6ad8ff.png" width="500">

**Flow of activities**

<img src="https://cloud.githubusercontent.com/assets/22945709/22124525/7d96cbdc-de90-11e6-9843-6cc6f69fc15d.png" width="500">
