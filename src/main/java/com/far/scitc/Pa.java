package com.far.scitc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Pa {
    public static void main(String[] args) {
        int imageNum = 1;//统计图片数量
        int pageSize = 149;//总页数
        for (int page = 1; page <= pageSize; page++) {

            String url = "https://api.bilibili.com/pgc/season/index/result?season_version=-1&area=-1&is_finish=-1&copyright=-1&season_status=-1&season_month=-1&year=-1&style_id=-1&order=3&st=1&sort=0&page="
                    + page + "&season_type=1&pagesize=20&type=1";
//            String url = "https://api.bilibili.com/x/space/arc/search?mid=808171&ps=30&tid=0&pn=1&keyword=&order=pubdate&jsonp=jsonp";

            Document document = null;
            try {
                document = Jsoup.connect(url).timeout(3000).ignoreContentType(true).get();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("解析url失败");
            }
            //转换为JSON
            JSONObject jsonObject = JSON.parseObject(document.text());

            List<String> coverList = JSONObjectToCoverList(jsonObject);
            for (String cover : coverList) {
                //创建路径
                String imagePath = createImagePath(cover);
                InputStream inputStream = createUrlConnection(cover);
                System.err.println("第 " + imageNum++ + " 张图片下载完成");
                inputStreamToFile(inputStream, imagePath);
            }
            System.err.println("-------第 " + page + " 页图片爬取完成");
        }


    }


    public static InputStream createUrlConnection(String cover) {
        InputStream inputStream = null;
        try {
            URL imgUrl = new URL(cover);
            URLConnection urlConnection = imgUrl.openConnection();
            urlConnection.setConnectTimeout(10 * 1000);
            inputStream = urlConnection.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("URL创建失败");
        }
        return inputStream;

    }

    //流转换
    public static void inputStreamToFile(InputStream inputStream, String imagePath) {
//        System.out.println("开始转换图片");
        try {
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imagePath));
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            bis.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("inputStream转换异常");
        }

    }

    //创建图片路径
    private static String createImagePath(String cover) {
        System.out.println("开始创建图片路径");
        //图片名称
        String imgName = cover.substring(cover.lastIndexOf("/") + 1);
        //创建路径
        String path = "D://BiliBili//images";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = dir + File.separator + imgName;
        System.out.println("图片路径：" + fileName);
        return fileName;
    }

    // 把json转化为只含有 链接 的集合
    public static List<String> JSONObjectToCoverList(JSONObject jsonObject) {
        List<String> coverList = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < jsonArray.size(); i++) {
            Map<String, String> map = (Map) jsonArray.get(i);
            coverList.add(map.get("cover"));
        }
        return coverList;
    }
}