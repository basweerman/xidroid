package xi.xidroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by basweerman on 11/25/15.
 */
public class XiDroidHTTPReturnObject {

    String rawServerString;
    Map<String, String> parameterMap = new HashMap<String, String>();

    public XiDroidHTTPReturnObject(String input){
        rawServerString = input;
        if (rawServerString != null) {
            if (isJSONValid(rawServerString)) {
                try {
                    JSONObject textsJson = new JSONObject(input.trim());
                    Iterator<?> keys = textsJson.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        parameterMap.put(key, (String) textsJson.getString(key));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

            }
        }
    }

    public String getText(String key) {
      return getText(key, null);
    }

    public String getText(String key, String returnDefault){
        //loadDefault();
        if (parameterMap.size() == 0){
            return returnDefault;
        }
        if (parameterMap.containsKey(key)){
            return parameterMap.get(key);
        }
        else {//get default!
            return returnDefault;
        }
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

}
