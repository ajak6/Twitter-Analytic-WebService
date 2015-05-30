import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ETLReducerPhase2Part1 {
	public static void main(String[] args) throws IOException {
		 BufferedReader file = new BufferedReader(new
		 InputStreamReader(System.in));

		String line = "";
		String prevLine = "";
		String prevKey = "";
		String key = "";
		String prevValue = "";
		HashSet<String> tweetIds = new HashSet<String>();
		HashMap<String, MyValue> relativeMap = new HashMap<String, MyValue>();
	try{
		while ((line = file.readLine()) != null) {
			if (line.isEmpty()) {
				continue;
			}
			String keyValue[] = line.split("\t");
			key = keyValue[0];
			String value = keyValue[1];
			if (prevKey.equals(key)) {
				String valueArray[] = prevValue.split(" ");
				String tweetId = valueArray[2];
				if (!tweetIds.add(tweetId)) {
					// duplicate tweet
					prevValue=value;
					continue;
				}

				String relative = valueArray[0];
				char relation = valueArray[3].charAt(0);
				// ignore self retweet
				if (prevKey.equals(relative))
					continue;
				if (relativeMap.containsKey(relative)) {
					MyValue editValue = relativeMap.get(relative);
					if (editValue.relation == relation) {
						editValue.count += 1;
					} else {
						editValue.relation = '*';
						editValue.count += 1;
					}
				} else {
					MyValue newValue = new MyValue(1, relation, relative);
					relativeMap.put(relative, newValue);
				}
				prevKey = key;
				prevValue = value;
				prevLine = line;
			} else {
				if (!prevKey.isEmpty()) {

					String valueArray[] = prevValue.split(" ");
					String tweetId = valueArray[2];
					String relative = valueArray[0];

					char relation = valueArray[3].charAt(0);
					// not a duplicate required to check here edge case wont
					// process this tweet but print the previous series, also
					// ignore the self retweet
					if (tweetIds.add(tweetId) && !(prevKey.equals(relative))) {
						if (relativeMap.containsKey(relative)) {
							// System.out.println("processing " + prevKey);
							MyValue editValue = relativeMap.get(relative);
							if (editValue.relation == relation) {
								editValue.count += 1;
							} else {
								// System.out.println("converting to *");
								editValue.relation = '*';
								editValue.count += 1;
							}
						} else {
							MyValue myvalue = new MyValue(1, relation, relative);
							relativeMap.put(prevKey, myvalue);

						}
					}

					List<MyValue> ls = new ArrayList<MyValue>(relativeMap.values());
					Collections.sort(ls);
					StringBuilder st=new StringBuilder();
					for (MyValue myValue2 : ls) {
						st.append(myValue2.relation+","+myValue2.count+","+myValue2.userId+"\\n");
					}
					System.out.println(prevKey+"\t"+st.toString());
				}

				prevKey = key;
				prevValue = value;
				prevLine = line;
				relativeMap = new HashMap<String, MyValue>();
				tweetIds = new HashSet<String>();
			}

		}}catch(ArrayIndexOutOfBoundsException e){
			System.err.println("records is " +line);
			e.printStackTrace();
		}
		// handle last record
		if (prevKey.equals(key) && ((!prevKey.isEmpty())) && !(key.isEmpty())) {
			String keyValue[] = prevLine.split("\t");
			key = keyValue[0];
			String value = keyValue[1];
			String valueArray[] = value.split(" ");// use current line
			String tweetId = valueArray[2];
			if (tweetIds.add(tweetId)) {
				String relative = valueArray[0];
				char relation = valueArray[3].charAt(0);
				if (relativeMap.containsKey(relative)) {
					MyValue editValue = relativeMap.get(relative);
					if (editValue.relation == relation) {
						editValue.count += 1;
					} else {
						editValue.relation = '*';
						editValue.count += 1;
					}
				} else {
					MyValue newValue = new MyValue(1, relation, relative);
					relativeMap.put(relative, newValue);
				}
			}
			List<MyValue> ls = new ArrayList<MyValue>(relativeMap.values());
			Collections.sort(ls);
			StringBuilder st=new StringBuilder();
			for (MyValue myValue2 : ls) {
				st.append(myValue2.relation+","+myValue2.count+","+myValue2.userId+"\\n");
			}
			System.out.println(prevKey+"\t"+st.toString());

		} else if (!prevKey.isEmpty()) {
			String keyValue[] = prevLine.split("\t");
			key = keyValue[0];
			String value = keyValue[1];
			String valueArray[] = value.split(" ");// use current line
			String tweetId = valueArray[2];
			String relative = valueArray[0];
			char relation = valueArray[3].charAt(0);
			MyValue myValue = new MyValue(1, relation, relative);
			relativeMap.put(relative, myValue);
			List<MyValue> ls = new ArrayList<MyValue>(relativeMap.values());
			Collections.sort(ls);
			StringBuilder st=new StringBuilder();
			for (MyValue myValue2 : ls) {
				st.append(myValue2.relation+","+myValue2.count+","+myValue2.userId+"\\n");
			}
			System.out.println(prevKey+"\t"+st.toString());
		}
	}
}

class MyValue implements Comparable<MyValue> {
	int count;
	char relation;
	String userId;

	public MyValue(int count, char rel, String id) {
		this.count = count;
		relation = rel;
		userId = id;
	}

	@Override
	public String toString() {
		return count + " " + relation + " " + userId;
	}

	@Override
	public int compareTo(MyValue obj2) {
		char c = this.relation;
		char c2 = obj2.relation;
		if (c == '*' && c2 != '*')
			return -1;
		else if (c2 == '*' && c != '*')
			return 1;
		else if (c == '+' && c2 == '-')
			return -1;
		else if (c == '-' && c2 == '+')
		{	
			return +1;
		}else if (c2 == c) {
			// sort by count
			Integer count = this.count;
			Integer count2 = obj2.count;
			int k = count.compareTo(count2);
			if (k == 0) {
				// sort on user id
				Long userId = Long.parseLong(this.userId);
				Long userId2 = Long.parseLong(obj2.userId);
				int l = userId.compareTo(userId2);
				return l;//Increasing order of user ID
			} else
				return -k;//desc order of count
		}
		return 0;
	}
}
