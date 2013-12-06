package gson;

import com.google.gson.annotations.SerializedName;

public class JSONRoot {		

		@SerializedName("success")
		public boolean success;		
		
		public JSONData data;	

	}