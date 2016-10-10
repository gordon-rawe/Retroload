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
        Worker worker;
        workGroups.put(bookId, worker = new Worker(bookId));
        worker.startDownload();
    }

    public void pauseDownload(String bookId) {
        Worker worker = workGroups.get(bookId);
        if(worker!=null) worker.pauseDownload();
    }

    public void resumeDownload(String bookId) {
        Worker worker = workGroups.get(bookId);
        if(worker!=null) worker.pauseDownload();
    }

    public void cancelDownload(String bookId) {
        Worker worker = workGroups.get(bookId);
        if(worker!=null) worker.pauseDownload();
    }

    public boolean isDownloadJobExisted(String bookId) {
        return workGroups.containsKey(bookId);
    }

    public void finishDownload(String bookId) {
        workGroups.remove(bookId);
    }
}
