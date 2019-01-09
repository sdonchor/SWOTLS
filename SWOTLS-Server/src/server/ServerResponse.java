package server;

import java.io.Serializable;

public class ServerResponse implements Serializable{
	private static final long serialVersionUID = 1865829841529993145L;
	private String responseType="";
	private boolean boolTypeResponse=false;;
	private String stringTypeResponse=null;
	private int intTypeResponse=-999;
	public boolean getBoolTypeResponse() {
		return boolTypeResponse;
	}
	public void setBoolTypeResponse(boolean boolTypeResponse) {
		this.boolTypeResponse = boolTypeResponse;
	}
	public String getStringTypeResponse() {
		return stringTypeResponse;
	}
	public void setStringTypeResponse(String stringTypeResponse) {
		this.stringTypeResponse = stringTypeResponse;
	}
	public int getIntTypeResponse() {
		return intTypeResponse;
	}
	public void setIntTypeResponse(int intTypeResponse) {
		this.intTypeResponse = intTypeResponse;
	}
	public String getResponseType() {
		return responseType;
	}
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

}
