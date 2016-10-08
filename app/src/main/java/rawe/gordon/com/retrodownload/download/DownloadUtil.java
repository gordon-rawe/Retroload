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
        ByteBuffer buffer = ByteBuffer.wrap(response.body().bytes());
        FileOutputStream fileOutputStream = new FileOutputStream(destination);
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.write(buffer);
        fileChannel.close();
        fileOutputStream.close();
    }

    public static void downloadFile1(String url, String destination) throws IOException {
        ReadableByteChannel rbc = Channels.newChannel(new URL(url).openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(destination);
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(rbc, 0, 1 << 23);
        fileOutputStream.close();
        fileChannel.close();
        rbc.close();
    }
}