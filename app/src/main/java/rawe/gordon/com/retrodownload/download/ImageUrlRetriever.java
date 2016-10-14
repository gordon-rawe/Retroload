package rawe.gordon.com.retrodownload.download;

import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rawe.gordon.com.retrodownload.MainActivity;
import rawe.gordon.com.retrodownload.deserialize.UrlExtractor;

/**
 * Created by gordon on 10/8/16.
 */
public class ImageUrlRetriever {

    public static final String TAG = ImageUrlRetriever.class.getCanonicalName();

    private ImageUrlRetriever() {
        refreshCheckListEntries();
    }

    public static ImageUrlRetriever getInstance() {
        return Holder.instance;
    }

    public static class Holder {
        public static ImageUrlRetriever instance = new ImageUrlRetriever();
    }

    public void refreshCheckListEntries() {
        File directory = new File(MainActivity.FOLDER);
        String[] checkList = null;
        if (directory.isDirectory()) {
            checkList = directory.list(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    return s.startsWith("check_list_");
                }
            });
        }
        if (checkList != null)
            for (String aCheckList : checkList) {
                Map<String, Worker.Entry> asd = Worker.getCheckListByFileName(aCheckList);
                for (Map.Entry<String, Worker.Entry> entry : asd.entrySet()) {
                    if (entry.getValue().isDownloaded)
                        maps.get().put(entry.getKey(), entry.getValue());
                }
            }
    }


    WeakReference<Map<String, Worker.Entry>> maps = new WeakReference<Map<String, Worker.Entry>>(new HashMap<String, Worker.Entry>());

    public List<String> parseBookImagesList(final String content) {
        return UrlExtractor.extractUrls(content);
    }

    public String dump() {
        String retValue = "";
        if (maps.get() != null) {
            for (Map.Entry<String, Worker.Entry> entry : maps.get().entrySet()) {
                retValue += entry.getKey() + " -> " + entry.getValue().savedName + " -> " + entry.getValue().isDownloaded + '\n';
            }
        }
        return retValue;
    }

    public void dumpToLog() {
        System.out.println(dump());
    }

    public String retrieveLocalUrl(String original) throws Exception {
        if (maps.get() != null) return maps.get().get(original).savedName;
        throw new Exception("image not found");
    }
}
