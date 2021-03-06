package rawe.gordon.com.retrodownload.download;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import rawe.gordon.com.retrodownload.MainActivity;
import rawe.gordon.com.retrodownload.deserialize.UrlExtractor;

/**
 * Created by gordon on 9/22/16.
 */
public class Worker {

    public static final String FOLDER = MainActivity.FOLDER;

    public static class Entry implements Serializable {
        public String savedName;
        public boolean isDownloaded;

        public Entry(String savedName, boolean isDownloaded) {
            this.isDownloaded = isDownloaded;
            this.savedName = savedName;
        }
    }

    public static final int WORK_THREAD_COUNT = Runtime.getRuntime().availableProcessors() + 1;
    public static final boolean usingInterruptedWay = true;
    public volatile boolean cancelPerformed = false;
    public volatile boolean exceptionHappened = false;
    private String bookId;
    private ExecutorService executorService;
    private List<Future> jobFutures;
    private int COUNT;
    private int downloadedCount;
    private AtomicInteger counter;
    private Map<String, Entry> checkList;

    public Worker(String bookId) {
        this.bookId = bookId;
    }

    private void prepareForDownload() {
        this.executorService = Executors.newFixedThreadPool(WORK_THREAD_COUNT);
        jobFutures = new ArrayList<>();
        counter = new AtomicInteger(0);
        /**首先判断是否有保存状态的攻略书映射清单*/
        File check = new File(getCheckListNameByBookId(bookId));
        if (check.exists()) {
            checkList = getCheckListByIdentifier(bookId);
            COUNT = checkList.size();
            for (final String url : checkList.keySet()) {
                if (checkList.get(url).isDownloaded) downloadedCount++;
            }
            performDownload();
        } else {
            downloadedCount = 0;
            checkList = new HashMap<>();
            try {
                UrlExtractor.downloadBook(bookId, new UrlExtractor.BookResult() {
                    @Override
                    public void onDownloaded(String bookContent) {
                        EventBus.getDefault().post(new ProgressEvent(bookId, downloadedCount, ProgressEvent.BOOK_DOWNLOAD_SUCCESS, COUNT));
                        persistBook(bookContent);
                        List<String> urls = ImageUrlRetriever.getInstance().parseBookImagesList(bookContent);
                        for (int i = 0; i < urls.size(); i++) {
                            String savedName = FOLDER + "/" + UUID.randomUUID().toString() + ".jpg";
                            checkList.put(urls.get(i), new Entry(savedName, false));
                        }
                        COUNT = checkList.size();
                        performDownload();
                    }

                    @Override
                    public void onFail() {
                        EventBus.getDefault().post(new ProgressEvent(bookId, downloadedCount, ProgressEvent.BOOK_DOWNLOAD_FAIL, COUNT));
                        Retroload.getInstance().finishDownload(bookId);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new ProgressEvent(bookId, downloadedCount, ProgressEvent.BOOK_DOWNLOAD_FAIL, COUNT));
                Retroload.getInstance().finishDownload(bookId);
            }
        }
    }

    private void performDownload() {
        if (downloadedCount == COUNT) {
            EventBus.getDefault().post(new ProgressEvent(bookId, downloadedCount, ProgressEvent.ALL_DOWNLOADED, COUNT));
            Retroload.getInstance().finishDownload(bookId);
            return;
        }
        for (final String url : checkList.keySet()) {
            final Entry entry = checkList.get(url);
            if (entry.isDownloaded) continue;
            jobFutures.add(executorService.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        DownloadUtil.downloadFile(url, checkList.get(url).savedName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(Worker.class.getCanonicalName(), "url: " + url + "downloadBook error happened...");
                        cancelFutures(usingInterruptedWay);
                        Retroload.getInstance().finishDownload(bookId);
                        persistCheckList();
                        if (!cancelPerformed && !exceptionHappened) {
                            EventBus.getDefault().post(new ProgressEvent(bookId, downloadedCount + counter.get(), ProgressEvent.PAUSED, COUNT));
                            exceptionHappened = true;
                        }
                        return;
                    }
                    synchronized (this) {
                        checkList.get(url).isDownloaded = true;
                    }
                    Log.d(Worker.class.getCanonicalName(), "url: " + url + "downloaded...");
                    EventBus.getDefault().post(new ProgressEvent(bookId, downloadedCount + counter.incrementAndGet(), COUNT));
                    if (counter.get() + downloadedCount == COUNT) {
                        Retroload.getInstance().finishDownload(bookId);
                        persistCheckList();
                        EventBus.getDefault().post(new ProgressEvent(bookId, downloadedCount + counter.get(), ProgressEvent.ALL_DOWNLOADED, COUNT));
                        ImageUrlRetriever.getInstance().refreshCheckListEntries();
                    }
                }
            }));
        }
        executorService.shutdown();
    }

    public void startDownload() {
        prepareForDownload();
    }

    public void pauseDownload() {
        //停止线程池，保存下载的对照表
        cancelFutures(usingInterruptedWay);
        persistCheckList();
        Retroload.getInstance().finishDownload(bookId);
    }

    private void cancelFutures(boolean interrupted) {
        for (Future job : jobFutures) {
            if (!job.isDone()) {
                job.cancel(interrupted);
            }
        }
    }

    public void cancelDownload() {
        //根据对照表删除文件，同时删除对照表
        //停止线程池，保存下载的对照表
        cancelPerformed = true;
        cancelFutures(usingInterruptedWay);
        deleteCheckList();
        deleteBook();
        Retroload.getInstance().finishDownload(bookId);
        EventBus.getDefault().post(new ProgressEvent(bookId, counter.get() + downloadedCount, ProgressEvent.CANCEL, COUNT));
    }

    public void resumeDownload() {
        //根据对照表重新生成任务，重新回调进度
        prepareForDownload();
    }

    public void persistBook(String content) {
        PersistUtil.saveTextFile(content, getBookNameByBookId(bookId));
    }

    public void persistCheckList() {
        String content = JSON.toJSONString(checkList);
        System.out.println(content);
        PersistUtil.saveTextFile(content, getCheckListNameByBookId(bookId));
    }

    public ProgressEvent getWorkerStatus() {
        return new ProgressEvent(bookId, downloadedCount + counter.get(), ProgressEvent.NORMAL, COUNT);
    }

    /**
     * internal operations
     */
    public void deleteCheckList() {
        new File(getCheckListNameByBookId(bookId)).delete();
    }

    public void deleteBook() {
        new File(getBookNameByBookId(bookId)).delete();
    }

    public static Map<String, Entry> getCheckListByIdentifier(String book_id) {
        Map<String, Entry> retValue = new HashMap<>();
        String content = PersistUtil.readFileAsText(getCheckListNameByBookId(book_id));
        JSONObject jsonObject = JSON.parseObject(content);
        if (jsonObject == null) return retValue;
        for (String key : jsonObject.keySet()) {
            JSONObject tmpValue = (JSONObject) jsonObject.get(key);
            retValue.put(key, new Entry((String) tmpValue.get("savedName"), (Boolean) tmpValue.get("isDownloaded")));
        }
        return retValue;
    }

    public static Map<String, Entry> getCheckListByFileName(String fileName) {
        Map<String, Entry> retValue = new HashMap<>();
        String content = PersistUtil.readFileAsText(FOLDER + "/" + fileName);
        JSONObject jsonObject = JSON.parseObject(content);
        if (jsonObject == null) return retValue;
        for (String key : jsonObject.keySet()) {
            JSONObject tmpValue = (JSONObject) jsonObject.get(key);
            retValue.put(key, new Entry((String) tmpValue.get("savedName"), (Boolean) tmpValue.get("isDownloaded")));
        }
        return retValue;
    }

    public static String getCheckListNameByBookId(String bookId) {
        return FOLDER + "/check_list_" + bookId.hashCode() + ".txt";
    }

    public static String getBookNameByBookId(String bookId) {
        return FOLDER + "/book_" + bookId.hashCode() + ".txt";
    }
}
