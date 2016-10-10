package rawe.gordon.com.retrodownload.download;

/**
 * Created by gordon on 10/8/16.
 */
public class ProgressEvent {

    public static final int NORMAL = 0;
    public static final int EXCEPTION = 1;
    public static final int ALL_DOWNLOADED =2;
    public static final int FINISH = 3;


    public String bookId;
    public int total;
    public int current;
    public int status = NORMAL;

    public ProgressEvent(String bookId, int current, int status, int total) {
        this.bookId = bookId;
        this.current = current;
        this.status = status;
        this.total = total;
    }

    public ProgressEvent(String bookId, int current, int total) {
        this.bookId = bookId;
        this.current = current;
        this.total = total;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int isStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
