package is322_main;
/*
 * InvertedIndex - Given a set of text files, implement a program to create an
 * inverted index. Also create a user interface to do a search using that inverted
 * index which returns a list of files that contain the query term / terms.
 * The search index can be in memory.
 */
import java.io.*;
import java.util.*;
//=====================================================================
class DictEntry2 {
    public int doc_freq = 0;
    public int term_freq = 0;
    public HashSet<Integer> postingList;
    DictEntry2() {
        postingList = new HashSet<Integer>();
    }
}
//=====================================================================
class Index2 {
    Map<Integer, String> sources;
    HashMap<String, DictEntry2> index;
    Index2() {
        sources = new HashMap<Integer, String>();
        index = new HashMap<String, DictEntry2>();
    }
    //---------------------------------------------
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry2 dd = (DictEntry2) pair.getValue();
            HashSet<Integer> hset = dd.postingList;
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
            Iterator<Integer> it2 = hset.iterator();
            while (it2.hasNext()) {
                System.out.print(it2.next() + ", ");
            }
            System.out.println("");
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //-----------------------------------------------
    public void buildIndex(String[] files) {
        int i = 0;
        for (String fileName : files) {
            try ( BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();
                        if (!index.containsKey(word)) {
                            index.put(word, new DictEntry2());
                        }
                        if (!index.get(word).postingList.contains(i)) {
                            index.get(word).doc_freq += 1;
                            index.get(word).postingList.add(i);
                        }
                        index.get(word).term_freq += 1;
                    }
                }
                printDictionary();
            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            i++;
        }
    }
    //--------------------------------------------------------------------------
    /*
     *this method take a phrase and then find the intersection of the words in it.
     *it split the phrase and put the words in the string array called words.
     *then find from id of the documents the paths for the files that all word exist in it and put them in rec.
     *if the rec size is 0 then this word not found
     *finally we print these paths that exist.
     */
    public String find(String phrase) {
        String[] words = phrase.split("\\W+");
        HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        //String arr[] = new String[res.size()];
        for (String word : words) {
            res.retainAll(index.get(word).postingList);
        }
        if (res.size() == 0) {
            System.out.println("Not found");//this word is not found
            return "";
        }
        String result = "Found in: \n";
        for (int num : res) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }
    //----------------------------------------------------------------------------
    HashSet<Integer> intersect(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet<>();
       List<Integer> p1= new ArrayList<>(pL1);
        List<Integer> p2= new ArrayList<>(pL2);
        List<Integer> answer1= new ArrayList<>(answer);
        int p1_index = 0, p2_index = 0;
        int increment = 0;
        while (p1_index < p1.size() && p2_index < p2.size()) {
            if (p1.get(p1_index) == p2.get(p2_index)) {
                    answer1.add(p1.get(p1_index));
                    ++p1_index;
                    ++p2_index;
                } else if (p1.get(p1_index) < p2.get(p2_index)) {
                    ++p1_index;
                } else {
                    ++p2_index;
                }
            ++increment;
        }
        HashSet<Integer> answer2 = new HashSet<Integer>(answer1);
        return answer2;
    }
    //-----------------------------------------------------------------------
    public String find_01(String phrase) { // 2 term phrase  2 postingsLists
        String result = "";
        String[] words = phrase.split("\\W+");
        // 1- get first posting list
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        // 2- get second posting list
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        // 3- apply the algorithm "use intersect function"
        HashSet<Integer> answer = intersect(pL1, pL2);
     //   System.out.println("Found in: ");
        for (int num : answer) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }
