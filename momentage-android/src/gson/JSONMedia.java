package gson;

import com.google.gson.annotations.SerializedName;

public class JSONMedia {
	
	@SerializedName("id")
	public String id;
	
	@SerializedName("src")
	public String src;
	
	@SerializedName("mime_type")
	public String mime_type;
	
	@SerializedName("filesize")
	public String filesize;
	
	@SerializedName("post_date")
	public String post_date;
	
	
	@SerializedName("user_id")
	public String user_id;
	
}
