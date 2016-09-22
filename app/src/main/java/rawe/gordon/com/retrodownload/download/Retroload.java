package rawe.gordon.com.retrodownload.download;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gordon on 9/22/16.
 */
public class Retroload {

    private Retroload() {
        workGroups = new HashMap<>();
    }

    public static class Holder {
        public static Retroload instance = new Retroload();
    }

    public static Retroload getInstance() {
        return Holder.instance;
    }

    private Map<String, WorkGroup> workGroups;

    public void startDownload(String bookId, WorkGroup.ProgressListener progressListener) throws Exception {
//        if (isDownloadJobExisted(bookId)) throw new Exception("");
        WorkGroup workGroup;
        workGroups.put(bookId, workGroup = new WorkGroup(bookId, progressListener));
        workGroup.startDownload();
    }

    public void pauseDownload(String bookId) {

    }

    public void resumeDownload(String bookId, WorkGroup.ProgressListener progressListener) {

    }

    public void cancelDownload(String bookId) {

    }

    public boolean isDownloadJobExisted(String bookId) {
        return workGroups.containsKey(bookId);
    }
}
