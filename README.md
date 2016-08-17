# DuplicateFinder

 This is a program I wrote to scan folders on my computer, and tell me where I have duplicate files.  It will work on any type of file - comparing files byte by byte.
 
**Features**

 - Written in Java - works on any platform java runs on - including Linux, Mac, and if your in a pinch, Windows.
 - Finds files that are identical to each other, while ignoring the file names, dates, timestamps, and locations.
 - Search only specific folders, or an entire drive
 - Uses MD5 Checksums to detect duplicates.
 - If MD5 isn't good enough, you can optionally do a byte-for-byte comparison of MD5 indicated duplicates.
 - Manual or automatic duplicate removal
 - Preview for certain file types - jpg's, txt files, etc.
 - Its Free!

**Screen Shot**

![](http://armbrust.dyndns.org/programs/duplicateFinder/duplicateFinder.jpg)

**CAUTION!**

This program will delete files (by design) when you tell it to. There is no undo feature. It will not put files into your recycle bin. I HIGHLY recommend that you make a copy of the folders you want to search for duplicates, before you do your removals. This way, if there is some really ugly bug in the program that causes the wrong files to be deleted, you still have your files. YOU HAVE BEEN WARNED! I will not be responsible for any of your files if you lose them.
Requirements

You will need to have Java version 1.4 or newer installed to use this program.

**Download**

(you agree to [this](LICENSE) license by downloading this software)

Click [here](http://armbrust.dyndns.org/programs/duplicateFinder/DuplicateFinder.jnlp) to launch the program with webstart.

Note: When you first start the program, java will recommend that you not run the program because their is no proper signing authority. You will just have trust that no one has hacked my webserver, and replaced the program with a malicious one.

Otherwise, you can download just the executable: [duplicateFinder.jar](http://armbrust.dyndns.org/programs/duplicateFinder/duplicateFinder.jar). If you have java installed correctly on your machine, simply double clicking on the file should launch the program. If not, try "java -jar duplicateFinder.jar".

**Bugs**

Gasp! Yes, this software may have bugs.  You can report them here: https://github.com/darmbrust/DuplicateFinder/issues