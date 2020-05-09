/**
 * @author Jeffrey Kozik
 */

/**
 * External resources referenced
 * https://canvas.case.edu/courses/21069/files/folder/Lecture%20Notes?preview=2353423 -> Data Structures lecture notes on Hash Tables
 * https://canvas.case.edu/courses/18665/files/2086387?module_item_id=731050 -> Intro to Java example linked list class
 * https://canvas.case.edu/courses/18665/files/2041343?module_item_id=724999 -> Intro to Java lecture notes on for each loops
 * https://www.tutorialspoint.com/java/java_regular_expressions.htm -> Refresher on Regular Expressions
 * https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#split-java.lang.String- -> Java API on String.split()
 * https://canvas.case.edu/courses/18665/files/2019317?fd_cookie_set=1 -> Intro to Java Lab on File Reading
 * https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuffer.html -> Java API on String Buffer
 * https://docs.oracle.com/javase/8/docs/api/java/io/PrintStream.html -> Java API on PrintStream
 * https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html -> Java API on BufferedReader
 * https://docs.oracle.com/javase/8/docs/api/java/io/FileOutputStream.html -> Java API on FileOutputStream
 * https://canvas.case.edu/courses/21069/files/folder/Lecture%20Notes?preview=2353429 -> Data Structures lecture notes on Rehashing
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;

/** A class representing a hash table and used to find the frequency of every word in a text file. */
public class HashTable{
    /** The array representation of the hash table. The array is composed of HashLists (similar to API's Linked List) */
    private HashList[] hashArray;
    /** Stores the total number of unique words being stored in the hash table. Used to calculate load factor to rehash. */
    private int numItems;

    /** 
     * Creates a new instance of a hash table
     * @param size The initial size of the array representing the hash table
    */
    public HashTable(int size){
        this.hashArray = new HashList[size];
        this.numItems = 0;

        /** Initializes each element in the array as an empty HashList, each iteration does a different one */
        for(int i = 0; i < hashArray.length; i++){
            hashArray[i] = new HashList();
        }
    }

    /** Prints to the console what the hash table currently looks like, complete with numbers for the index in the array*/
    public void printHashTable(){
        /** The index in the array */
        int row = 0;

        /** Prints to the console the hash table, each iteration goes through separate element in array */
        for(HashList element: this.hashArray){
            System.out.print(row + " ");
            element.printHashList();
            System.out.println();
            row++;
        }
    }

    /** 
     * Rehashes the table when the load factor is equal to or greater than 1 by creating new hash table
     * Putting all the words into that table
     * And making the original hash table's array equal to the new hash table's array
     */
    public void rehash(){
        /** 
         * The new hash table that is twice the size of the original hash table.
         * This ensures the methods are working efficiently and the load factor isn't too high
         * Also, by doubling (and starting with the size being a factor of 2), the table size is never 31 which causes problems with the hashing
         */
        HashTable newTable = new HashTable(this.hashArray.length * 2);

        /** Goes through each element in the array (each HashList) and rehashes all of them; each iteration does a different one */
        for(HashList element: this.hashArray){
            element.rehash(newTable);
        }

        this.hashArray = newTable.hashArray;
    }

    /** 
     * Puts the desired word into the hash table by utilizing HashList's put function as well 
     * @param word The word to add to the hash table (or increment the frequency of if already present)
     */
    public void put(String word){
        /** 
         * Puts the desired word in the proper position within the array and within the HashList. 
         * If the word being added doesn't already exist in the hash table, it's a new item and thus impacts the load factor
         * As a result, the numItems field is incremented
         */
        if(hashArray[Math.abs(word.hashCode()) % this.hashArray.length].put(word)){
            this.numItems++;
        }

        /** If the load factor is greater than one, the hash table is rehashed. */
        if((this.numItems/this.hashArray.length) >= 1){
            this.rehash();
        }
    }

