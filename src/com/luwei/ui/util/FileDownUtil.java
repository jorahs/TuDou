package com.luwei.ui.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2015-1-25.
 */
public class FileDownUtil extends Thread {
    private String url = null;

    public FileDownUtil(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        try {
            URL httpUrl = new URL(url);
            URLConnection conn = httpUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            InputStream inputStream = conn.getInputStream();

            byte[] bytes = new byte[6 * 1024];

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File file = Environment.getExternalStorageDirectory();

                File download = new File(file, "test.apk");

                FileOutputStream fileOutputStream = new FileOutputStream(download);

                int len;
                while ((len = inputStream.read()) != -1) {
                    fileOutputStream.write(bytes, 0, len);
                }

                fileOutputStream.close();
            }

            inputStream.close();
            Log.d("file","file download successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
