package rawe.gordon.com.retrodownload.download;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gordon on 10/8/16.
 */
public class ImageUrlRetriever {
    public static List<String> getBookImagesList(String book_id) {
        List<String> retValue = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            retValue.add("http://i.imgur.com/wyCOFYM.jpg");
        }
        return retValue;
    }
}
