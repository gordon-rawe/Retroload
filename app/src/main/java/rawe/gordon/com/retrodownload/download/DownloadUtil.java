package rawe.gordon.com.retrodownload.download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by gordon on 9/22/16.
 */
public class DownloadUtil {
    public static void downloadFile(String url, String destination) throws IOException {
        ReadableByteChannel rbc = Channels.newChannel(new URL(url).openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(destination);
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(rbc, 0, 1 << 24);
        fileOutputStream.close();
        fileChannel.close();
        rbc.close();
    }
}