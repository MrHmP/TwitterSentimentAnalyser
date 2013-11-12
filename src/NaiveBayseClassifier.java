import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/* 
 * What I am doing in this class is basically reading every word present in the cleanedData file and 
 * assiging two numbers with it:-
 * 1. positive references
 * 2. Negative referances
 * It is sort of a hashmap with key being the word and value being the two numbers
 * */

public class NaiveBayseClassifier {
	FileReader file;
	String scan = "";
	FileOutputStream fileWrite;
	PrintStream ps = null;
	int counter = -1, totalTweets = 0, positiveTweets = 0, negativeTweets = 0;

	// this "valuemap" hashmap will have an object array as the value, in 0th
	// location no.
	// of +ve responses are stored and in 1st index no. of -ve responses are
	// stored

	private Map<String, Object[]> valueMap = new HashMap<String, Object[]>();
	private Map<String, Object[]> loadedMap = new HashMap<String, Object[]>();

	public void makeMap() {
		try {
			file = new FileReader("cleanedData.csv");
			BufferedReader br = new BufferedReader(file);
			try {
				while (((scan = br.readLine()) != null)) {
					totalTweets++;
					String[] words = scan.split(" ");
					if (words.length == 0)
						break;
					boolean result = words[0].toString().equals("positive") ? true
							: false;
					for (int i = 1; i < words.length; i++) {
						if (valueMap.containsKey(words[i].toString())) {
							Object[] tempObj = valueMap
									.get(words[i].toString());

							if (result) {
								positiveTweets++;
								int pos = Integer
										.valueOf(tempObj[0].toString());
								pos++;
								tempObj[0] = pos;
							} else {
								negativeTweets++;
								int neg = Integer
										.valueOf(tempObj[1].toString());
								neg++;
								tempObj[1] = neg;
							}
							// in hashmaps the row once inserted can not be
							// edited
							// so the only option is to delete the old row and
							// insert a new modified row
							valueMap.remove(words[i].toString());
							valueMap.put(words[i].toString(), tempObj);
						} else {
							Object[] tempObj = new Object[2];
							if (result) {
								tempObj[0] = 1;
								tempObj[1] = 0;
							} else {
								tempObj[0] = 0;
								tempObj[1] = 1;
							}
							valueMap.put(words[i].toString(), tempObj);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.print("t= " + (positiveTweets + negativeTweets)
					+ " pos= " + positiveTweets + " neg= " + negativeTweets);
			try {
				fileWrite = new FileOutputStream("BayesianClassifer.txt");
				ps = new PrintStream(fileWrite);
				Object[] tempObj = null;
				for (Entry<String, Object[]> entry : valueMap.entrySet()) {
					tempObj = entry.getValue();
					ps.println(entry.getKey() + " " + tempObj[0] + " "
							+ tempObj[1]);
				}
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void loadMapFromDisk() {
		String scan;
		try {
			file = new FileReader("BayesianClassifer.txt");
			BufferedReader br = new BufferedReader(file);

			/* Iterate over the classifier.txt and load each value in the map */
			while (((scan = br.readLine()) != null)) {
				Object[] tempObj = new Object[2];
				String[] values = scan.split(" ");
				tempObj[0] = values[1];
				tempObj[1] = values[2];
				loadedMap.put(values[0], tempObj);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void tweetScore(String tweet) {
		String[] words = tweet.split(" ");
		double actualpositive = 0, actualnegative = 0;
		for (int i = 0; i < words.length; i++) {
			try {
				double pos = 0, neg = 0;

				if (loadedMap.containsKey(words[i])) {
					Object[] tempObj = loadedMap.get(words[i]);
					pos = Double.parseDouble(tempObj[0].toString());
					neg = Double.parseDouble(tempObj[1].toString());
					double total = pos + neg;

					double posprob = pos / total;
					double negprob = neg / total;
					actualpositive += posprob;
					actualnegative += negprob;

				}
			} catch (Exception e) {
			}
		}
		double result=actualpositive/actualnegative;
		System.out.println("Result:"+result);
	}

	public int getNumberOfPositiveTweets(String key) {
		Object[] tempObj = loadedMap.get(key);
		return Integer.parseInt(tempObj[0].toString());
	}

	public int getNumberOfNegativeTweets(String key) {
		Object[] tempObj = loadedMap.get(key);
		return Integer.parseInt(tempObj[1].toString());
	}

	public int getNumberOfTotalTweets(String key) {
		Object[] tempObj = loadedMap.get(key);
		return (Integer.parseInt(tempObj[0].toString()) + Integer
				.parseInt(tempObj[1].toString()));
	}

	public int getTotalPositive() {
		return positiveTweets;
	}

	public int getTotalNegative() {
		return negativeTweets;
	}

	public int getTotalNoOfTweets() {
		return (positiveTweets + negativeTweets);
	}

}
