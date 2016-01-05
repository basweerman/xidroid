package xi.xidroid;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by basweerman on 11/24/15.
 */
public class XiDroidHTTPDataObject {

    static public int N_GET = 1;
    static public int N_POST = 2;
    static public int N_POST_FILE = 3;
    static public int N_GET_READ = 4;
    static public int N_CHECK_SERVER = 5;

    static public String N_lineEnd = "\r\n";
    static public String N_twoHyphens = "--";
    static public String N_boundary = "*****";

    private ByteArrayOutputStream bos = null;

    private String postData = "";
    private int type;
    public boolean sent = false; //tag to indicate whether this one has been sent already!

    String POST_fileName;


    private Map<String, String> map = new HashMap<String, String>();

    public int getType(){
        return this.type;
    }

    public XiDroidHTTPDataObject(int type){
        this.type = type;
    }

    public void addGetParameter(String key, int value){
        map.put(key, Integer.toString(value));
    }

    public void addGetParameter(String key, String value){
        map.put(key, value);
    }

    public String getGetParameter(String key){
        return map.get(key);
    }

    public void setPostData(String data){
        postData = data;
    }

    public String getPostData(){
        return postData;
    }

    public void addFileName(String filename){
        POST_fileName = filename;
        setByteArrayOutputStream();
    }

    public void setByteArrayOutputStream(){
        try {
            FileInputStream fis = new FileInputStream(POST_fileName);
            bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum); //no doubt here is 0
            }
        }
        catch (Exception e){

        }
    }



    public ByteArrayOutputStream getByteArrayOutputStream(){
        try {
            if (bos == null){ //null -> set it
                FileInputStream fis = new FileInputStream(POST_fileName);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                for (int readNum; (readNum = fis.read(buf)) != -1;) {
                    bos.write(buf, 0, readNum); //no doubt here is 0
                }
            }
            return bos;
        }
        catch (Exception e){
            return null;
        }
    }

    public String getGetString(){
        try {
            return new JSONObject(map).toString();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }


}