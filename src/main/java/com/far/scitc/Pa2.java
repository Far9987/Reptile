package com.far.scitc;





        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Map;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;

        import com.alibaba.fastjson.JSON;
        import com.alibaba.fastjson.JSONArray;
        import com.alibaba.fastjson.JSONObject;
        import org.jsoup.Jsoup;
        import org.jsoup.nodes.Document;
        import org.jsoup.nodes.Element;
        import org.jsoup.select.Elements;

public class Pa2 {
    public static void main(String[] args) {

int page=9;
int uid=23085689;
pa(uid,page);

    }


    public static void pa(Integer uid,Integer page) {

        for (int j = 0; j < page; j++) {
            String url = "https://api.vc.bilibili.com/link_draw/v1/doc/doc_list?uid="+uid+"&page_num="+j+"&page_size=30&biz=all";
                          https://api.vc.bilibili.com/link_draw/v1/doc/doc_list?uid=7399330&page_num=0&page_size=30&biz=all
            // 2 解析 html ：  https：//jsoup.org
            try {

                Document doc = Jsoup.connect(url).timeout(30000).ignoreContentType(true).get();

                JSONObject jsonObject = JSON.parseObject(doc.text());
                List<List<JSONArray>> imglist = JSONObjectToCoverList(jsonObject);

                ExecutorService pool = Executors.newCachedThreadPool();
                pool = Executors.newFixedThreadPool(9);
                for (List<JSONArray> list : imglist) {
                    System.out.println(list.size());
//            pool = Executors.newSingleThreadExecutor();
                    for (int i = 0; i < list.size(); i++) {
                        Map map = (Map) list.get(i);
                        String img = (String) map.get("img_src");

                        pool.execute(new Download(img));
                    }
                }
                pool.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("解析失败");
            }
        }
    }

    public static List<List<JSONArray>> JSONObjectToCoverList(JSONObject jsonObject) {
        List<List<JSONArray>> imglist=new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("items");
        for (int i=0;i<jsonArray.size();i++){
            Map<String,List<JSONArray>> map= (Map) jsonArray.get(i);
            imglist.add(map.get("pictures"));
        }
        return imglist;
    }

}