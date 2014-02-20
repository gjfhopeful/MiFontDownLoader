package com.xandy.mizi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class HttpTools {
	
	public static final String BaseURL = "http://zhuti.xiaomi.com";
	public static final String FontURLSort = "&sort=";
	public static int FontPageMAX = 13;
	public static int FontPageMIN = 1;
	
	private boolean isHot = true;
	
	public static String getFontURL(int page ,boolean isHot ){
		if(page > FontPageMAX || page < FontPageMIN) page = FontPageMIN ;
		return BaseURL + "/font?page=" + page + FontURLSort + (isHot ? "Hot" : "New");
	}
	
	public static String getFontDownLoad(String font){
		return BaseURL + font;
	}

	public static List<Font> getFonts(int page , boolean isHot){
		
		List<Font> mFont = new ArrayList<Font>();
		Document doc = null;
		try {
			doc = Jsoup.connect(getFontURL(page, isHot)).get();
			Element mMain = doc.getElementById("main");
			Elements mElements = mMain.getElementsByTag("li");
			int count = mElements.size();
			for(int i = 0 ; i < count ; i++ ){
				Element temp = mElements.get(i).getElementsByClass("thumb").first();
				Element Img = temp.getElementsByTag("img").first();
				Element Url = temp.getElementsByTag("a").first();
				Font font = new Font();
				font.setFontName(Img.attr("alt"));
				font.setFontImgURL(Img.attr("data-src"));
				font.setFontURL(Url.attr("href"));
				mFont.add(font);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mFont;
	}
}
