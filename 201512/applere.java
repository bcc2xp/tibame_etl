package appledaily;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class applere {
	public static void main(String[] args) throws IOException {
		Document doc;
		doc = Jsoup.connect("http://www.appledaily.com.tw/realtimenews/section/new/").get();
		String patternStr = "(.+)\\((\\d+)\\)";
		Pattern pattern = Pattern.compile(patternStr);

		Elements rtddt = doc.select(".rtddt a");
		for (Element li : rtddt) {

			String title = li.select("h1").text();
			Matcher matcher = pattern.matcher(title);
			boolean matchFound = matcher.find();
			if (matchFound) {
				System.out.println(matcher.group(1) + "" + Integer.parseInt(matcher.group(2)));
			}
		}
	}
}
