import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.inet.jortho.SpellChecker;

class DataCollector {

	/*
	 * Hash map of emoticon and name >:] :-) :) :o) :] :3 :c) :> =] 8) =) :g :^)
	 * ........smile >:D :-D :D 8-D 8D x-D xD X-D XD =-D =D =-3 =3.......laugh
	 * :'( ;*( : ( .........cry >:[ :-( :( :-c :c :-< :< :-[ :[ :f ........frown
	 * >;] ;-) ;) *-) *) ;-] ;] ;D ........wink >:o >:O :-O :O ........surprise
	 * D:< >:( >:-C >:C >:O D-:< >:-( :-@ :@ ;(.......angry
	 */

	Map<String, String> emotiMap = new HashMap<String, String>();

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
	}

	/***
	 * Function to apply specified regural expression on the tweet
	 * */
	public String filter(String scan, String regex) {
		try {
			Pattern pt = Pattern.compile(regex);
			Matcher match = pt.matcher(scan);
			while (match.find()) {
				String s = match.group();
				scan = scan.replaceAll(s, " ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return scan.toString();

	}

	public String emoticonToName(String emoticon) {
		String str = null;
		return str;
	}

	/*** Function to retreive tweets from twitter */
	public void collectData() {

		final int LIMIT = 100;
		int minId = 0;
		int sinceId = 0;

		// Your Twitter App's Consumer Key
		String consumerKey = "ecGHfsMwx38UCNC6CbQI3w";

		// Your Twitter App's Consumer Secret
		String consumerSecret = "4kBTO8pwa7XpwdoIEGKkNTUdHODOe4TywXKgBXuEE";

		// Your Twitter Access Token
		String accessToken = "168741775-mQjCUa9TrRajOkWqmgatZZ7vxFPUxFzIQwXfZdYm";

		// Your Twitter Access Token Secret
		String accessTokenSecret = "JiRAZnqqJw75CiwohFrDo7yswkXc0IR7Tsz6I73lo";

		// Instantiate a re-usable and thread-safe factory
		TwitterFactory twitterFactory = new TwitterFactory();

		// Instantiate a new Twitter instance
		Twitter twitter = twitterFactory.getInstance();

		// setup OAuth Consumer Credentials
		twitter.setOAuthConsumer(consumerKey, consumerSecret);

		// setup OAuth Access Token
		twitter.setOAuthAccessToken(new AccessToken(accessToken,
				accessTokenSecret));

		// Tell what to search
		Query query = new Query("amazed");

		// Maximum tweats
		int numberOfTweets = 20;

		// I am taking tweets and storing them in an arraylist (similar to
		// linked lists)
		// As twitter search api allows retreival of only 100 tweets at one time
		// so I need to repeat this process until 2000 tweets are not retreived

		// For this twitter api has 2 things:
		// 1.Id of a tweet (a number assigned to a tweet).
		// 2. Maximum Id...id upto ehich u are supposed to retreive in this
		// batch

		long lastID = Long.MAX_VALUE;

		// the arraylist which has tweets
		ArrayList<Status> tweets = new ArrayList<Status>();

		// This loop is required so that the retreival process can be continued
		// 20 times (as 2000/100=20(100 is the maximum tweets in one batch))

		while (tweets.size() < numberOfTweets) {
			if (numberOfTweets - tweets.size() > 100)
				query.setCount(100);
			else
				query.setCount(numberOfTweets - tweets.size());
			try {
				QueryResult result = twitter.search(query);
				tweets.addAll(result.getTweets());

				// u need to find the minimum id in the previous batch and set
				// the lastId equal to that so that in next batch you can
				// calculate the maximum id...
				// see line **A
				for (Status t : tweets)
					if (t.getId() < lastID)
						lastID = t.getId();
			}

			catch (TwitterException te) {
				System.out.println("Couldn't connect: " + te);
			}
			// ....**A
			query.setMaxId(lastID - 1);
		}

		// now we have the tweets, all we have to do is to process them in order
		// to make them useful
		for (int i = 0; i < tweets.size(); i++) {
			Status t = (Status) tweets.get(i);

			String text = t.getText();

			// 1. If the string start with RT then it is a re tweet and is
			// of no use as one copy is already present in our db.
			// 2. It may happen that the search tweet is present in
			// the user name and we don't need that

			if ((!t.getUser().getScreenName().contains(query.toString()))
					&& (!text.startsWith("RT"))) {

				// to generalise I am taking all tweets in small letters
				text = text.toLowerCase();

				// to remove all the URLS
				text = new DataCollector()
						.filter(text, "http[://a-zA-Z0-9./]+");

				// to remove all the @user thing...
				text = new DataCollector().filter(text, "@[a-zA-Z0-9]+");
				// to remove #tags...
				text = new DataCollector().filter(text, "#[a-zA-Z0-9]+");

				// trim the string i.e removing all the spaces from right
				text = text.trim();
				System.out.println(i + text);

				// Removing stop words (e.g. the, is, at, which, on).
				text = text
						.replace(
								"the|is|at|which|on|a|has|had|have|having|am|was|were|i'm",
								" ");
				// replacing emoticons with there meaning i.e replacing :) with
				// 'smile'...
			}
		}

	}

	public static void main(String args[]) {

		new DataRefiner().refineData();
	}

	@SuppressWarnings("finally")
	public String filter(String scan, String string, String string2) {
		StringBuffer sb = new StringBuffer();

		try {
			Pattern pt = Pattern.compile(string);
			Matcher m = pt.matcher(scan);

			while (m.find()) {
				m.appendReplacement(sb, string2);
			}

			m.appendTail(sb);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return sb.toString();
		}
	}
}
