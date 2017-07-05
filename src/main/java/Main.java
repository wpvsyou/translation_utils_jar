import com.baidu.translate.demo.TransApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private final static String TAG = "BTRSN";
    private static final SimpleDateFormat SDF_LOG = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20170627000060621";
    private static final String SECURITY_KEY = "zFjZgCuMH_vfB9Q7ieM4";
    private final static int QUERY_STR_MAX_LENGTH = 2000;
    private static boolean SHOW_INFO_LOG = false;
    private static String SRC_FILE_PATH = "";
    private static String SRC_CODE = "auto";
    private static String DES_CODE = "en";

    public static void main(String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            printfHelpDetails();
            return;
        }
        List<String> cmds = Arrays.asList(args);
        SHOW_INFO_LOG = cmds.contains("-i") || cmds.contains("--info");
        if (cmds.contains("-h") || cmds.contains("--help")) {
            printfHelpDetails();
            return;
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-f") || args[i].equals("--file")) {
                if (args.length >= i + 1) {
                    SRC_FILE_PATH = args[i + 1];
                } else {
                    log("Error: param -f --file must with a valid resource file path!");
                    printfHelpDetails();
                    return;
                }
            }
            if (args[i].equals("-s") || args[i].equals("--src")) {
                if (args.length >= i + 1) {
                    SRC_CODE = args[i + 1];
                } else {
                    log("Error: param -s --src!");
                    printfHelpDetails();
                    return;
                }
            }
            if (args[i].equals("-d") || args[i].equals("--des")) {
                if (args.length >= i + 1) {
                    DES_CODE = args[i + 1];
                } else {
                    log("Error: param -s --src!");
                    printfHelpDetails();
                    return;
                }
            }
        }

        startTranslation();
    }

    private static void startTranslation() {
        do {
            if (SRC_FILE_PATH.trim().isEmpty()) {
                log("Error: input source file must not be null! more details please with --help or -h");
                printfHelpDetails();
                break;
            }
            TransApi api = new TransApi(APP_ID, SECURITY_KEY);
            logInfo("check input resource file path: " + SRC_FILE_PATH);
            File srcF = new File(SRC_FILE_PATH);
            if (!srcF.exists()) {
                log("Error: in put the source file un exists!");
                break;
            }

            if (!srcF.isFile()) {
                log("Error: in put the source file invalid!");
                break;
            }

            if (!srcF.canRead()) {
                log("Error: in put the source file couldn't read by function! please retry this " +
                        "utils after run \"sudo chmod a+x [file path]\" command!");
                break;
            }

            logInfo("load local source file success!");

            SAXReader reader = new SAXReader();
            HashMap<String, String> tmp = null;
            try {
                Document document = reader.read(srcF);
                tmp = parsingXml(document);
            } catch (DocumentException e) {
                e.printStackTrace();
                log("Error: Document Exception when Reader init Document!\n" + e);
            }

            if (tmp == null || tmp.isEmpty()) {
                log("Error: parsing input file error!");
                break;
            }

            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            String queryAll = gson.toJson(tmp);
            logInfo("check query all length: " + queryAll.length());
            String[] qs = getSubStringIfTooLength(queryAll);
            ArrayList<String> transResult = new ArrayList<>();
            for (String s : qs) {
                logInfo("check send trans result: " + s);
                TransApi.ResultBean rb = api.getTransResult(s, SRC_CODE, DES_CODE);
                transResult.add(rb.trans_result.get(0).dst);
            }

            if (transResult.isEmpty()) {
                log("Error: trans result error!");
                break;
            }

            String desStr = "";
            for (String s : transResult) {
                desStr += s;
            }
            logInfo("check des str: " + desStr + ", length: " + desStr.length());

            HashMap<String, String> resultMap = null;
            try {
                resultMap = gson.fromJson(desStr, new TypeToken<HashMap<String, String>>() {
                }.
                        getType());
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            if (resultMap == null || resultMap.isEmpty()) {
                log("Error: result string convert to map failed!");
                break;
            }
            File outFile = new File("out");
            if (!outFile.exists()) {
                outFile.mkdirs();
            }
            File stringsXml = new File(outFile, "strings.xml");
            if (stringsXml.exists()) {
                stringsXml.delete();
            }
            FileOutputStream fops = null;
            try {
                fops = new FileOutputStream(stringsXml);
                fops.write("<resources>\n".getBytes());
                fops.write(String.format("<!-- Auto generate by peng.wang@pekall.com translation " +
                        "utils at %s.-->%n", SDF_LOG.format(new Date())).getBytes());
                for (Map.Entry<String, String> e : resultMap.entrySet()) {
                    String s = String.format("\t<string name=\"%s\">%s</string>%n", unicode2string(e.getKey()), unicode2string(e.getValue()));
                    logInfo(s);
                    fops.write(s.getBytes());
                }
                fops.write("</resources>".getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } finally {
                if (fops != null) {
                    try {
                        fops.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            log("please check the  generate file: " + stringsXml.getAbsolutePath());
            log("=== SUCCESS ===");
            return;
        } while (false);
        log("=== FAILED!!! ===");
    }

    public static String[] getSubStringIfTooLength(String str) {
        String[] result;
        if (str.length() > QUERY_STR_MAX_LENGTH) {
            int size = str.length() / QUERY_STR_MAX_LENGTH + 1;
            result = new String[size];
            for (int i = 0; i < size; i++) {
                int start = i * QUERY_STR_MAX_LENGTH;
                int end = (i + 1) * QUERY_STR_MAX_LENGTH;
                if (end > str.length()) {
                    end = str.length();
                }
                logInfo(String.format("getSubStringIfTooLength: start[%s], end[%s], str length[%s], " +
                        "size[%s]", start, end, str.length(), size));
                result[i] = str.substring(start, end);
            }

        } else {
            result = new String[1];
            result[0] = str;
        }
        return result;
    }

    public static HashMap<String, String> parsingXml(Document document) throws DocumentException {
        Element root = document.getRootElement();
        // iterate through child elements of root
        HashMap<String, String> tmp = new HashMap<>();
        for (Iterator<Element> it = root.elementIterator(); it.hasNext(); ) {
            Element element = it.next();
            // do something
            Attribute isSelfAttr = element.attribute("name");
            String key = isSelfAttr.getStringValue();
            String value = element.getStringValue();
//            value = value.replace(";", "").replace("“", "\\\"").replace("”", "\\\"");
            logInfo(String.format("<string name=\"%s\">%s</string>", key, value));
            tmp.put(key, value);
        }
        logInfo("************************************************************");
        return tmp;
    }

    private final static HashMap<String, String> SUPPORTED_MAP = new HashMap<>();
    static {
        SUPPORTED_MAP.put("auto", "自动检测");
        SUPPORTED_MAP.put("zh", "中文");
        SUPPORTED_MAP.put("en", "英语");
        SUPPORTED_MAP.put("yue", "粤语");
        SUPPORTED_MAP.put("wyw", "文言文");
        SUPPORTED_MAP.put("jp", "日语");
        SUPPORTED_MAP.put("kor", "韩语");
        SUPPORTED_MAP.put("fra", "法语");
        SUPPORTED_MAP.put("spa", "西班牙语");
        SUPPORTED_MAP.put("th", "泰语");
        SUPPORTED_MAP.put("ara", "阿拉伯语");
        SUPPORTED_MAP.put("ru", "俄语");
        SUPPORTED_MAP.put("pt", "葡萄牙语");
        SUPPORTED_MAP.put("de", "德语");
        SUPPORTED_MAP.put("it", "意大利语");
        SUPPORTED_MAP.put("el", "希腊语");
        SUPPORTED_MAP.put("nl", "荷兰语");
        SUPPORTED_MAP.put("pl", "波兰语");
        SUPPORTED_MAP.put("bul", "保加利亚语");
        SUPPORTED_MAP.put("est", "爱沙尼亚语");
        SUPPORTED_MAP.put("dan", "丹麦语");
        SUPPORTED_MAP.put("fin", "芬兰语");
        SUPPORTED_MAP.put("cs", "捷克语");
        SUPPORTED_MAP.put("rom", "罗马尼亚语");
        SUPPORTED_MAP.put("slo", "斯洛文尼亚语");
        SUPPORTED_MAP.put("swe", "瑞典语");
        SUPPORTED_MAP.put("hu", "匈牙利语");
        SUPPORTED_MAP.put("cht", "繁体中文");
        SUPPORTED_MAP.put("vie", "越南语");
    }

    private static void printfHelpDetails() {
            StringBuilder sb = new StringBuilder("  Welcome use the peng.wang@pekall.com personal translation java utils.")
                    .append("\n")
                    .append("\n")
                    .append("  ").append("E.g").append("\t-f[--file] /project_path/resource_path/string.xml")
                    .append("\n")
                    .append("\n")
                    .append("  ").append("-f").append("\t--file\tThe input source file path that will be translation.").append("\n")
                    .append("  ").append("-h").append("\t--help\tGet help information.").append("\n")
                    .append("  ").append("-i").append("\t--info\tPrint the process debug logs.").append("\n")
                    .append("  ").append("-s").append("\t --src\tSrc code.").append("\n")
                    .append("  ").append("-d").append("\t --des\tDes code.").append("\n");
        for (Map.Entry<String, String> entry : SUPPORTED_MAP.entrySet()) {
            sb.append("  ").append(" ").append("\t   \t\t").append("[").append(entry.getKey()).append("\t").append(entry.getValue()).append("]").append("\n");
        }
        System.out.print(sb.toString());
    }

    public static void log(String msg) {
        System.out.println(String.format("%s [%s] %s",
                TAG, SDF_LOG.format(new Date(System.currentTimeMillis())), msg));
    }

    public static void logInfo(String msg) {
        if (SHOW_INFO_LOG) System.out.println(String.format("[INFO] [%s] %s",
                SDF_LOG.format(new Date(System.currentTimeMillis())), msg));
    }

    public static String unicode2string(String s) {
        List<String> list = new ArrayList<String>();
        String zz = "\\\\u[0-9,a-z,A-Z]{4}";
        //正则表达式用法参考API
        Pattern pattern = Pattern.compile(zz);
        Matcher m = pattern.matcher(s);
        while (m.find()) {
            list.add(m.group());
        }
        for (int i = 0, j = 2; i < list.size(); i++) {
            String st = list.get(i).substring(j, j + 4);
            //将得到的数值按照16进制解析为十进制整数，再強转为字符
            char ch = (char) Integer.parseInt(st, 16);
            //用得到的字符替换编码表达式
            s = s.replace(list.get(i), String.valueOf(ch));
        }
        s = s.replace("“", "\"").replace("”", "\"").replace("：", ":").replace("，", ",");
        return s;
    }
}
