import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Q6 {
	public String processRequestq6(String id1, String id2) {
		Connection conn = null;
		String result = "";
		int result1 = 0;
		int result2 = 0;
		try {
			// System.out.println("m  " + id1 + " n " + id2);
			long t = System.currentTimeMillis();

			conn = Project619Phase1Q1.getConnection();
			// System.out.println("time for connection " +
			// (System.currentTimeMillis() - t));
			// PreparedStatement ps =
			// conn.prepareStatement("select count(id) from q6 where id>=? and id<=? ");
			// Entry<Integer, Integer> s =
			// cache.ceilingEntry(Integer.parseInt(id1));
			// result1 = s.getValue();
			// System.out.println("result 1 :" + result1);
			// Entry<Integer, Integer> s1 =
			// cache.floorEntry(Integer.parseInt(id2));
			// result2 = s1.getValue();
			// System.out.println("result 2 :" + result2);
			long ts = System.currentTimeMillis();
			PreparedStatement ps = conn
					.prepareStatement("select cnt from q6test where id=(select min(id) from q6test where id>=?);");
			ps.setString(1, id1);
			ResultSet rs = ps.executeQuery();
			PreparedStatement ps1 = conn
					.prepareStatement("select cnt from q6test where id=(select max(id) from q6test where id<=?);");

			ps1.setString(1, id2);
			ResultSet rs1 = ps1.executeQuery();

			// System.out.println("time to fetch count " +
			// (System.currentTimeMillis() - ts));
			if (rs.next()) {
				result1 = rs.getInt(1);
			}
			if (rs1.next()) {
				result2 = rs1.getInt(1);
			}
			result = (result2 - result1 + 1) + "";
			rs.close();
			ps.close();
			Project619Phase1Q1.releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (MyDAOException e) {
			e.printStackTrace();
		}
		// cache3.put(id, result);
		return "TeamCloudCrackers,117333310089,020697801606,984101504455\n" + result + "\n";
	}

	public String processRequestFromCache(String id1, String id2) {
		String result = "";
		long result1 = 0;
		long result2 = 0;

		// System.out.println("m  " + id1 + " n " + id2);
		long t = System.currentTimeMillis();

		System.out.println("time for connection " + (System.currentTimeMillis() - t));
		// PreparedStatement ps =
		// conn.prepareStatement("select count(id) from q6 where id>=? and id<=? ");
		Entry<Long, Long> s = cache.ceilingEntry(Long.parseLong(id1));
		result1 = s.getValue();
		System.out.println("result 1 :" + result1);
		Entry<Long, Long> s1 = cache.floorEntry(Long.parseLong(id2));
		result2 = s1.getValue();
		System.out.println("result 2 :" + result2);

		result = (result2 - result1 + 1) + "";

		return "TeamCloudCrackers,117333310089,020697801606,984101504455\n" + result + "\n";
	}

	static NavigableMap<Long, Long> cache = new ConcurrentSkipListMap<Long, Long>();

	public static void warmup() {
		long heapSize = Runtime.getRuntime().totalMemory();

		long heapMaxSize = Runtime.getRuntime().maxMemory();

		long heapFreeSize = Runtime.getRuntime().freeMemory();
		String output = "headSize :" + heapSize + "\n heapMaxSize " + heapMaxSize + "\nheapFreeSize " + heapFreeSize;
		System.out.println(output);
		System.out.println("inside warmup");
		Connection conn = null;
		String result = "";
		int result1 = 0;
		int result2 = 0;
		long x = 0;
		try {
			long t = System.currentTimeMillis();

			conn = Project619Phase1Q1.getConnection();

			System.out.println("time to execute full query" + (System.currentTimeMillis() - t));
			PreparedStatement ps = conn.prepareStatement("select * from q6test where id >= ? and id <=?");
			while (x <= 2640374638l) {
				// System.out.println("in");
				// long window = 2640374638l < x + 10000 ? (x+10000-2640374638l)
				// : 10000;

				ps.setLong(1, x);
				ps.setLong(2, x + 10000);
				ResultSet rs = ps.executeQuery();
				x = x + 10000 + 1;
				t = System.currentTimeMillis();
				while (rs.next()) {
					// System.out.println("inside loop" + rs.getInt(1) + "   " +
					// rs.getInt(2));

					cache.put(rs.getLong(1), rs.getLong(2));
				}
				rs.close();

			}
			System.out.println("time to load in cache" + (System.currentTimeMillis() - t));
		} catch (Exception e) {
			System.err.println("head size error value of x is " + x);
		}
	}
}
