package com.luwei.init;

/**
 * 所以涉及URL都在这里
 * 
 * @author LuWei
 * 
 */
public class URLs {
	private static String BASCURL = "http://www.jorahlu.com:8080/SmartServer";

	private static String LOCALURL = "http://192.168.56.1:8080/SmartServer";
	
	public static String URL_JSONServer = BASCURL + "/JsonServer";
	public static String URL_ContactJSON = BASCURL + "/ContactJSON";
	public static String URL_WorldList = BASCURL + "/WorldListJSON";
	public static String URL_Login = BASCURL + "/LoginServlet";
	public static String URL_MainImage = BASCURL + "/ImageServlet";
	public static String URL_ImageWall = BASCURL + "/ImageWallServlet";
	public static String URL_Fittings = BASCURL + "/FittingServlet";
}
