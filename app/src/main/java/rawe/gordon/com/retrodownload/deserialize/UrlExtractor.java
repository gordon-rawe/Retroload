package rawe.gordon.com.retrodownload.deserialize;

import com.alibaba.fastjson.JSON;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import rawe.gordon.com.retrodownload.download.Retroload;
import rawe.gordon.com.retrodownload.download.Worker;

/**
 * Created by gordon on 9/29/16.
 */
public class UrlExtractor {
    public static List<String> extractUrls(String content) {
        List<String> urls = parseUrls(content);
        System.out.println(urls.size());
        printUrl(urls);
        return urls;
    }

    public static void downloadBook(final String bookId, final BookResult listener) throws IOException {
        /**
         * sample url for testing
         *
         * URLConnection connection = new URL("http://10.2.25.160/Pocket/New/Json/100021.txt").openConnection();
         * */
        Request request = new Request.Builder().url("http://10.2.25.160/Pocket/New/Json/100021.txt").build();
        Retroload.getInstance().getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) listener.onFail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) {
                    response.body().close();
                    if (listener != null) listener.onFail();
                }
                ByteBuffer buffer = ByteBuffer.wrap(response.body().bytes());
                FileOutputStream fileOutputStream = new FileOutputStream(Worker.getBookNameByBookId(bookId));
                FileChannel fileChannel = fileOutputStream.getChannel();
                fileChannel.write(buffer);
                fileChannel.close();
                if (listener != null) listener.onDownloaded(new String(response.body().bytes()));
                response.body().close();
                fileOutputStream.close();
            }
        });
    }

    public interface BookResult {
        void onDownloaded(String bookContent);

        void onFail();
    }

    public static List<String> parseUrls(String content) {
        List<String> images = new ArrayList<>();
        long start = System.currentTimeMillis();
        List<Parent> nodes = JSON.parseArray(content, Parent.class);
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(nodes.size());
        start = System.currentTimeMillis();
        for (Parent node : nodes) {
            for (SubParent node1 : node.pages) {
                for (SubSubParent node2 : node1.children) {
                    traverseSection(node2.sections, images);
                }
                traverseSection(node1.sections, images);
            }
        }
        System.out.println(System.currentTimeMillis() - start);
        return images;
    }

    public static void traverseSection(List<Section> sections, List<String> images) {
        for (Section section : sections) {
            for (SubSection subSection : section.subsections) {
                images.addAll(subSection.ContentImgSrcs);
//                for (Photo photo : subSection.photos) {
//                    images.add(photo.image_url);
//                    images.add(photo.origin_image_url);
//                }
            }
            if (section.CoverImageUrl != null && !section.CoverImageUrl.equals("")) {
                images.add(section.CoverImageUrl);
            }
        }
    }

    public static void printUrl(List<String> urls) {
        for (String url : urls) {
            System.out.println(url);
        }
    }
}
