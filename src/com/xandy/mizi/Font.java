package com.xandy.mizi;

public class Font {
	
	private String FontURL;
	private String FontName;
	private String FontImgURL;
	
	public String getFontDetail(){
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
	
}
