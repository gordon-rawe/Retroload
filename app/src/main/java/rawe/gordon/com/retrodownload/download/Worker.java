package rawe.gordon.com.retrodownload.download;

import android.util.Log;

import com.alibaba.fastjson.JSON;

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

/**
 * Created by gordon on 9/22/16.
 */
public class Worker {
    enum Status {
        DOWNLOADING,
        PAUSED,
        CANCELD
    }

    public static class Entry implements Serializable {
        public String savedName;
        public boolean isDownloaded;

        public Entry(String savedName, boolean isDownloaded) {
            this.isDownloaded = isDownloaded;
            this.savedName = savedName;
        }
    }

    public static final int WORK_THREAD_COUNT = 4;
    private String bookId;
    private ExecutorService executorService;
    private List<Future> jobFutures;
    private List<String> urls;
    private int COUNT;
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
            checkList = getCheckList(bookId);
            COUNT = checkList.size();
        } else {
            checkList = new HashMap<>();
            urls = ImageUrlRetriever.getBookImagesList(this.bookId);
            for (int i = 0; i < urls.size(); i++) {
                String savedName = MainActivity.FOLDER + "/" + UUID.randomUUID().toString() + ".jpg";
                checkList.put(urls.get(i), new Entry(savedName, false));
            }
            COUNT = checkList.size();
        }
    }

    private void performDownload() {
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
                        Log.d(Worker.class.getCanonicalName(), "url: " + url + "download error happened...");
                        cancelDownload();
                        EventBus.getDefault().post(new ProgressEvent(bookId, counter.get(), false, COUNT));
                    }
                    synchronized (this) {
                        checkList.get(url).isDownloaded = true;
                    }
                    Log.d(Worker.class.getCanonicalName(), "url: " + url + "downloaded...");
                    EventBus.getDefault().post(new ProgressEvent(bookId, counter.incrementAndGet(), true, COUNT));
                    if (counter.get() == COUNT) {
                        Retroload.getInstance().finishDownload(bookId);
                        persistCheckList();
                    }
                }
            }));
        }
        executorService.shutdown();
    }

    public void startDownload() {
        prepareForDownload();
        performDownload();
    }

    public void pauseDownload() {
        //停止线程池，保存下载的对照表
        for (Future job : jobFutures) {
            if (!job.isDone()) {
                job.cancel(true);
            }
        }
        persistCheckList();
    }

    public void cancelDownload() {
        //根据对照表删除文件，同时删除对照表
        //停止线程池，保存下载的对照表
        for (Future job : jobFutures) {
            if (!job.isDone()) {
                job.cancel(true);
            }
        }
        deleteCheckList();
    }

    public void resumeDownload() {
        //根据对照表重新生成任务，重新回调进度
        prepareForDownload();
        performDownload();
    }

    public void persistCheckList() {
        String content = JSON.toJSONString(checkList);
        System.out.println(content);
        PersistUtil.saveTextFile(content, getCheckListNameByBookId(bookId));
    }


    /**
     * internal operations
     */
    public void deleteCheckList() {
        new File(getCheckListNameByBookId(bookId)).deleteOnExit();
    }

    public static Map<String, Entry> getCheckList(String book_id) {
        String content = PersistUtil.readFileAsText(getCheckListNameByBookId(book_id));
        return (Map<String, Entry>) JSON.parse(content);
    }

    private static String getCheckListNameByBookId(String bookId) {
        return MainActivity.FOLDER + "/check_list_" + bookId + ".txt";
    }
}
