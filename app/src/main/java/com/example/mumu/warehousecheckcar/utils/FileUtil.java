package com.example.mumu.warehousecheckcar.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    public static void writeFile(String content, File folder, String fileName) {
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append("/**************************/");
            bw.append(content);
            bw.append("/**************************/");
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