//-----------------------------------------------------------------------

    public String find_02(String phrase) { //  lists of 3 terms
        String result = "";
        String[] words = phrase.split("\\W+");
        // 1- get first posting list
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        // 2- get second posting list
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
        // 2- get third posting list
        HashSet<Integer> pL3 = new HashSet<Integer>(index.get(words[2].toLowerCase()).postingList);
        // 3- apply the algorithm "use intersect function"
        HashSet<Integer> answer = intersect(pL1, pL2);
        HashSet<Integer> answer2=intersect(answer,pL3);
        //System.out.println("Found in: ");
        for (int num : answer2) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;

    }
    //-----------------------------------------------------------------------
    /*this method take more than 3 parameters and get the intersection
     *we split the phrase in string array called words
     *then we check on the length of the array to know the number of words.
     *if there is only one word we print the posting list of this word.
     *if there is more than one word we get the posting list of the first two words and get the intersection of then using intersection method.
     *then the result of the intersection of the first two words become the first attribute to the intersect method with the remaining words.
     *finally we print the final answer.
     **/
    public String find_03(String phrase) { //more than 3 terms
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> answer=new HashSet<>();
         if(words.length == 1){
             HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
             //System.out.println("Found in: ");
             for (int num : pL1) {
                 result +=  "\t" + sources.get(num) + "\n";
             }
         }
        else if(words.length > 1){
                HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
                HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
                answer = intersect(pL1, pL2);
                HashSet<Integer> a3=new HashSet<>(answer);
             for (int i = 2; i < words.length; i++) {
                 HashSet<Integer> p1 = new HashSet<Integer>(index.get(words[i].toLowerCase()).postingList);
                 answer = intersect(a3, p1);
             }
        }
       // System.out.println("Found in: ");
        for (int num : answer) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }
    //-----------------------------------------------------------------------
    /*this method take more than 3 parameters and get the intersection using optimized search
     *we split the phrase in string array called words
     *then we check on the length of the array to know the number of words.
     *if there is only one word we print the posting list of this word.
     *if there is more than one word we get the posting list of the first two words and then the smallest size of them become the first parameter in the intersect method and get the intersection of them.
     *then we compare the size of the result with the posting list of the next word to send the smallest first to the intersect method.
     *finally we print the final answer.
     **/
    public String find_04(String phrase) { // optimized search
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> answer=new HashSet<>();
        HashSet<Integer> a2=new HashSet<>();
        if(words.length == 1){
            HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
            for (int num : pL1) {
                result +=  "\t" + sources.get(num) + "\n";
            }
        }
        else if(words.length > 1){
            HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
            HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
            if(pL1.size()<pL2.size())
            {
                answer = intersect(pL1, pL2);
                a2=new HashSet<>(answer);
            }
            else
            {
                answer = intersect(pL2, pL1);
                a2=new HashSet<>(answer);
            }
            for (int i = 2; i < words.length; i++) {
                HashSet<Integer> p1 = new HashSet<Integer>(index.get(words[i].toLowerCase()).postingList);
                if(a2.size()<p1.size())
                {
                    answer = intersect(a2, p1);
                }
                else
                {
                    answer = intersect(p1, a2);
                }
            }
        }
        for (int num : answer) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }
    //-----------------------------------------------------------------------
    /*
    * time of Find_04 is less than any times of Find_03 , Find because it implemented by optimized search
    * we notice that may be there is noice that made time of find or find_03 is less than find_04
    * if time of find and find_03 and find_04 almost equal because they are ordered
    * */
    public void compare(String phrase) {
        long iterations=100;
        String result = "";
        long startTime = System.currentTimeMillis();
        for (long i = 1; i < iterations; i++) {
            result = find(phrase);
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(" (*) elapsed = " + estimatedTime+" ms.");

        System.out.println(" result = " + result);
        startTime = System.currentTimeMillis();
        for (long i = 1; i < iterations; i++) {
            result = find_03(phrase);
        }
        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(" (*) Find_03 non-optimized intersect  elapsed = " + estimatedTime +" ms.");
        System.out.println(" result = " + result);

        startTime = System.currentTimeMillis();
        for (long i = 1; i < iterations; i++) {
            result = find_04(phrase);
        }
        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(" (*) Find_04 optimized intersect elapsed = " + estimatedTime+" ms.");
        System.out.println(" result = " + result);
    }
}
//=====================================================================
public class InvertedIndex002 {

    public static void main(String args[]) throws IOException {
        Index2 index = new Index2();
        String phrase = "";
        index.buildIndex(new String[]{
                "E:\\Y3 T2\\Information Retrieval Assignment\\src\\100.txt",
                "E:\\Y3 T2\\Information Retrieval Assignment\\src\\101.txt",
                "E:\\Y3 T2\\Information Retrieval Assignment\\src\\102.txt",
                "E:\\Y3 T2\\Information Retrieval Assignment\\src\\103.txt",
                "E:\\Y3 T2\\Information Retrieval Assignment\\src\\104.txt",
                "E:\\Y3 T2\\Information Retrieval Assignment\\src\\105.txt",
                "E:\\Y3 T2\\Information Retrieval Assignment\\src\\106.txt",
                "E:\\Y3 T2\\Information Retrieval Assignment\\src\\107.txt",
                "E:\\Y3 T2\\Information Retrieval Assignment\\src\\108.txt",
                "E:\\Y3 T2\\Information Retrieval Assignment\\src\\109.txt"
        });

        int number=0;
        String check="";
        boolean item = true;
        Scanner in = new Scanner(System.in);
        do {
            System.out.println("1- find_01 ");
            System.out.println("2- find_02");
            System.out.println("3- find_03");
            System.out.println("4- find_04");
            System.out.println("5- Compare");
            System.out.println("6- Exit");
            System.out.println("Enter number of operation : ");
            number = in.nextInt();
            in.nextLine();
            switch (number) {
                case 1:
                    System.out.println("Enter Phrase : ");
                    phrase = in.nextLine();
                    System.out.println(index.find_01(phrase));
                    break;
                case 2:
                    System.out.println("Enter Phrase : ");
                    phrase = in.nextLine();
                    System.out.println(index.find_02(phrase));
                    break;
                case 3:
                    System.out.println("Enter Phrase : ");
                    phrase = in.nextLine();
                    System.out.println(index.find_03(phrase));
                    break;
                case 4:
                    System.out.println("Enter Phrase : ");
                    phrase = in.nextLine();
                    System.out.println(index.find_04(phrase));
                    break;
                case 5:
                    System.out.println("Enter Phrase : ");
                    phrase = in.nextLine();
                    index.compare(phrase);
                    break;
                default:
                    item = false;
                    break;
            }
        }while (item == true);
    }
}