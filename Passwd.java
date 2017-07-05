/*

    Copyright 2017 Aniket Bhadane (aniketbhadane93@gmail.com)

    This file is part of passwd_predict - 
    Password Prediction Service to help users pick better passwords.

    passwd_predict is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    passwd_predict is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with passwd_predict.  If not, see <http://www.gnu.org/licenses/>.

*/

/*
 *  File: Passwd.java
 *
 *  Author: Aniket Bhadane (aniketbhadane93@gmail.com)
 *
 *	Description:
 *  Accepts user input password and predicts 
 *  next top 3 most likely choices that can occur in the next character 
 *  using data from rockyou-withcount.txt dataset of passwords,
 *  stored in Trie data structure.
 *  Also shows prediction status - whether user is choosing characters
 *  same as system predicted next likely characters.
 *  Also shows Password Strength based on Predictability after user finishes
 *  writing the password.
 *  
*/


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// NOTE:

// There are a total of 32,603,387 plaintext passwords on the RockYou list, including duplicates. 
// There are 14,344,391 unique passwords.
// rockyou.txt or rockyou-withcount.txt do not have all the 32 million passwords.
// Instead they have 14,344,391 distinct/unique passwords 
// arranged in descending order of occurrence count (frequency).

// To predict next character, we need to do Prefix search.

// Using Trie Data Structure.

// Reading each password from rockyou-withcount.txt,
// creating n-grams for the password (currently n=5)
// and inserting the n-grams into the Trie
// with the frequency of that password.


class Passwd {
	
	JFrame jfrm;   
	
	JLabel file_loading;
    JLabel pass_label;
    JLabel next_choice;
    JLabel first, second, third;
    JLabel predict_text;
    JLabel predict_status;
    JLabel strength;
    JLabel color_labels;
    JLabel footer;
    
    JTextField pass_input;
		  
    JButton btn_calcStrength;
	
	Trie trie;
	
	int n;
	
	int x; // used for moving first, second, third
	
	ArrayList<String> predict_list;
	
	String first_var = "";
	String second_var = "";
	String third_var = "";
	
	
	Passwd() {
		start();
		
		// Display frame
		jfrm.setVisible(true);
	}
	
