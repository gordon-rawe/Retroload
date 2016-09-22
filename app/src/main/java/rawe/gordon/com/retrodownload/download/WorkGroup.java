package rawe.gordon.com.retrodownload.download;

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
public class WorkGroup {

    public static final int WORK_THREAD_COUNT = 4;

    public WorkGroup(String bookId, ProgressListener progressListener) {
        this.bookId = bookId;
        this.executorService = Executors.newFixedThreadPool(WORK_THREAD_COUNT);
        this.progressListener = progressListener;
        jobFutures = new ArrayList<>();
        urls = getBookImagesList(this.bookId);
        COUNT = urls.size();
        counter = new AtomicInteger(0);
    }

    private String bookId;
    private ExecutorService executorService;
    private ProgressListener progressListener;
    private List<Future> jobFutures;
    private List<String> urls;
    private int COUNT;
    private AtomicInteger counter;


    public interface ProgressListener {
        void callbackPercent(int finished, int total);
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
                        System.out.println("error");
                    }
                    System.out.println(url);
                    progressListener.callbackPercent(counter.incrementAndGet(), COUNT);
                }
            }));
        }
        executorService.shutdown();
    }


    public static List<String> getBookImagesList(String book_id) {
        List<String> retValue = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            retValue.add("http://i.imgur.com/wyCOFYM.jpg");
        }
        return retValue;
    }
}
