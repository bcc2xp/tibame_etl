package appledaily;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class saveapple {
	public static void main(String[] args) throws Exception {
		
		Connection con = null;
		PreparedStatement pst = null;
		String insertdbSQL = "insert into news_main(title, content, view_cnt, category) "
				+ "values(?,?,?,?)";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/appledaily", "root", "test");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		pst = (PreparedStatement) con.prepareStatement(insertdbSQL);
		
		Document doc;
		doc = Jsoup.connect("http://www.appledaily.com.tw/realtimenews/section/new/").get();
		String domain = "http://www.appledaily.com.tw";
		String html = doc.html();

		Elements rtddt = doc.select(".rtddt");

		for (Element li : rtddt) {
			String time = li.select("time").text();
			String author = li.select("h2").text();
			String title = li.select("h1").text();
			String link = li.select("a").attr("href");
			readarticle(domain + link, pst);
			//System.out.println(time + " " + author + " " + title + " " + domain + link);
		}
		con.close();
	}
	
	public static void readarticle(String article_url, PreparedStatement pst) throws Exception{
		
		Document doc;
		doc = Jsoup.connect(article_url).get();		
		
		// 2015�~09��20��12:37
		String title = doc.select("#h1").text() ;
		String content = doc.select("#summary").text();
		String time = doc.select(".gggs time").text() ;
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy�~MM��dd��hh:mm");
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
		Date dt = formatter.parse(time);

		String popularity = doc.select(".clicked").text();
		String category = doc.select("realtimelist h1").text() ;
		//realtimelist h1
		
		String patternStr = "(.+)\\((\\d+)\\)";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(popularity);
		boolean matchFound = matcher.find();
		if (matchFound) {
			Integer view_cnt = Integer.parseInt(matcher.group(2));
			
			pst.setString(1, title);
			pst.setString(2, content);
			//pst.setTime(3, (Time) dt);
			pst.setInt(3, view_cnt);
			pst.setString(4, category);
			pst.execute();
			System.out.println(String.format("%s %s %s %d %s"
					,title, content, dt 
					,view_cnt, category));
		}
		
	}
}