	public void start() {

		// Create a new JFrame container.
		jfrm = new JFrame("Password Predictability & Strength Measure");
		
		// absolute positioning of all components
		jfrm.setLayout(null);
		
		// Give the frame an initial size.
		jfrm.setSize(500, 400);
		
		// align window to center of screen
		jfrm.setLocationRelativeTo(null);  
		// Terminate the program when the user closes the application.
		jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		file_loading = new JLabel("Loading rockyou-withcount.txt. Please wait...");
		pass_label = new JLabel("Type Your Password:");	
		next_choice = new JLabel("Next Likely Choice:");
		first = new JLabel("");
		second = new JLabel("");
		third = new JLabel("");
		predict_text = new JLabel("Prediction Status:");
		predict_status = new JLabel("");
		strength = new JLabel("");
		color_labels = new JLabel("<html>(<font color='#32CD32'>Green</font>) - indicates unpredictable"
		 		+ "<br/>(<font color='red'>Red</font>) - indicates predictable");
		footer = new JLabel("© Aniket Bhadane 2017");
		 
		pass_input = new JTextField(100);
		
		btn_calcStrength = new JButton("Calculate Password Strength");             				
		
		jfrm.add(file_loading);
		jfrm.add(pass_label);
		jfrm.add(next_choice);
		jfrm.add(first);
		jfrm.add(second);
		jfrm.add(third);
		jfrm.add(predict_text);
		jfrm.add(predict_status);
		jfrm.add(strength);
		jfrm.add(color_labels);
		jfrm.add(footer);
		
		jfrm.add(pass_input);
		
		jfrm.add(btn_calcStrength);
		
		x = 150;
		
		// setBounds(x,y,width,height) x is actually vertical columns, and y is horizontal rows
		file_loading.setBounds(10, 0, 700, 50); 
		pass_label.setBounds(20, 50, 150, 20);
		next_choice.setBounds(30, 60, 300, 50);
		first.setBounds(x, 60, 80, 50); 
		second.setBounds(x, 80, 80, 50); 
		third.setBounds(x, 100, 80, 50); 
		predict_text.setBounds(30, 130, 200, 50);
		predict_status.setBounds(150, 130, 700, 50);
		color_labels.setBounds(30, 170, 700, 50);
		strength.setBounds(100, 260, 700, 50);
		footer.setBounds(290, 320, 700, 50);
		
		pass_input.setBounds(150,50,200,50);
		
		btn_calcStrength.setBounds(130, 230, 200, 30);
		
		pass_input.setSize(200,20);
	
		pass_input.setEditable(false);
		pass_input.setEnabled(false);
		
		btn_calcStrength.setEnabled(false);
		
		strength.setVisible(false);
		
		predict_list = new ArrayList<String>();
		
		trie = new Trie();
		
		n = 5; // 5-gram --> change as per your need
	    	
    	Runnable runnable = new Runnable(){

    		public void run(){
				InputStream in = getClass().getResourceAsStream("rockyou-withcount.txt"); 
				//File file = new File("rockyou-withcount.txt");
				//try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
					
					String line;
				    int count = 0;
				    
				    while ((line = br.readLine()) != null) {
				    	
				    	line = line.replace("\n", "").replace("\r", "");
				    	line = line.trim();
				    	
				    	if(line != null && !line.isEmpty()) {
				    		
				    		String[] words = line.split("\\s+");
				    		
				    		if(words.length == 2) {
				    		
					    		int freq = Integer.parseInt(words[0]);
					    		
					    		String password = words[1];
					    		
					    		ArrayList<String> grams = findNgrams(password, n);
					    		
					    		for (int i = 0; i < grams.size(); i++) {
									
					    			trie.insertGram(grams.get(i), freq);
								}
				    		
				    		}

				    	}
				    				    	
				    	// Loading whole rockyou dataset takes lot of time.
				    	// so currently limiting to 1000000 (1 Million) passwords,
				    	// which take few seconds to load on my machine
				    	count++;
				    	if(count>1000000)
				    		break;
				    }
				    
				    file_loading.setVisible(false);
				    pass_input.setEditable(true);
					pass_input.setEnabled(true);
					pass_input.requestFocus();
					btn_calcStrength.setEnabled(true);
					
				} catch (NullPointerException e) {  //} catch (FileNotFoundException e) {
					//e.printStackTrace();
					file_loading.setText("<html>rockyou-withcount.txt is not present in root directory of project."
							+ "<br/>Download http://downloads.skullsecurity.org/passwords/rockyou-withcount.txt.bz2</html>");
				} catch (IOException e) {
					e.printStackTrace();
				} 			
			}
			
    	};	
	    	
    	new Thread(runnable).start();
		
