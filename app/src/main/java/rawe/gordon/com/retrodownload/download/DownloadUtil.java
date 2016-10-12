package rawe.gordon.com.retrodownload.download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gordon on 9/22/16.
 */
public class DownloadUtil {
    public static void downloadFile(String url, String destination) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = Retroload.getInstance().getClient().newCall(request).execute();
        if (response.code() != 200) {
            response.close();
            throw new IOException("connection problem happened");
        }
        ByteBuffer buffer = ByteBuffer.wrap(response.body().bytes());
        FileOutputStream fileOutputStream = new FileOutputStream(destination);
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.write(buffer);
        fileChannel.close();
        response.close();
        fileOutputStream.close();
    }
}