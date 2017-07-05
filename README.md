# Password Predict and Strength Measure

Password Prediction Service to help users pick better passwords

![Demo gif](/passwd_predict_demo.gif?raw=true "Demo GIF")

While creating an account at any online service, users tend to choose passwords 
which have been proven to be easily guessable over the years. 
To help users in choosing stronger passwords, websites deploy strength meters. 
However, the logic of these strength meters is often ad-hoc and can be easily circumvented by users. 

In short:
The user can input a password and the system predicts next top 3 most likely choices 
that can occur in the next character as the user is typing, using data from rockyou-withcount.txt 
dataset of passwords, stored in Trie data structure.
At the start of the application, the rockyou-withcount.txt dataset is loaded 
and the system creates a Trie from the n-grams of the passwords with their frequencies. 
The system also shows prediction status :- whether user is choosing characters 
same as system predicted next likely characters.
The system also shows Password Strength based on Predictability 
after user finishes writing the password on a button click.

HOW TO RUN:

Method 1:

1. Download predict_passwd.jar file in jar folder.

2. I have not inserted rockyou-withcount.txt in this jar due to huge size.

3. Import the jar file in Eclipse

4. Download http://downloads.skullsecurity.org/passwords/rockyou-withcount.txt.bz2 
   and extract rockyou-withcount.txt

5. Please copy rockyou-withcount.txt in the same folder which has the Passwd.java file.

6. After that, open Passwd.java and click Run.

7. After the application loads, 
   please wait for the "Loading rockyou-withcount.txt. Please wait..." to disappear.

8. If you don't get the message: "rockyou-withcount.txt is not present in root directory of project" 
   on top of the application, it means you've copied the rockyou-withcount.txt file right.
   
Method 2:

1. Create an Ecliple project

2. Download / clone my project into src folder of your Eclipse project

3. Download http://downloads.skullsecurity.org/passwords/rockyou-withcount.txt.bz2 
   and extract rockyou-withcount.txt

4. Copy rockyou-withcount.txt to src folder.

5. After that, open Passwd.java in Eclipse and click Run.

6. After the application loads, please wait for the "Loading rockyou-withcount.txt. Please wait..." to disappear.

7. If you don't get the message: "rockyou-withcount.txt is not present in root directory of project" 
   on top of the application, it means you've copied the rockyou-withcount.txt file right.
   

This project has been developed in Java using Eclipse IDE.

It mainly contains three files:

Passwd.java (which has main method)

Trie.java (Utility class for performing operations on Trie)

TrieNode.java (TrieNode class)
   
There are a total of 32,603,387 plaintext passwords on the RockYou list, including duplicates. 
There are 14,344,391 unique passwords.
rockyou.txt or rockyou-withcount.txt do not have all the 32 million passwords.
Instead they have 14,344,391 distinct/unique passwords arranged in descending order of their occurrence count (frequency).

To predict next character, we need to do Prefix search.
I’m using Trie Data Structure.
Reading each password from rockyou-withcount.txt, creating n-grams for the password (currently n=5) and inserting the n-grams into the Trie with the frequency of that password.

Trie Node structure:

	class TrieNode {
		
		char c;

		// weight is frequency of the character as part of the n-gram
		// higher weight will be given more preference when finding next likely choices
		int weight;
		
		HashMap<Character, TrieNode> children = new HashMap<Character, TrieNode>();

		public TrieNode() {}

		public TrieNode(char c, int weight) {
			this.c = c;
			this.weight = weight;
		}
		
	}

Algorithm for creating Trie with n-grams of passwords with their frequencies:
1.	Read passwords and their frequency from rockyou-withcount.txt one by one.
2.	For each password, find all its n-grams. Considering n=5
3.	For each password, insert each gram into the trie. 
4.	When inserting, increase the weight of the character if already present as part of the gram, by the frequency of the password.

Algorithm for finding next top 3 most likely choices:
1.	Detect change in the input
2.	Check whether user typed one of the next likely choices. Update prediction status.
3.	For n-1 times: for(i = 0; i < n-1; i++)
	
	a.	Get substring from input: (length - n + 1 + i) ... (length-1) :– see below image
	
	b.	Search the trie for prefix (substring). Get TrieNode corresponding to last character of string.
	
	c.	Find 3 children of the TrieNode, with the highest weight among the siblings.
	
	d.	If all three next likely choices are found, then break, else repeat till all choices are found or loop completes

Password Strength can be calculated after user finishes typing the password, on a button click.

A very naïve formula: 

strength = number of user chosen characters not predicted correctly by system  / length of password

This needs to be improved upon.

 
Please see the working video for demonstration.
