package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveWordFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveWordFilter.class);

    // replacement characters
    private static final String REPLACEMENT = "***";

    // root node
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // adding to trie
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("Loading sensitive word file failed: " + e.getMessage());
        }
    }

    // adding a sensitive word to trie
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                //init child node
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            tempNode = subNode;
        }
        // set ending char
        tempNode.setKeywordEnd(true);
    }

    /**
     * Filtering sensitive words
     *
     * @param text text to filter
     * @return text after filtering
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // pointer 1
        TrieNode tempNode = rootNode;
        // pointer 2
        int begin = 0;
        // pointer 3
        int position = 0;
        // result
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            if (position >= text.length()) {
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
                continue;
            }

            // skip over specific chars
            if (isSymbol(c)) {
                // if pointer 1 is at root, then add it to the result and let pointer 2 move by one step
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                // pointer 3 moves by one step regardless of the position of the symbol
                position++;
                continue;
            }

            //check next node
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // strings that start at pointer 2 are not sensitive words
                sb.append(text.charAt(begin));
                // enter next position
                position = ++begin;
                // initialize the trie pointer
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd) {
                // sensitive word found. replace begin~position
                sb.append(REPLACEMENT);
                // enter next position
                begin = ++position;
                // initialize the trie pointer
                tempNode = rootNode;
            } else {
                // check next char
                if (position < text.length() - 1) {
                    position++;
                }
            }
        }

        // finalize the filter
        sb.append(text.substring(begin));

        return sb.toString();
    }

    // checking if a char is only a symbol
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF is the range for East Asian characters
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    private class TrieNode {

        // signal for the end of keyword
        private boolean isKeywordEnd = false;

        // child node
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // adding child
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        // retrieving child
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
