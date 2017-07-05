package com.baidu.translate.demo;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransApi {
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    private String appid;
    private String securityKey;

    public TransApi(String appid, String securityKey) {
        this.appid = appid;
        this.securityKey = securityKey;
    }

    public static void log(String msg) {
        System.out.println(String.format("%s [%s] %s",
                "", "", msg));
    }

    public ResultBean getTransResult(String query, String from, String to) {
        Map<String, String> params = buildParams(query, from, to);
        String result = HttpGet.get(TRANS_API_HOST, params);
        ResultBean resultBean = null;
        try {
            resultBean = new Gson().fromJson(result, ResultBean.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            log("Error: " + result);
        }
        return resultBean;
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appid);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5.md5(src));

        return params;
    }

    public static class ResultBean {
        public String from;
        public String to;
        public List<Bean> trans_result;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ResultBean that = (ResultBean) o;

            if (from != null ? !from.equals(that.from) : that.from != null) return false;
            if (to != null ? !to.equals(that.to) : that.to != null) return false;
            return trans_result != null ? trans_result.equals(that.trans_result) : that.trans_result == null;

        }

        @Override
        public int hashCode() {
            int result = from != null ? from.hashCode() : 0;
            result = 31 * result + (to != null ? to.hashCode() : 0);
            result = 31 * result + (trans_result != null ? trans_result.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "ResultBean{" +
                    "from='" + from + '\'' +
                    ", to='" + to + '\'' +
                    ", trans_result=" + trans_result +
                    '}';
        }

        public static class Bean {
            public String src;
            public String dst;

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Bean bean = (Bean) o;

                if (src != null ? !src.equals(bean.src) : bean.src != null) return false;
                return dst != null ? dst.equals(bean.dst) : bean.dst == null;

            }

            @Override
            public int hashCode() {
                int result = src != null ? src.hashCode() : 0;
                result = 31 * result + (dst != null ? dst.hashCode() : 0);
                return result;
            }

            @Override
            public String toString() {
                return "Bean{" +
                        "src='" + src + '\'' +
                        ", dst='" + dst + '\'' +
                        '}';
            }
        }
    }
}
