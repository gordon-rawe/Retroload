package rawe.gordon.com.retrodownload.deserialize;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gordon on 9/29/16.
 */
public class UrlExtractor {
    public static List<String> extractUrls() {
        List<String> urls = new ArrayList<>();
        try {
            String shit;

            System.out.println(shit = download());
            urls = parseUrls(shit);
            System.out.println(urls.size());
            printUrl(urls);
            return urls;
        } catch (IOException e) {
            e.printStackTrace();
            return urls;
        }
    }

    public static String download() throws IOException {
        long start = System.currentTimeMillis();
        StringBuilder builder = new StringBuilder();
        URLConnection connection = new URL("http://10.2.25.160/Pocket/New/Json/100021.txt").openConnection();
//        URLConnection connection = new URL("http://localhost:8000/x.json").openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
        }
        bufferedReader.close();
        System.out.println(System.currentTimeMillis() - start);
        return builder.toString();
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