		pass_input.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				search();
			}
			public void removeUpdate(DocumentEvent e) {
				search();
			}
			public void insertUpdate(DocumentEvent e) {
				search();
			}
			
			public void search() {
				
				String curr_pass = pass_input.getText();
				
				int length = curr_pass.length();
				
				strength.setVisible(false);
				
				String yesOrNo = "no";
				
				try { // check whether user typed one of the next likely choices
					if(indexExists(curr_pass, length-1)) {
						if(curr_pass.charAt(length-1) == first_var.charAt(0) ||
							curr_pass.charAt(length-1) == second_var.charAt(0) ||
							curr_pass.charAt(length-1) == third_var.charAt(0))
	
							yesOrNo = "yes";
						else
							yesOrNo = "no";
					}
				} catch (Exception e) {
					// do nothing.
					// added because not always 
					// first_var second_var third_var may have a value
					// which throws exception on comparison
				}	
				
				first_var = "";
				second_var = "";
				third_var = "";		
				
				TrieNode node;
				
				for(int i = 0; i < n-1; i++) {
				
					String prefix = "";
					
					try {
						prefix = curr_pass.substring(length - n + 1 + i); // starting from this index
						
					} catch(Exception e) { /* do nothing, occurs when only 0 1 2 3 characters in input*/ }
					
					node = trie.startsWith(prefix);
					
					if (node != null) {					
						
						String[] res = trie.findThreeChildren(node);
						
						if(first_var.equals("")) // don't overwrite if previously filled
							first_var = res[0];
						if(second_var.equals(""))
							second_var = res[1];
						if(third_var.equals(""))
							third_var = res[2];								
						
						if(!first_var.equals("") && !second_var.equals("") && !third_var.equals(""))
							break; // all filled

					}
				}
				
				first.setText(first_var);
				second.setText(second_var);
				third.setText(third_var);			
				
				try {
		    	    predict_list.get( length-1 );
		    	    // predict[length] exists, which means a backspace
		    	    predict_list.remove(length);
		    	    
		    	    x -= 7;
		    	    first.setBounds(x, 60, 80, 50); 
		    		second.setBounds(x, 80, 80, 50); 
		    		third.setBounds(x, 100, 80, 50); 

		    	} catch ( IndexOutOfBoundsException e ) {
		    		// predict_list[length-1] does not exist, 
		    		// which means typed a new character or no character in password
		    		if(!indexExists(curr_pass, length-1)) { // input empty
		    			predict_list.remove(length);
		    			
		    			x = 150;
		    			first.setBounds(x, 60, 80, 50); 
		    			second.setBounds(x, 80, 80, 50); 
		    			third.setBounds(x, 100, 80, 50); 
		    		}
		    		else {	
		    			predict_list.add( yesOrNo );
		    		
		    			x +=7;
		    			first.setBounds(x, 60, 80, 50); 
		    			second.setBounds(x, 80, 80, 50); 
		    			third.setBounds(x, 100, 80, 50); 
		    		}
		    	}
				
				String predict_status_str = "<html>";
				
				for (int i = 0; i < predict_list.size(); i++) {
					
					if(predict_list.get(i) == "yes")
						predict_status_str +=  "<font color='red'>" + curr_pass.charAt(i) + "</font>";
					else
						predict_status_str +=  "<font color='#32CD32'>" + curr_pass.charAt(i) + "</font>"	;
					//predict_status_str +=  " " + curr_pass.charAt(i) + "-" + predict_list.get(i) + ",";
				}
				
				predict_status_str += "</html>";
				
				predict_status.setText(predict_status_str);
				
			}
		});		   	
    	
		btn_calcStrength.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				String curr_pass = pass_input.getText();
				
				int length = curr_pass.length();
				
				strength.setVisible(true);
				
				int not_predicted = 0;
				
				for (int i = 0; i < predict_list.size(); i++) {
					
					if(predict_list.get(i).equals("no"))
						not_predicted++;
					
				}
				
				int strength_var = Math.round((float)(not_predicted) / length * 100);
				
				strength.setText("Password Srength based on Predictability: " + strength_var + "%");			
				
			}
		});

    }	
	
	public boolean indexExists(String array,int index) {
	    if(array!=null && index >= 0 && index < array.length())
	        return true;
	    else 
	       return false;
	}
	
	// Finds all n-grams of a password
	public ArrayList<String> findNgrams(String input, int n) {
		
		ArrayList<String> grams = new ArrayList<String>();
		
		if(n <= input.length()) { // ignoring shorter passwords				
			
			int maxStartIndex = input.length() - n + 1;
			
		    for (int i = 0; i < maxStartIndex; i++)
		    	grams.add(input.substring(i, i + n));
		    
		}
		
		return grams;
	}
	
	public static void main(String args[]) {

		// Create the frame on the event dispatching thread.
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				
				new Passwd();					
				
			}
		});
	}
}