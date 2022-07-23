package com.example.campusbud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trie {

    public class TrieNode {
        Map<Character, TrieNode> children;
        char c;
        boolean isWord;

        public TrieNode(char c) {
            this.c = c;
            children = new HashMap<>();
        }

        public TrieNode() {
            children = new HashMap<>();
        }

        public void insert(String word) {
            if (word == null || word.isEmpty()) {
                return;
            }
            char firstChar = word.charAt(0);
            TrieNode child = children.get(firstChar);
            if (child == null) {
                child = new TrieNode(firstChar);
                children.put(firstChar, child);
            }

            if (word.length() > 1) {
                child.insert(word.substring(1));
            } else {
                child.isWord = true;
            }
        }
    }

    TrieNode root;

    public Trie(List<String> words) {
        root = new TrieNode();
        for (String word : words) {
            root.insert(word);
        }
    }

    public boolean find(String prefix, boolean exact) {
        TrieNode lastNode = root;
        for (char c: prefix.toCharArray()) {
            lastNode = lastNode.children.get(c);
            if (lastNode == null) {
                return false;
            }
        }
        return !exact || lastNode.isWord;
    }

    public boolean find(String prefix) {
        return find(prefix, false);
    }

    public void suggestHelper(TrieNode root, List<String> list, StringBuffer curr) {
        if (root.isWord) {
            list.add(curr.toString());
        }

        if (root.children == null || root.children.isEmpty()) {
            return;
        }

        for (TrieNode child: root.children.values()) {
            suggestHelper(child, list, curr.append(child.c));
            curr.setLength(curr.length() - 1);
        }
    }

    public List<String> suggest(String prefix) {
        List<String> list = new ArrayList<>();
        TrieNode lastNode = root;
        StringBuffer curr = new StringBuffer();
        for (char c : prefix.toCharArray()) {
            lastNode = lastNode.children.get(c);
            if (lastNode == null) {
                return list;
            }
            curr.append(c);
        }
        suggestHelper(lastNode, list, curr);
        return list;
    }
}


/*import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import kotlin.collections.ArrayDeque;

public class Trie {

    private TrieNode root;

    public Trie() {
        this.root = new TrieNode(' ');
    }

    public void addWord(String word) {
        char[] charArray = word.toCharArray();
        TrieNode temp = root;
        TrieNode tn = null;
        int index = 0;

        do {
            tn = temp.children[charArray[index] - 'A'];
            if (tn != null) {
                temp = tn;
                index++;
                if (index >= word.length()) {
                    temp.terminal = true;
                    temp.word = word;
                    return;
                }
            }
        } while (tn != null);

        for (; index < charArray.length; index++) {
            temp.children[charArray[index] - 'A'] = new TrieNode(charArray[index]);
            temp = temp.children[charArray[index] - 'A'];
        }

        temp.terminal = true;
        temp.word = word;
    }

    public String[] wordsByPrefix(String prefix) {
        char[] charArray = prefix.toCharArray();
        TrieNode temp = root;
        TrieNode tn = null;
        int index = 0;

        do {
            tn = temp.children[charArray[index] - 'A'];
            if (tn == null) {
                return null;
            }
            index++;
            temp = tn;
        } while (index < charArray.length);

        List<String> words = new ArrayList<String>();
        ArrayDeque<TrieNode> dq = new ArrayDeque<TrieNode>();
        dq.addLast(temp);
        while (!dq.isEmpty()) {
            TrieNode first = dq.removeFirst();
            if (first.terminal) {
                words.add(first.word);
            }

            for (TrieNode n: first.children) {
                if (n != null) {
                    dq.add(n);
                }
            }
        } return words.toArray(new String[0]);
    }
}*/
