UberVerificationSystem
======================

The in app purchase system provided by Google is missing one very important thing. 
This tool will allow app developers to provide their users with codes to be redeemed in app for extra content. 


This system uses Parse.com as a simple and free backend to manage your users and codes. All you need to do is create a free account and setup your app. Once you have your client and application keys, you are ready to go.

To generate codes, look in the /scripts/ folder for the correct generate script and follow the instructions. Please be sure to update the keys in push_code.sh script.
For example: 

To generate 5 codes with the sponser name "Bowser" on a Liux machine,

r2doesinc@t3hh4xx0r:~/Dropbox/Android/AndroidStudioProjects/UberVerificationDemo/scripts$ ./generate_codes_on_linux.sh 5 saved_codes.txt Bowser

This will generate 5 codes, save those values to saved_codes.txt, and insert Bowser as the code sponser. The sponsor is a way to track giveaway campaigns, etc. The app can use this sponsor value to setup branding or things of that sort. 

Please check out the exampple app for more information.
