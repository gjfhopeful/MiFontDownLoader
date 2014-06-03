package com.xandy.mifont;

import java.io.IOException;
import java.io.StringReader;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;

import android.util.JsonReader;

public class Font {
	
	public static final String FontBaseURL = "http://m.zhuti.xiaomi.com/api/subject/index";
	public static final String FontDetailBseURL = "http://m.zhuti.xiaomi.com/detail/";
	public static final String INFO = "category=Font&start=mIndex&count=mCount&device=aries&apk=100";
	
	public static final String mIndex = "sIndex";
	public static final String mCount = "mCount";
	
	
	public static final String FrontCoverBase = "http://t1.market.mi-img.com/thumbnail/jpeg/w286/";
	
	private static String FrontCover = "frontCover";
	private static String ModuleId = "moduleId";
	private static String Name = "name";
	private static String FileSize = "fileSize";
	
	
	private String mFrontCover = "";
	private String mModuleId = "";
	private String mName = "";
	private int mFileSize = 0;
	
	public String getFontCover(){
		return FrontCoverBase + mFrontCover;
	}
	
	public String getFontDetailURL(){
		return FontDetailBseURL + mModuleId;
	}
	
	public String getFontDetailName(){
		return mName;
	}
	
	public int getFontSize(){
		return mFileSize;
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
	
	
	
	
	public String getFontURL(int index ){
		return getFontURL(index,6);
	}
	
	public String getFontURL(int index ,int count){
		String mInfo = INFO.replace(mIndex, index+"" );
		mInfo = mInfo.replace(mCount, count+"");
		return FontBaseURL + "?" + mInfo;
	}
	
	public String getFontURL(String index ,String count){
		String mInfo = INFO.replace(mIndex, index);
		mInfo = mInfo.replace(mCount, count);
		return FontBaseURL + "?" + mInfo;
	}
	
	private String FontURL = "";
	private String FontName = "";
	private String FontImgURL = "";
	private boolean isDownLoading = false;
	
	public String getFontDetailString(){
		return "name:" + FontName + " | img:"  + FontImgURL + " | download:" + FontURL;
	}
	
	public void fixFontURL(){
		FontURL = FontURL.replaceAll("detail", "download");
	}

	public String getFontURL() {
		return FontURL;
	}

	public void setFontURL(String fontURL) {
		FontURL = fontURL;
	}

	public String getFontName() {
		return FontName;
	}

	public void setFontName(String fontName) {
		FontName = fontName;
	}

	public String getFontImgURL() {
		return FontImgURL;
	}

	public void setFontImgURL(String fontImgURL) {
		FontImgURL = fontImgURL;
	}

	public boolean isDownLoading() {
		return isDownLoading;
	}

	public void setDownLoading(boolean isDownLoading) {
		this.isDownLoading = isDownLoading;
	}
	
}
