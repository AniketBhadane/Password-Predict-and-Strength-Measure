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
 *  File: TrieNode.java
 *
 *  Author: Aniket Bhadane (aniketbhadane93@gmail.com)
 *
 *	Description:
 *  TrieNode class
 *  
*/

import java.util.HashMap;

class TrieNode {
	
    char c;

    // weight is frequency of the character as part of the n-gram
    // higher weight will be given more preference when finding most likely choices
    int weight;
    
    HashMap<Character, TrieNode> children = new HashMap<Character, TrieNode>();

    public TrieNode() {}

    public TrieNode(char c, int weight) {
        this.c = c;
        this.weight = weight;
    }
    
}