    /** 
     * Reads through the inputted file line by line and keeps track of the frequency of each word through a hash table
     * Once the file is completely read through, outputs the frequency of each word to the output file
     * @param input_file The file to be read through whose word frequencies will be reported
     * @param output_file The file to output the word frequencies to
     */
    public String wordCount(String input_file, String output_file) {
        /** If everything goes right in terms of the input and output files being valued, the method carries out as intended */
        try{
            /** File reader to read by line the inputted file */
            BufferedReader inputFileReader = new BufferedReader(new FileReader(input_file));
            /** File printer to report the frequencies of each word */
            PrintStream outputFile = new PrintStream(new FileOutputStream(output_file));
            /** String representation of the current line */
            String currentLine = inputFileReader.readLine();

            /** Keeps going until the end of the file is reached, each iteration puts word in hash table */
            while(currentLine != null){
                /** 
                 * Array representation of current line split up into separate "words" 
                 * Words are defined for this assignment as a series of consecutive letters
                 */
                String[] splitString = currentLine.toLowerCase().split("[^a-z]+");

                /** Puts each word from a line into the hash table; each iteration goes to the next element in the splitString array */
                for(int i = 0; i < splitString.length; i++){
                    if(splitString[i].length() > 0){
                        this.put(splitString[i]);
                    }
                }

                currentLine = inputFileReader.readLine();
            }

            /** Used to store string represenation of word frequencies to print into output file */
            StringBuffer buffer = new StringBuffer();

            /** For each element in the hashArray (so each HashList) the string representation is gathered and added to the buffer */
            for(HashList element: this.hashArray){
                buffer.append(element.hashListToString());
            }

            buffer.append("The average length of the collision lists was " + (double)this.numItems/this.hashArray.length);

            outputFile.print(buffer.toString());
            inputFileReader.close();
            outputFile.close();
            return "OK";
        }
        /** If the file isn't found, the user is told so */
        catch(FileNotFoundException e){
            return "File Not Found";
        }
        /** If there is an error with the inputting and outputting procedures the user is made aware */
        catch(IOException e){
            return "Input File Error";
        }
    }

    /** Class similar to API's LinkedList that stores information about words, frequencies, and pointers */
    private class HashList{
        /** The first node in the list */
        private WordNode front;

        /** Creates new instance of HashList initially with a null first node */
        public HashList(){
            this.front = null;
        }

        /**  
         * Sets the first node of the list
         * @param front The new first node of the list
        */
        public void setFront(WordNode front){
            this.front = front;
        }

        /**
         * Gets the first node of the list
         * @return this.front The first node of the list
         */
        public WordNode getFront(){
            return this.front;
        }

        /**
         * Adds the indicated word to its respective linked list
         * @param word The word to add
         * @return newWordAdded Whether or not the word being put is new or its frequency has just been incremented
         * This is used so that the numItems of the HashTable can be accordingly incremented
         */
        public boolean put(String word){
            /** Pointer to help iteration */
            WordNode ptr = this.getFront();
            /** If the word has been found, or added, used to terminate loop */
            boolean wordFound = false;
            /** Whether or not the word is unique, or has already been added */
            boolean newWordAdded = true;

            /** If the list is empty, this word is obviously unique, and made the first node of the list */
            if(ptr == null){
                this.setFront(new WordNode(word));
                wordFound = true;
            }

            /** Until something is done with the word - either its frequency is incremented or it's added to the list, the loop runs */
            while(!wordFound){
                /** If this word is already in the list, that words frequency is incremented and it's indicated that it isn't new*/
                if(ptr.getWord().equals(word)){
                    ptr.incrementFrequency();
                    wordFound = true;
                    newWordAdded = false;
                }
                /** Otherwise, if the end of the list has been reached, this word is added to the end */
                else if(ptr.getNext() == null){
                    ptr.setNext(new WordNode(word));
                    wordFound = true;
                }
                /** If neither of the above are true, the list is continued to be iterated through, keep trying */
                else{
                    ptr = ptr.getNext();
                }
            }
            return newWordAdded;
        }

