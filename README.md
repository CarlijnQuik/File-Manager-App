### BliFi: Find and save your files Blind
*App project of the Minor Programming at the University of Amsterdam*

#### Problem
We all know that frustrating moment when you forgot to save that one important file, you do not remember what name you gave it or where it is. At the same time, your files are all over the place and you haven't found the time to sort them yet. BliFi makes sure you will never lose a file again.

#### Functionality

##### 1 An integrated overview of files
*Aim: To search through all your files at once and be aware of the location of files (privacy/cloud).*
- Minimum Viable Product (MVP): At least one external storage place such as Google Drive.
- Ideal Future Product (IFP): The most commen storage platforms can be integrated, 
including an overview of what files are in the phones internal storage and eventual SD card or micro USB.

This first function is the most important and minimum. All functions hereafter are optional.

##### 2 Labelling and sorting
*Aim: To give files a more findable name and be able to sort them easily.*
- An example is the labelling function build into gmail.
- The option to label photos in whatsapp as "important", 
so these are kept and the rest are deleted after a certain period of time to create space.

##### 3 Backup
*Aim: Make sure no file is lost when one of the storage places malfunctions.*
- Notify when files do not have a backup yet. 
- Let the user decide where to save backups of files.
- Synchronize automatically to the users wishes.
- Identify files that are unnecessary duplicates and ask the user to delete them.

##### 4 Automatic saving
*Aim: Save files automatically so you will not lose them by mistake.*
- A backup of any created file that will automatically be removed after a chosen time period.
- Automatic exporting of Facebook photos you are tagged in to your phone so you will not lose them when the owner deletes them.
    
#### Sketch of visualization
<img src="https://cloud.githubusercontent.com/assets/22945709/21845853/a353540a-d7f4-11e6-83f9-5da9875df450.png" width="500">
    
#### Data sets, sources and APIs
- A Google Drive API exists that allows editing and deleting files.
- Access to the files on the phone: files, gallery.
- IFP: Access to WhatsApp and Facebook.
- IFP: Access to SD and micro USB.

#### Linking functionality
- Cloud and internal storage should be linked in order to work together.
- IFP: Notifying the user, so access to the phones sound system.
- Log in activity to Google Drive.

#### Problems that might occur
- To search through both internal and external storage at once, do the files need to be downloaded on the phone (preferably not)? 
- How to access the phones photo library, files and sound system.
- How to access Facebook, WhatsApp, SD and micro SD
- How to recognize duplicate files: filename?
- Automatic saving?

#### Existing applications with the same functionality
- Google Drive
- Files App on phone
- OneDrive
- DropBox
- Facebook
- GMail
