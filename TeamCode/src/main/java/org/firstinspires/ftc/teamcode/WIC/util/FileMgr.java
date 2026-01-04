package org.firstinspires.ftc.teamcode.WIC.util;


import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;

public class FileMgr {

    @NonNull
    static File getWicFolder() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        return new File(externalStorageDirectory, "WICFolder");
    }
}
