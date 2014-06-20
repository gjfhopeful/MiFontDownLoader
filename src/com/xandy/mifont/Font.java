package com.xandy.mifont;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.JsonReader;
import android.util.Log;

public class Font {
	
	public static final String FontBaseURL = "http://m.zhuti.xiaomi.com/api/subject/index";
	public static final String INFO = "category=Font&start=mIndex&count=mCount&device=aries&apk=100";

	public static final String FrontCoverBase = "http://t1.market.xiaomi.com/thumbnail/jpeg/w286/";
	public static final String FontDownloadBaseURL = "http://zhuti.xiaomi.com/download/";
	public static final String FontDetailBaseURL = "http://m.zhuti.xiaomi.com/detail/";
	
	public static final String mIndex = "mIndex";
	public static final String mCount = "mCount";
	
	private static String FrontCover = "frontCover";
	private static String ModuleId = "moduleId";
	private static String Name = "name";
	private static String FileSize = "fileSize";
	
	private static boolean isEnd = false;
	public static boolean isEnd(){
		return isEnd;
	}
		
	private static List<Font> mFonts = new ArrayList<Font>();
	public static List<Font> getFonts(){
		return mFonts;
	}
	public static void resetFonts(){
		mFonts.clear();
	}
	
	
	private String mFrontCover = "";
	private String mModuleId = "";
	private String mName = "";
	private int mFileSize = 0;
	
	private String mDetailImg = "";
	
	public String getFontCover(){
		return FrontCoverBase + mFrontCover;
	}
	
	public String getFontDownLoadUrl(){
		return FontDownloadBaseURL + mModuleId;
	}
	
	public String getFontDetailURL(){
		return FontDetailBaseURL + mModuleId;
	}
	
	public String getFontDetailName(){
		return mName;
	}
	
	public int getFontSize(){
		return mFileSize;
	}
	
	
	
	public static void refreshFontData(String strUrl) { 
		Log.d("wang", strUrl);
		if(isEnd){
			return ;
		}
		// HttpGet对象 
		HttpGet httpRequest = new HttpGet(strUrl); 
		String strResult = ""; 
		try { 
			// HttpClient对象 
			HttpClient httpClient = new DefaultHttpClient(); 
			// 获得HttpResponse对象 
			HttpResponse httpResponse = httpClient.execute(httpRequest); 
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { 
				// 取得返回的数据 
				strResult = EntityUtils.toString(httpResponse.getEntity()); 
				List<Font> fonts = getFonts(strResult);
				if(fonts.size() == 0){
					isEnd = true;
				} else {
					for(Font font : fonts){
						Log.d("wang", font.toString());
						mFonts.add(font);
					}
					isEnd = false;
				}
			} 
		} catch (ClientProtocolException e) {  
			e.printStackTrace(); 
		} catch (JSONException  e) { 
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private static List<Font> getFonts(String jsonData) throws JSONException{
		List<Font> mFonts = new ArrayList<Font>();
		JSONTokener jsonParser = new JSONTokener(jsonData);  
		JSONObject fonts = (JSONObject) jsonParser.nextValue();
		if(null != fonts){
			JSONArray fontArray = fonts.getJSONArray("Font"); 
			for(int i = 0 ; i < fontArray.length() ; i++){
				String fontInfo = fontArray.getJSONObject(i).toString();
				Font font = new Font();
				font.updateFontInfo(fontInfo);
				mFonts.add(font);
			}
		}
		return mFonts;
	}
	
	private void updateFontInfo(JSONObject font){
		try {
			mFrontCover = font.getString(FrontCover);
			mFileSize = font.getInt(FileSize);
			mName = font.getString(Name);
			mModuleId = font.getString(ModuleId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean updateFontInfo(String info){
		StringReader sreader = new StringReader(info);
        try {
            JsonReader reader = new JsonReader(sreader);
            boolean ok = readJson(reader);
            if (!ok) {
                reader.close();
                return false;
            }
            reader.close();
        } catch (Exception e) {
            return false;
        }
        return true;
	}
	
	public boolean readJson(JsonReader sreader) throws IOException {
        sreader.beginObject();
        while (sreader.hasNext()) {
            String name = sreader.nextName();
            if(FrontCover.equals(name)){
            	mFrontCover = sreader.nextString();
            }
            else if(ModuleId.equals(name)){
            	mModuleId = sreader.nextString();
            }
            else if(Name.equals(name)){
            	mName = sreader.nextString();
            }
            else if(FileSize.equals(name)){
            	mFileSize = sreader.nextInt();
            }
            else {
            	sreader.skipValue();
            }
        }
        sreader.endObject();
        return true;
    }
	
	
	public String toString(){
		return " Name = " + mName + "  FileSize = " + mFileSize + 
				" FrontCover = " + mFrontCover + " ModuleId" + mModuleId ;
	}
	
	
	public static String getFontURL(int index ){
		int count = 10;
		if(0 == index){
			count = 20;
		}
		return getFontURL(index,count);
	}
	
	public static String getFontURL(int index ,int count){
		String mInfo = INFO.replace(mIndex, index+"" );
		mInfo = mInfo.replace(mCount, count+"");
		return FontBaseURL + "?" + mInfo;
	}
	
	public static String getFontURL(String index ,String count){
		String mInfo = INFO.replace(mIndex, index);
		mInfo = mInfo.replace(mCount, count);
		return FontBaseURL + "?" + mInfo;
	}
	
	public boolean refreshDetailImgUrl(){
		Document doc = null;
		try {
			doc = Jsoup.connect( getFontDetailURL() ).get();
			Elements mDivClass = doc.getElementsByClass("blockinfo");
			Element mImage = mDivClass.first();
			if(null != mImage) mDetailImg = mImage.getElementsByTag("img").attr("src");
//			Log.d("wang", "url = " + url);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	public String getFontDetailImgUrl(){
		return mDetailImg;
	}
	
}
