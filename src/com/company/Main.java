package com.company;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class Main {

    public static void main(String[] args) {
        String url = convertUrl(args[0]);
        downloadFile(url);
    }

    //TODO Handle exceptions
    public static String convertUrl(String url) {
        int counter = 0;
        int idStartIndex = 0;

        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) == '/') {
                counter++;
                if (counter == 3) {
                    idStartIndex = i + 1;
                }

                if (counter == 4) {
                    return buildNewUrl(
                            url.substring(0, i + 1),
                            url.substring(idStartIndex, i)
                    );
                }
            }
        }
        return "bad string";
    }

    public static String buildNewUrl(String url, String id) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(url);
        urlBuilder.append("output/");
        urlBuilder.append(id);
        urlBuilder.append(".zip?download=zip");

        return urlBuilder.toString();
    }

    public static void downloadFile(String urlString) {
        try {
            URL website = new URL(urlString);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream("output.zip");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        } catch (MalformedURLException ex) {
            System.out.println("Bad url: " + ex.toString());
            System.exit(-1);

        } catch (IOException ex) {
            System.out.println(ex.toString());
            System.exit(-1);
        }
    }

}