        /**
         * Reassigns every word in this list to the new hash table
         * @param newTable The new HashTable to hash everything to
         */
        public void rehash(HashTable newTable){
            /** Pointer to help with iteration */
            WordNode nodeptr = this.getFront();
            /** Goes until the end of the list; each iteration put the word into the new hash table */
            while(nodeptr != null){
                /** The element of the new hash table's array to insert this word into */
                HashList hl = newTable.hashArray[Math.abs(nodeptr.getWord().hashCode()) % newTable.hashArray.length];
                /** 
                 * In order to add this node's contents to the new hash table without
                 * messing up the process of extracting every word, its contents must be copied
                 */
                WordNode tempNodeptr = new WordNode(nodeptr.getWord());
                tempNodeptr.setFrequency(nodeptr.getFrequency());
                tempNodeptr.setNext(hl.getFront());
                hl.setFront(tempNodeptr);
                nodeptr = nodeptr.getNext();
            }
        }

        /** Prints a representation of this hashlist in terms of each words frequency */
        public void printHashList(){
            /** Pointer to help with iteration */
            WordNode nodeptr = this.getFront();
            /** Iterates until end of list; each iteration prints out the next word */
            while(nodeptr != null){
                System.out.print("(" + nodeptr.getWord() + " " + nodeptr.getFrequency() + ")");
                nodeptr = nodeptr.getNext();
            }
        }

        /**
         * Turns hash list into a string. Frequency and word bundled together and separated by line
         * @return String representation of this list to be printed into output file
         */
        public String hashListToString(){
            /** Helps with iteration */
            WordNode nodeptr = this.getFront();
            /** Builds string */
            StringBuffer buffer = new StringBuffer();
            /** Iterates until end of list */
            while(nodeptr != null){
                buffer.append("(" + nodeptr.getWord() + " " + nodeptr.getFrequency() + ")\n");
                nodeptr = nodeptr.getNext();
            }
            return buffer.toString();
        }

        /** Class to wrap word, frequency, and pointers */
        private class WordNode{
            /** Word being stored */
            private String word;
            /** Amount of times that word has showed up throughout document */
            private int frequency;
            /** What this node points to */
            private WordNode next;
    
            /**
             * Creates new instance of WordNode
             * @param word Word being wrapped
             */
            public WordNode(String word){
                this.word = word;
                this.frequency = 1;
                this.next = null;
            }
    
            /**
             * @return Word being stored
             */
            public String getWord(){
                return this.word;
            }

            /**
             * @return Frequency being stored
             */
            public int getFrequency(){
                return this.frequency;
            }

            /**
             * @param frequency New frequency to store
             */
            public void setFrequency(int frequency){
                this.frequency = frequency;
            }
    
            /**
             * Increases frequency by 1
             */
            public void incrementFrequency(){
                this.frequency++;
            }
    
            /**
             * @return The next WordNode in the list
             */
            public WordNode getNext(){
                return this.next;
            }
    
            /**
             * @param next The new next WordNode
             */
            public void setNext(WordNode next){
                this.next = next;
            }
        }
    }

    /**
     * Computes frequency of every word in a file and outputs to a different file
     * @param args What user types, intended to be input and output file names
     */
    public static void main(String[] args) {
        /** If the input file and output file are given no more no less */
        try{
            /** HashTable to store all data. Starts off as size 8 to keep load factor under control */
            HashTable ht = new HashTable(8);
            System.out.println(ht.wordCount(args[0], args[1]));
            /** Used to store string represenation of word frequencies to print into output file */
            StringBuffer buffer = new StringBuffer();

            /** For each element in the hashArray (so each HashList) the string representation is gathered and added to the buffer */
            for(HashList element: ht.hashArray){
                buffer.append(element.hashListToString());
            }

            System.out.print(buffer.toString());
            System.out.println("The average length of the collision lists was " + (double)ht.numItems/ht.hashArray.length);
        }
        /** If user doesn't use correct inputs he/she is told */
        catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Please type the file name you'd like the word frequency of, and the file name to output to.");
        }
    }
}