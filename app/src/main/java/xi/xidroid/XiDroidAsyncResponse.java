package xi.xidroid;

/**
 * Created by basweerman on 11/24/15.
 */
public interface XiDroidAsyncResponse {

    void processFinish(String output, int responseCode, String responseString, XiDroidHTTPDataObject dataObject, XiDroidHTTPReturnObject returnObject);

}
