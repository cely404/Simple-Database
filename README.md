# TuroInterview

The SimpleDB class was written in java. It takes input from standard 
input and prints output to standard output. 
USAGE: 
SET name value
GET name
UNSET name
NUMEQUALTO value
END
BEGIN
ROLLBACK
COMMIT 

USAGE, SET, GET etc can are not case sensitive. All other input 
is case sensitive. The application will not crash if an invalid 
input is given, instead it will prompt the user to try another 
input and will terminate when an EOF is reached or the user inputs "END". 


To compile: javac SimpleDB.java
To run: java SimpleDB

The program makes use of two HashMaps and one 2D ArrayList. 
One Hashmap contains the <name, value> pairs 
The other contains the <value, number of values equal to value>

The 2-D ArrayList acts as the cache that keeps track of the inital
values of the variables changed in each begin block. The ArrayList 
will contain no duplicates in each of its arraylists as it only aims 
to keep track of the inital value of any item modified and once that value
is present in that arraylist, it is never updated. When 
the ROLLBACK command is initiated, the 2D arraylist will take 
the last ArrayList and revert to the values that the variables contained 
at the beginning of the begin block 

The COMMIT command will simply clear the cache and all changes are preserved

