package com.company;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {

    public static void main(String[] args) {
        String url = null;
        try {
            url = convertUrl(args[0]);
        } catch (Exception ex) {
            System.out.println(ex.toString());
            System.exit(-1);
        }

        File zipFile = downloadZipFile(url);
        unzipFiles(zipFile);

    }

    public static String convertUrl(String url) throws Exception {
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

        throw new Exception("Bad url");
    }

    public static String buildNewUrl(String url, String id) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(url);
        urlBuilder.append("output/");
        urlBuilder.append(id);
        urlBuilder.append(".zip?download=zip");

        return urlBuilder.toString();
    }

    public static File downloadZipFile(String urlString) {
//        try {
//            URL website = new URL(urlString);
//            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
//            FileOutputStream fos = new FileOutputStream("output.zip");
//            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        File zipFile = new File("output.zip");
        System.out.println(zipFile.getName());
        return zipFile;

//        } catch (MalformedURLException ex) {
//            System.out.println("Malformed url: " + ex.toString());
//            System.exit(-1);
//        } catch (IOException ex) {
//            System.out.println(ex.toString());
//            System.exit(-1);
//        }
    }

    public static void unzipFiles(File zipFile) {
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));

            ZipEntry zipEntry = zis.getNextEntry();
            String fileName = null;
            byte[] buffer = new byte[1024];
            while (zipEntry != null) {


                if (zipEntry.getName().contains(".flv")) {
                    if (zipEntry.getName().contains("screenshare")) {
                        fileName = "video.flv";
                    } else if (zipEntry.getName().contains("cameraVoip")) {
                        fileName = "audio.flv";
                    }
                } else {
                    fileName = null;
                }

                if (fileName != null) {
                    File newFile = new File(fileName);
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int lenght;
                    while ((lenght = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, lenght);
                    }
                    fos.close();
                }

                zipEntry = zis.getNextEntry();


            }
            zis.closeEntry();
            zis.close();

        } catch (
                FileNotFoundException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }


}
