import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ETLMapperPhase2Part1 {
	public static void main(String[] args) throws IOException {

		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));

		String line = "";
		while ((line = file.readLine()) != null) {
			if (line.isEmpty()) {
				continue;
			}
			JSONObject object = getParsedObject(line);
			JSONObject retweeted_object = (JSONObject) object.get("retweeted_status");
			if (retweeted_object == null)
				continue;
			else {

				String tweetId = (String) object.get("id_str");
				String originalTweetId = (String) retweeted_object.get("id_str");

				// from these object find the userid
				JSONObject retweeter = (JSONObject) object.get("user");
				JSONObject tweeter = (JSONObject) retweeted_object.get("user");
				String retweeterId = (String) retweeter.get("id_str");
				String tweeterId = (String) tweeter.get("id_str");
				if (retweeterId.equals(tweeterId)){
					continue;}
				System.out.println(tweeterId + "\t" + retweeterId + " " + 1 + " " + tweetId + " " + "+");
				System.out.println(retweeterId + "\t" + tweeterId + " " + 1 + " " + tweetId + " " + "-");

			}

		}
	}

	private static JSONObject getParsedObject(String line) {
		JSONParser parser = new JSONParser();
		JSONObject object = null;
		try {
			object = (JSONObject) parser.parse(line);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return object;
	}
}
