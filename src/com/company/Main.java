package com.company;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    private static ArrayList<File> filesToBeDeleted;

    public static void main(String[] args) {
        String url = null;
        try {
            url = convertUrl(args[0]);
        } catch (Exception ex) {
            System.out.println(ex.toString());
            System.exit(-1);
        }

        filesToBeDeleted = new ArrayList<>();

        File zipFile = downloadZipFile(url);
        unzipFiles(zipFile);

        mergeMedia();
        deleteFiles();

        System.out.println("\nThe video file is output.flv");
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
        try {
            System.out.print("Downloading file: ");
            URL website = new URL(urlString);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream("output.zip");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            System.out.println("success");
            return new File("output.zip");

        } catch (MalformedURLException ex) {
            System.out.println("Malformed url: " + ex.toString());
            System.exit(-1);
        } catch (IOException ex) {
            System.out.println(ex.toString());
            System.exit(-1);
        }

        return null;
    }

    public static void unzipFiles(File zipFile) {
        System.out.print("Unzipping source: ");
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
                    filesToBeDeleted.add(newFile);
                }

                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        filesToBeDeleted.add(zipFile);
        System.out.println("success");
    }

    public static void mergeMedia() {
        System.out.print("Merging audio and video: ");
        try {
            Process process = Runtime.getRuntime().exec(
                    new String[]{
                        "cmd.exe", "/c", "start", "/wait",
                        "ffmpeg",
                        "-i", "audio.flv",
                        "-i", "video.flv",
                        "-c", "copy",
                        "-map", "0:a:0",
                        "-map", "1:v:0",
                        "-shortest", "output.flv"
                    }
            );
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("success");
    }

    public static void deleteFiles() {
        System.out.println("Deleting files: ");
        for (File file: filesToBeDeleted) {
            System.out.print("\t- " + file.getName());
            if(file.delete()) {
                System.out.println(" deleted");
            } else {
                System.out.println(" could not deleted");
            }
        }
    }


}
