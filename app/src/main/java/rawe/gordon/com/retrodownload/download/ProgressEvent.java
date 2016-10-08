package rawe.gordon.com.retrodownload.download;

/**
 * Created by gordon on 10/8/16.
 */
public class ProgressEvent {
    public String bookId;
    public int total;
    public int current;
    public boolean status;

    public ProgressEvent(String bookId, int current, boolean status, int total) {
        this.bookId = bookId;
        this.current = current;
        this.status = status;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
