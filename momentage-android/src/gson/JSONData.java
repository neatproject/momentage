package gson;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class JSONData {
	
	@SerializedName("uid")
	public String uid;
	
	public List<JSONMedia> media;
	
}
