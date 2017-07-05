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
 *  File: Trie.java
 *
 *  Author: Aniket Bhadane (aniketbhadane93@gmail.com)
 *
 *	Description:
 *  Utility class for performing operations on Trie
 *  
*/

import java.util.*;

public class Trie {
	
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Inserts a password into the trie.
    // Not using currently, wrote for testing purpose.
    // see insertGram method below
    public void insert(String word, int weight) {
        HashMap<Character, TrieNode> children = root.children;

        for(int i=0; i<word.length(); i++){
            char c = word.charAt(i);

            TrieNode t;
            if(children.containsKey(c)){
                    t = children.get(c);
            }else{
                t = new TrieNode(c, weight);
                children.put(c, t);
            }
                        
            t.weight = Math.max(weight, t.weight);

            children = t.children;

        }
    }
    
    // Inserts a gram into the trie.
    // Increments the weight of the character if already present
    // as part of the gram.
    public void insertGram(String word, int weight) {
        HashMap<Character, TrieNode> children = root.children;

        for(int i=0; i<word.length(); i++){
            char c = word.charAt(i);

            TrieNode t;
            if(children.containsKey(c)){
                    t = children.get(c);
                    t.weight += weight;
            }else{
                t = new TrieNode(c, weight);
                children.put(c, t);
            }
            
            children = t.children;

        }
    }

    // Returns if there is any word in the trie
    // that starts with the given prefix.
    public TrieNode startsWith(String prefix) {
        TrieNode t = searchNode(prefix);
    	if(t == null)
            return null;
        else
            return t;
    }

    // Returns last node corresponding to last character of string
    // else return null if exact string not found
    public TrieNode searchNode(String str) {
        Map<Character, TrieNode> children = root.children;
        TrieNode t = null;
        for(int i=0; i<str.length(); i++){
            char c = str.charAt(i);
            if(children.containsKey(c)){
                t = children.get(c);
                children = t.children;
            } else{
                return null;
            }
        }

        return t;
    }
    
    // Find 3 children of the TrieNode, with the highest weight among the siblings
    public String[] findThreeChildren(TrieNode obj) {
    	
    	Map<Character, TrieNode> children = obj.children;
    	
    	int a, b, c;
    	a = b = c = Integer.MIN_VALUE;
		
    	String aName, bName, cName;
    	aName = bName = cName = "";    	
    	
    	for (Map.Entry<Character, TrieNode> entry : children.entrySet()) {
    		
    		TrieNode node = entry.getValue();
    		int curr_wt = node.weight;
    		String name = String.valueOf(node.c);
    	    
    		if(curr_wt > a) {
				c = b;			cName = bName;
				b = a;			bName = aName;
				a = curr_wt;	aName = name;
			}else if(curr_wt > b) {
				c = b;			cName = bName;
				b = curr_wt;	bName = name;
			}else if(curr_wt > c) {
				c = curr_wt;	cName = name;
			}
			
    	}
    	
         String[] res = new String[] {aName, bName, cName};
         return res;
     }
    
    
}