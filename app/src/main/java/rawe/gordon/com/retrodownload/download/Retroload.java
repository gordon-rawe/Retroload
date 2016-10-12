package rawe.gordon.com.retrodownload.download;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * Created by gordon on 9/22/16.
 * <p/>
 * Retroload是下载攻略书的下载器，主要通过{@link Retroload#startDownload},{@link Retroload#pauseDownload},
 * {@link Retroload#resumeDownload},{@link Retroload#cancelDownload}几个api并结合{@Param bookId}来控制，
 * 提供了{@link Retroload#getDownloadedBook}来获取下载好的攻略书文本，{@link Retroload#traceBookStatus}
 * 来获取下载好的攻略书的下载状态。
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

    /**
     * 开启下载
     */
    public void startDownload(String bookId) throws Exception {
        if (isDownloadJobExisted(bookId))
            throw new Exception("downloading worker are working, wrong state...");
        Worker worker;
        workGroups.put(bookId, worker = new Worker(bookId));
        worker.startDownload();
    }

    /**
     * 暂停下载
     */
    public void pauseDownload(String bookId) {
        Worker worker = workGroups.get(bookId);
        if (worker != null) worker.pauseDownload();
    }

    /**
     * 重新恢复某本书下载
     */
    public void resumeDownload(String bookId) {
        Worker worker;
        workGroups.put(bookId, worker = new Worker(bookId));
        worker.startDownload();
    }

    /**
     * 取消某本书的下载
     */
    public void cancelDownload(String bookId) {
        Worker worker = workGroups.get(bookId);
        if (worker != null) worker.cancelDownload();
    }

    public void finishDownload(String bookId) {
        workGroups.remove(bookId);
    }

    /**
     * 获取某本书的地址
     */
    public File getDownloadedBook(String bookId) {
        File file = new File(Worker.getBookNameByBookId(bookId));
        if (file.exists()) {
            return file;
        }
        return null;
    }


    /**
     * 获取某本书的下载状态和进度
     */
    public BookStatus traceBookStatus(String bookId) {
        if (workGroups.get(bookId) != null) {
            Worker worker = workGroups.get(bookId);
            ProgressEvent progressEvent = worker.getWorkerStatus();
            return new BookStatus(progressEvent.getCurrent(), progressEvent.getTotal(), BookStatus.DOWNLOADING);
        }
        if (!new File(Worker.getCheckListNameByBookId(bookId)).exists())
            return new BookStatus(0, 0, BookStatus.NOT_DOWNLOADED);
        Map<String, Worker.Entry> entries = Worker.getCheckList(bookId);
        int count = entries.size();
        int downloadedCount = 0;
        for (String key : entries.keySet()) {
            if (entries.get(key).isDownloaded) downloadedCount++;
        }
        return count == downloadedCount ? new BookStatus(downloadedCount, count, BookStatus.DOWNLOADED) : new BookStatus(downloadedCount, count, BookStatus.PAUSED_WITH_PERCENT);
    }

    /**
     * 内部使用，判断某个Worker是否已经存在
     */
    private boolean isDownloadJobExisted(String bookId) {
        return workGroups.containsKey(bookId);
    }

    public static class BookStatus {
        /**
         * 没下载
         */
        public static final int NOT_DOWNLOADED = 0;
        /**
         * 已下载
         */
        public static final int DOWNLOADED = 1;
        /**
         * 下载中
         */
        public static final int DOWNLOADING = 2;
        /**
         * 下载暂停
         */
        public static final int PAUSED_WITH_PERCENT = 3;

        public int status;
        public int current;
        public int total;

        public BookStatus(int current, int total, int status) {
            this.current = current;
            this.status = status;
            this.total = total;
        }

        @Override
        public String toString() {
            return String.valueOf(current) + total + status;
        }
    }
}
