package rawe.gordon.com.retrodownload.download;

/**
 * 这个类用于使用EventBus向页面回调数据，主要有四个字段，分别是{@Param bookId}用于区别是哪本书的事件,
 * {@Param total}是某本书的总图片数量,{@Param current}是当前下载完成的数量,{@Param status}是回调事
 * 件的种类。
 */
public class ProgressEvent {

    /**
     * 正在下载攻略中，并且在回调进度
     */
    public static final int NORMAL = 0;

    /**
     * 下载攻略暂停
     */
    public static final int PAUSED = 1;

    /**
     * 下载攻略成功（包括图片和文本）
     */
    public static final int ALL_DOWNLOADED = 2;

    /**
     * 取消下载攻略成功
     */
    public static final int CANCEL = 3;

    /**
     * 下载攻略书文本失败
     */
    public static final int BOOK_DOWNLOAD_FAIL = 5;

    /**
     * 下载攻略书文本成功
     */
    public static final int BOOK_DOWNLOAD_SUCCESS = 6;

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
