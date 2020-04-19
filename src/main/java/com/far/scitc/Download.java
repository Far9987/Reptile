package com.far.scitc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2020/3/14 0014.
 */
public class Download implements Runnable {
    private  String imgPath;
    public Download(String src) {
        this.imgPath=src;
    }


    @Override
    public void run() {
        HttpURLConnection conn=null;
//        HttpURLConnection conn=null;
        InputStream is=null;
        FileOutputStream out=null;

        try {
            conn= (HttpURLConnection) new URL(imgPath).openConnection();
            is=conn.getInputStream();
            String path="D:\\BiliBili\\images\\";
            String file=imgPath.substring(imgPath.lastIndexOf("/")+1);
            file=path+file;

            out=new FileOutputStream(file);
            int size;
            byte[] buf=new byte[1024*16];
            while (-1 !=(size=is.read(buf))){
                out.write(buf,0,size);
            }
            System.out.println(Thread.currentThread().getName()+"下载"+imgPath);
        }
   catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (conn!=null){
        conn.disconnect();
            }
        }
    }
}
