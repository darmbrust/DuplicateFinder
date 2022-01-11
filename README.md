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

![](https://user-images.githubusercontent.com/5016252/148861822-1ca5ed51-5705-4a9b-83f8-925d2374b1c5.jpg)

**CAUTION!**

This program will delete files (by design) when you tell it to. There is no undo feature. It will not put files into your recycle bin. 
I HIGHLY recommend that you make a copy of the folders you want to search for duplicates, before you do your removals. This way, if there is some 
really ugly bug in the program that causes the wrong files to be deleted, you still have your files. YOU HAVE BEEN WARNED! I will not be responsible 
for any of your files if you lose them.

Requirements

You will need to have Java version 1.8 or newer installed to use this program.  You can get it [here](https://adoptopenjdk.net/)

**Download**

(you agree to [this](LICENSE) license by downloading this software)

Download just the executable: [duplicateFinder.jar](https://github.com/darmbrust/DuplicateFinder/releases/download/1/duplicateFinder.jar). 
If you have java installed correctly on your machine, simply double clicking on the file should launch the program. If not, try "java -jar duplicateFinder.jar".

**Bugs**

Gasp! Yes, this software may have bugs.  You can report them here: https://github.com/darmbrust/DuplicateFinder/issues