package com.company;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        List<Integer> lossyValues = Arrays.asList(50,60,70,80,90);
        List<Float> qualityValues = Arrays.asList(0.3f,0.5f,0.7f);
//        optimizeGifFromDirectory(lossyValues,"original_v2","optimized_v3");
        optimizeImgFromDirectory(qualityValues,"original_img", "compressed_img");
//        optmizeImgToWebP("original_img.jpg", "compressed-webp-20.webp","20");

    }

    static void optimizeGifFromDirectory(List<Integer> lossyValues, String originalDirectory, String outDirectory) throws IOException {
        List<String> fileNames = getFileNames(originalDirectory);

        Path path;
        Path path2;
        Path path3;
        Process p = null;
        String nameFileCompres;
        String originDirectory = originalDirectory + "/";
        String nameFileOriginal;
        String fileOriginalPath;
        String optimizedDirectoryPath =outDirectory + "/";
        Files.createDirectory(Paths.get(outDirectory));
        String directory;

        final String cmd = "./gifsicle -O3 --lossy=BBB --colors=254 -o AAA.gif CCC.gif";

        try {

            for (String name : fileNames) {
                path = Paths.get(optimizedDirectoryPath + name.replace(".gif",""));
                Files.createDirectory(path);
                fileOriginalPath = originDirectory + name;
                directory = optimizedDirectoryPath + name.replace(".gif","") + "/";
                path2 = Paths.get(fileOriginalPath);
                path3 = Paths.get(directory + name);
                Files.copy(path2, path3, StandardCopyOption.REPLACE_EXISTING);
                nameFileOriginal = directory + name;
                for (Integer lossyValue : lossyValues) {
                    nameFileCompres = directory + name.replace(".gif","-" +lossyValue+".gif");
                    p = Runtime.getRuntime().exec(cmd
                            .replace("BBB", lossyValue.toString())
                            .replace("CCC.gif", nameFileOriginal)
                            .replace("AAA.gif", nameFileCompres));
                }
            }

            showConsoleOutput(p);
            System.out.println("Done.");
        } catch (Exception err) {
            err.printStackTrace();
        }

    }

    static void optimizeImgFromDirectory(List<Float> qualityValues, String originalDirectory, String outDirectory) throws IOException {
        List<String> fileNames = getFileNames(originalDirectory);

        Path path;
        Path path2;
        Path path3;
        Process p = null;
        String nameFileCompres;
        String originDirectory = originalDirectory + "/";
        String nameFileOriginal;
        String fileOriginalPath;
        String optimizedDirectoryPath =outDirectory + "/";
        Files.createDirectory(Paths.get(outDirectory));
        String directory;

        for (String name : fileNames) {
            path = Paths.get(optimizedDirectoryPath + name.replace(".jpg",""));
            Files.createDirectory(path);
            fileOriginalPath = originDirectory + name;
            directory = optimizedDirectoryPath + name.replace(".jpg","") + "/";
            path2 = Paths.get(fileOriginalPath);
            path3 = Paths.get(directory + name);
            Files.copy(path2, path3, StandardCopyOption.REPLACE_EXISTING);
            nameFileOriginal = directory + name;
            for (Float qualityValue : qualityValues) {
                nameFileCompres = directory + name.replace(".jpg","-" + qualityValue +".jpg");
                optimizeImg(nameFileOriginal,nameFileCompres,qualityValue);
            }
        }

    }

    static void optimizeImg(String inputFile, String outputFile,float compressionQuality) throws IOException {
        File input = new File(inputFile);
        BufferedImage image = ImageIO.read(input);

        File output = new File(outputFile);
        OutputStream out = new FileOutputStream(output);

        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(out);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(compressionQuality);
        }

        writer.write(null, new IIOImage(image, null, null), param);

        out.close();
        ios.close();
        writer.dispose();
    }

    static void optmizeImgToWebP(String inputFile, String outputFile, String quality) throws IOException {
        final String cmd = "cwebp AAA -q CCC -o BBB";
        Process p = null;

        try {

            p = Runtime.getRuntime().exec(cmd
                    .replace("CCC", quality)
                    .replace("BBB", outputFile)
                    .replace("AAA", inputFile));

            showConsoleOutput(p);
        }catch (Exception err) {
            err.printStackTrace();
        }

    }

    static List<String> getFileNames(String path){
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        List<String> fileNames = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
                fileNames.add(listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }

        return fileNames;
    }

    static void showConsoleOutput(Process p) throws IOException, InterruptedException {
        String line;
        BufferedReader bri = new BufferedReader
                (new InputStreamReader(p.getInputStream()));
        BufferedReader bre = new BufferedReader
                (new InputStreamReader(p.getErrorStream()));
        while ((line = bri.readLine()) != null) {
            System.out.println(line);
        }
        bri.close();
        while ((line = bre.readLine()) != null) {
            System.out.println(line);
        }
        bre.close();
        p.waitFor();
        System.out.println("Done.");
    }

}
