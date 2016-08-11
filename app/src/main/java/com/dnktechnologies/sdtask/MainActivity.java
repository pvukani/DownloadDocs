package com.dnktechnologies.sdtask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int BUFFER_SIZE = 4096;
    File root, dir;
    String str_URL = "http://27.109.28.6/DhirenService/ExportExcel/1084/kalpesh_08112016_105138.xlsx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = android.os.Environment.getExternalStorageDirectory();
        dir = new File(root.getAbsolutePath() + "/DhirenDiamond");
        if (dir.exists() == false) {
            dir.mkdirs();
        }
        new AS_excel().execute();
    }

    public static void downloadFile(String fileURL, String saveDir)
            throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            Log.i("Content-Type = ", "" + contentType);
            Log.i("Content-Disposition = ", "" + disposition);
            Log.i("Content-Length = ", "" + contentLength);
            Log.i("fileName = ", "" + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            Log.i("Result", "File downloaded");
        } else {
            Log.i("Result", "No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }

    public class AS_excel extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                downloadFile(str_URL, dir.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
