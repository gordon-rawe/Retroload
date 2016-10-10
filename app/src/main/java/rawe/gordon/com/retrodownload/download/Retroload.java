package rawe.gordon.com.retrodownload.download;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * Created by gordon on 9/22/16.
 */
public class Retroload {

    private Retroload() {
        workGroups = new HashMap<>();
        httpClient = new OkHttpClient();
    }

    public static class Holder {
        public static Retroload instance = new Retroload();
    }

    public static Retroload getInstance() {
        return Holder.instance;
    }

    private Map<String, Worker> workGroups;
    private OkHttpClient httpClient;

    public OkHttpClient getClient() {
        return httpClient;
    }

    public void startDownload(String bookId) throws Exception {
        if (isDownloadJobExisted(bookId)) throw new Exception("download job already in progress");
        Worker worker;
        workGroups.put(bookId, worker = new Worker(bookId));
        worker.startDownload();
    }

    public void pauseDownload(String bookId) {
        workGroups.get(bookId).pauseDownload();
    }

    public void resumeDownload(String bookId) {
        workGroups.get(bookId).resumeDownload();
    }

    public void cancelDownload(String bookId) {
        workGroups.get(bookId).cancelDownload();
    }

    public boolean isDownloadJobExisted(String bookId) {
        return workGroups.containsKey(bookId);
    }

    public void finishDownload(String bookId) {
        workGroups.remove(bookId);
    }
}
