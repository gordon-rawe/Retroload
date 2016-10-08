package rawe.gordon.com.retrodownload.download;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    enum Status{
        DOWNLOADING,
        PAUSED,
        CANCELD
    }

    public static final int WORK_THREAD_COUNT = 4;
    private String bookId;
    private ExecutorService executorService;
    private List<Future> jobFutures;
    private List<String> urls;
    private int COUNT;
    private AtomicInteger counter;

    public Worker(String bookId) {
        this.bookId = bookId;
        this.executorService = Executors.newFixedThreadPool(WORK_THREAD_COUNT);
        jobFutures = new ArrayList<>();
        urls = ImageUrlRetriever.getBookImagesList(this.bookId);
        COUNT = urls.size();
        counter = new AtomicInteger(0);
    }

    public void startDownload() {
        for (final String url : urls) {
            jobFutures.add(executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        DownloadUtil.downloadFile(url, MainActivity.FOLDER + "/" + UUID.randomUUID().toString() + ".jpg");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(Worker.class.getCanonicalName(), "url: " + url + "download error happened...");
                        EventBus.getDefault().post(new ProgressEvent(bookId, counter.get(), false, COUNT));
                    }
                    Log.d(Worker.class.getCanonicalName(), "url: " + url + "downloaded...");
                    EventBus.getDefault().post(new ProgressEvent(bookId, counter.getAndIncrement(), true, COUNT));
                }
            }));
        }
        executorService.shutdown();
    }

    public void pauseDownload() {

    }

    public void cancelDownload() {

    }

    public void resumeDownload() {

    }
}
