import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/* By refingn I mean doing following things:
 * 1. Removing hyperlinks
 * 2. Removing @user and hashtag
 * 3. Removing 'not required words' like 'a','an','the' etc as they dont carry any sentiment
 * 4. Changing smileys to there value(bc ye nahi ho pa raha)
 * 5. changing happpppyyyyyyy!!!!! to happy!(this sort of things)
 * 6. stemming...What is stemming?? It essentially means converting the word to its root
 *    i.e changing word like 'trying' to 'try', 'amazing' to 'amaze'...It will reduce the classifier size very much*/

/* The end result of this class would be a file 'cleanedData.txt' which will have thousands of line, and each line will have two values:
 * 1. the nature of the line(positive or negative).
 * 2. Refined tweet*/

/* 'data.csv' is the raw data file and I am making a new file 'cleanedData.txt' with it*/

public class DataRefiner {
	int counter = 0;

	Map<String, String> emotiMap = new HashMap<String, String>();
	Map<String, String> map = new HashMap<String, String>();

	/*** To populate the emotiMap hashmap */
	public void mapConstruct() {
		emotiMap.put(">:]", "smile");
		emotiMap.put(":-)", "smile");
		emotiMap.put(":)", "smile");
		emotiMap.put(":o)", "smile");
		emotiMap.put(":]", "smile");
		emotiMap.put(":3", "smile");
		emotiMap.put(":c)", "smile");
		emotiMap.put(":>", "smile");
		emotiMap.put("=]", "smile");
		emotiMap.put("8)", "smile");
		emotiMap.put("=)", "smile");
		emotiMap.put(":g", "smile");
		emotiMap.put(":^)", "smile");
		emotiMap.put(">:D", "laugh");
		emotiMap.put(":-D", "laugh");
		emotiMap.put(":D", "laugh");
		emotiMap.put("8-D", "laugh");
		emotiMap.put("8D", "laugh");
		emotiMap.put("x-D", "laugh");
		emotiMap.put("xD", "laugh");
		emotiMap.put("=D", "laugh");
		emotiMap.put("=-D", "laugh");
		emotiMap.put("=3", "laugh");
		emotiMap.put("=-3", "laugh");
		emotiMap.put(":'(", "cry");
		emotiMap.put(";*(", "cry");
		emotiMap.put(":(", "cry");
		emotiMap.put(">:[", "frown");
		emotiMap.put(":-(", "frown");
		emotiMap.put(":(", "frown");
		emotiMap.put(":-c", "frown");
		emotiMap.put(":c", "frown");
		emotiMap.put(":-<", "frown");
		emotiMap.put(":<", "frown");
		emotiMap.put(":-[", "frown");
		emotiMap.put(":[", "frown");
		emotiMap.put(":f", "frown");
		emotiMap.put(">;]", "wink");
		emotiMap.put(";-)", "wink");
		emotiMap.put(";)", "wink");
		emotiMap.put("*-)", "wink");
		emotiMap.put("*)", "wink");
		emotiMap.put(";-]", "wink");
		emotiMap.put(";]", "wink");
		emotiMap.put(";D", "wink");
		emotiMap.put(">:o", "surprise");
		emotiMap.put(">:O", "surprise");
		emotiMap.put(":-O", "surprise");
		emotiMap.put(":O", "surprise");
		emotiMap.put(":<", "angry");
		emotiMap.put(">:(", "angry");
		emotiMap.put(">:-C", "angry");
		emotiMap.put(">:C", "angry");
		emotiMap.put(">:O", "angry");
		emotiMap.put("D-:<", "angry");
		emotiMap.put(">:-(", "angry");
		emotiMap.put(":-@", "angry");
		emotiMap.put(":@", "angry");
		emotiMap.put(";(", "angry");

		String stopwords = " im a about above after again against all am an and any are aren't as at be because been before being below between both but by can't cannot could couldn't did didn't do does doesn't doing don't down during each few for from further had hadn't has hasn't have haven't having he he'd he'll he's her here here's hers herself him himself his how how's i i'd i'll i'm i've if in into is isn't it it's its itself let's me more most mustn't my myself no nor not of off on once only or other ought our ours ourselves out over own same shan't she she'd she'll she's should shouldn't so some such than that that's the their theirs them themselves then there there's these they they'd they'll they're they've this those through to too under until up very was wasn't we we'd we'll we're we've were weren't what what's when when's where where's which while who who's whom why why's with won't would wouldn't you you'd you'll you're you've your yours yourself yourselves";
		String[] arrin = stopwords.split(" ");

		for (int i = 0; i < arrin.length; i++)
			map.put(arrin[i], i + "");
	}

	/***
	 * This function will refine the training data
	 */
	Spelling sps = null;

	public void spellingInit() {
		try {
			sps = new Spelling("big.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void refineData() {

		/*
		 * I have done something similar in DataCollector class but the motive
		 * is different here
		 */

		String scan;
		FileReader file;
		FileOutputStream fileWrite;
		PrintStream ps = null;
		mapConstruct();
		spellingInit();
		try {
			fileWrite = new FileOutputStream("tryCleanedData.csv");
			ps = new PrintStream(fileWrite);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}

		try {

			file = new FileReader("tryingData");
			BufferedReader br = new BufferedReader(file);

			try {
				while (((scan = br.readLine()) != null)) {

					scan = new DataCollector().filter(scan, "\\?+",
							" zzqueszz ");
					scan = new DataCollector()
							.filter(scan, "!+", " zzexclezz ");

					// detecting smileys
					String[] words = scan.split(" ");
					for (int i = 0; i < words.length; i++) {
						if (emotiMap.containsKey(words[i].toString()))
							words[i] = emotiMap.get(words[i].toString());
					}
					scan = "";
					for (int i = 0; i < words.length; i++)
						scan += words[i] + " ";

					// // to generalise I am taking all tweets in small letters
					scan = scan.toLowerCase();

					// to remove all the URLS
					scan = new DataCollector().filter(scan,
							"http[://a-zA-Z0-9./]+");

					// // to remove all the @user thing...
					scan = new DataCollector().filter(scan,
							"@[a-zA-Z0-9!@#$%^&*()+/*+_~`]+");
					scan = new DataCollector().filter(scan, "'[sm]", " ");

					// removing , and extra spaces
					scan = new DataCollector().filter(scan, "[^a-zA-Z0-9 ]+",
							" ");

					scan = new DataCollector().filter(scan, " [\\s]+", " ");

					// stemming the words
					Stemmer s = new Stemmer();
					char[] arr = scan.toCharArray();
					s.add(arr, arr.length);
					s.stem();
					scan = s.toString();

					// Removing stop words (e.g. the, is, at, which, on).
					String[] wordis = scan.split(" ");
					for (int i = 0; i < wordis.length; i++) {
						if (map.containsKey(wordis[i].toString()))
							wordis[i] = "";
					}
					scan = "";
					for (int i = 0; i < wordis.length; i++)
						scan += wordis[i] + " ";

					// removing all the spaces from right and more than one
					// contimous space
					scan = new DataCollector().filter(scan, " [\\s]+", " ");
					scan = scan.trim();

					// // Spelling checker...this is a bit slow but I am looking
					// // for a faster version
					String[] words2 = scan.split(" ");
					scan = "";
					for (int i = 0; i < words2.length; i++)
						scan += sps.correct(words2[i]) + " ";					

					// finally write to cleanedData.csv
					ps.println(scan);
					System.out.println("done with " + ++counter);
				}
				System.out.println(map.toString());
				br.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
