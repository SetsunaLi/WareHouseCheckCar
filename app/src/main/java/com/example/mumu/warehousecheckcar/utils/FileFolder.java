package com.example.mumu.warehousecheckcar.utils;

import android.os.Environment;


import com.example.mumu.warehousecheckcar.Constant;

import java.io.File;

public class FileFolder {

    public static File rootDir() {
        return new File(Environment.getExternalStorageDirectory() + Constant.LOG_FILENAME);
    }

}
