package com.xandy.mizi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class HttpTools {
	
	private static boolean DEBUG = true;
	private static final String TAG = "HttpTools";
	
	public static final String DownLoadDir = "MiFont";
	public static final String BaseURL = "http://zhuti.xiaomi.com";
	public static final String FontURLSort = "&sort=";
	public static int FontPageMAX = 13;
	public static int FontPageMIN = 1;
	
	private boolean isHot = true;
	
	public static String getFontURL(int page ,boolean isHot ){
		if(page > FontPageMAX || page < FontPageMIN) page = FontPageMIN ;
		return BaseURL + "/font?page=" + page + FontURLSort + (isHot ? "Hot" : "New");
	}
	
//	public static String getFontDownLoad(String font){
//		return BaseURL + font;
//	}
	
	public static String getFontDownLoad(Font font){
		return BaseURL + font.getFontURL().replace("detail", "download");
	}

	public static List<Font> getFonts(int page , boolean isHot){
		
		List<Font> mFont = new ArrayList<Font>();
		Document doc = null;
		try {
			String url = getFontURL(page, isHot);
			log(url);
			doc = Jsoup.connect(url).get();
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
				log(font.getFontDetailString());
				mFont.add(font);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mFont;
	}
	
	public static String getFontDetailImgUrl(Font font){
		String url = "";
		Document doc = null;
		try {
			log(font.getFontDetailString());
//			doc = Jsoup.connect("http://zhuti.xiaomi.com/detail/af7ac489-e9f9-46a5-b534-89f073c921c8").get();
			doc = Jsoup.connect(BaseURL + font.getFontURL() ).get();
			Element mMain = doc.getElementById("main");
			Element mElements = mMain.getElementsByAttributeValueStarting("id", "J_detail").first();
			if(null != mElements) url = mElements.getElementsByTag("img").attr("src");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	public static void log(String log){
		if(DEBUG) Log.d(TAG, log);
	}
}
