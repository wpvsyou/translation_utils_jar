import com.baidu.translate.demo.TransApi;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public final static String TAG = "TRANSLATION_UTIL_JAR";
    public static final SimpleDateFormat SDF_LOG = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20170627000060621";
    private static final String SECURITY_KEY = "zFjZgCuMH_vfB9Q7ieM4";
    private final static int QUERY_STR_MAX_LENGTH = 2000;
    private final static HashMap<String, String> SUPPORTED_MAP_MODEL_1 = new HashMap<>();
    private final static HashMap<String, String> SUPPORTED_MAP_MODEL_2 = new HashMap<>();
    private static boolean SHOW_INFO_LOG = false;
    private static String SRC_FILE_PATH = "";
    private static String SRC_CODE = "auto";
    private static String DST_CODE = "en";
    private static String SEPARATOR = "\\\\n";
    private static String MODE = "bath";

    static {
        SUPPORTED_MAP_MODEL_1.put("auto", "自动检测");
        SUPPORTED_MAP_MODEL_1.put("zh", "中文");
        SUPPORTED_MAP_MODEL_1.put("en", "英语");
        SUPPORTED_MAP_MODEL_1.put("yue", "粤语");
        SUPPORTED_MAP_MODEL_1.put("jp", "日语");
        SUPPORTED_MAP_MODEL_1.put("kor", "韩语");
        SUPPORTED_MAP_MODEL_1.put("ru", "俄语");
        SUPPORTED_MAP_MODEL_1.put("it", "意大利语");
        SUPPORTED_MAP_MODEL_1.put("cs", "捷克语");
        SUPPORTED_MAP_MODEL_1.put("slo", "斯洛文尼亚语");
        SUPPORTED_MAP_MODEL_1.put("hu", "匈牙利语");
        SUPPORTED_MAP_MODEL_1.put("cht", "繁体中文");
        SUPPORTED_MAP_MODEL_1.put("vie", "越南语");
    }

    static {
        SUPPORTED_MAP_MODEL_2.put("wyw", "文言文");
        SUPPORTED_MAP_MODEL_2.put("fra", "法语");
        SUPPORTED_MAP_MODEL_2.put("spa", "西班牙语");
        SUPPORTED_MAP_MODEL_2.put("th", "泰语");
        SUPPORTED_MAP_MODEL_2.put("ara", "阿拉伯语");
        SUPPORTED_MAP_MODEL_2.put("pt", "葡萄牙语");
        SUPPORTED_MAP_MODEL_2.put("de", "德语");
        SUPPORTED_MAP_MODEL_2.put("el", "希腊语");
        SUPPORTED_MAP_MODEL_2.put("nl", "荷兰语");
        SUPPORTED_MAP_MODEL_2.put("pl", "波兰语");
        SUPPORTED_MAP_MODEL_2.put("bul", "保加利亚语");
        SUPPORTED_MAP_MODEL_2.put("est", "爱沙尼亚语");
        SUPPORTED_MAP_MODEL_2.put("dan", "丹麦语");
        SUPPORTED_MAP_MODEL_2.put("fin", "芬兰语");
        SUPPORTED_MAP_MODEL_2.put("rom", "罗马尼亚语");
        SUPPORTED_MAP_MODEL_2.put("swe", "瑞典语");
    }

    public static void main(String[] args) {
        log("===> WELCOME USE TRANSLATION UTIL JAR. ");
        log("===> WAITING FOR INIT...");
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
            if (args[i].equals("-d") || args[i].equals("--dst")) {
                if (args.length >= i + 1) {
                    DST_CODE = args[i + 1];
                } else {
                    log("Error: param -d --dst!");
                    printfHelpDetails();
                    return;
                }
            }
            if (args[i].equals("-m") || args[i].equals("--mode")) {
                if (args.length > i + 1) {
                    MODE = args[i + 1];
                } else {
                    log("Error: param -m --mode!");
                    printfHelpDetails();
                    return;
                }
            }
        }

        if (!SUPPORTED_MAP_MODEL_2.containsKey(SRC_CODE) && !SUPPORTED_MAP_MODEL_1.containsKey(SRC_CODE)) {
            log("Error: param -s --src invalid, please use -h to check support --src code list!");
            return;
        }

        if (!SUPPORTED_MAP_MODEL_2.containsKey(DST_CODE) && !SUPPORTED_MAP_MODEL_1.containsKey(DST_CODE)) {
            log("Error: param -d --dst invalid, please use -h to check support --dst code list!");
            return;
        }

        startTranslation();
    }

    private static void startTranslation() {
        log("===> START TRANSLATION WAIT A MINUTE...");
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
            LinkedHashMap<String, String> tmp = null;
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

            if (SUPPORTED_MAP_MODEL_2.keySet().contains(DST_CODE)) {
                MODE = "single";
            }

            if (MODE.equals("single")) {
                translationOneByOne(tmp);
                return;
            }
            ArrayList<String> willQueryStrList = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            for (String s : tmp.values()) {
                logInfo("current string builder length: " + sb.toString().length());
                logInfo("current value string length: " + s.length());
                long sumLength = sb.length() + s.length() + 1;
                logInfo("sum current two string: " + sumLength);
                if (sumLength < QUERY_STR_MAX_LENGTH) {
                    sb.append(s).append(SEPARATOR);
                } else {
                    String query = sb.toString();
                    logInfo("check query str: " + query);
                    willQueryStrList.add(query);
                    sb = new StringBuilder();
                    sb.append(s).append(SEPARATOR);
                }
            }
            if (!sb.toString().isEmpty()) {
                String query = sb.toString();
                logInfo("check query str: " + query);
                willQueryStrList.add(query);
            }
            ArrayList<String> transResult = new ArrayList<>();
            for (String query : willQueryStrList) {
                TransApi.ResultBean rb = api.getTransResult(query, SRC_CODE, DST_CODE);
                logInfo("check send trans result: " + query + "\ncheck result: " + rb);
                transResult.add(rb.trans_result.get(0).dst);
            }

            if (transResult.isEmpty()) {
                log("Error: trans result error!");
                break;
            }
            LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
            String desStr = "";
            for (String result : transResult) {
                desStr += unicode2string(result);
            }
            desStr = desStr.replace("\\ n", SEPARATOR);
            if (!desStr.endsWith(SEPARATOR)) {
                logInfo("invalid end char, check last index: " + desStr.lastIndexOf(SEPARATOR));
                desStr = desStr.substring(0, desStr.lastIndexOf(SEPARATOR));
            }
            logInfo("check des str: " + desStr);

            String[] desCollect = desStr.split(String.valueOf(SEPARATOR));
            for (String s : desCollect) {
                logInfo(s);
            }
            long desCollectSize = desCollect.length;
            long srcCollectSize = tmp.keySet().size();
            logInfo("check des size: " + desCollectSize);
            logInfo("check src size: " + srcCollectSize);
            if (desCollectSize != srcCollectSize) {
                log("Error: origin source size un equals result data size, maybe data lost!!!");
                break;
            }

            int i = 0;
            for (String k : tmp.keySet()) {
                resultMap.put(k, desCollect[i++]);
            }

            if (resultMap.isEmpty()) {
                log("Error: result string convert to map failed!");
                break;
            }

            if (saveNewStringInFile(resultMap)) {
                log("===> SUCCESS");
            } else {
                break;
            }
            return;
        } while (false);
        log("===> FAILED!!!");
    }

    private static void translationOneByOne(LinkedHashMap<String, String> tmp) {
        logInfo("translationOneByOne");
        LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        int count = 0;
        ProgressBar progressBar = new ProgressBar();
        for (Map.Entry<String, String> entry : tmp.entrySet()) {
            String val = "";
            try {
                TransApi.ResultBean rb = api.getTransResult(entry.getValue(), SRC_CODE, DST_CODE);
                val = unicode2string(rb.trans_result.get(0).dst);
                float percent = ((float) ++count / (float) tmp.size()) * 100;
//                logInfo("translationOneByOne check result des: " + val);
                int p = (int) percent;
                progressBar.showBarByPoint(p);
                /*String printStr = String.format("=== TRANSLATION PERCENT　%s%% ===", p);
                System.out.print(printStr);
                for (int j = 0; j <= String.valueOf(printStr).length(); j++) {
                    System.out.print("\b");
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            resultMap.put(entry.getKey(), val);
        }
        System.out.print("\n");
        if (saveNewStringInFile(resultMap)) {
            log("===> TRANSLATION PERCENT　100%");
            log("===> SUCCESS");
        } else {
            log("===> FAILED!!!");
        }
    }

    private static boolean saveNewStringInFile(LinkedHashMap<String, String> resultMap) {
        boolean result = false;
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
            fops.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
            fops.write("<resources>\n".getBytes());
            fops.write(String.format("%n<!-- Auto generate by peng.wang@pekall.com translation " +
                    "utils at %s.-->%n%n", SDF_LOG.format(new Date())).getBytes());
            for (Map.Entry<String, String> e : resultMap.entrySet()) {
                String val = unicode2string(e.getValue());
                do {
                    if (val.endsWith(" ")) {
                        val = val.substring(0, val.length() - 1);
                        continue;
                    }
                    if (val.endsWith("\\")) {
                        val = val.substring(0, val.length() - 1);
                        continue;
                    }
                    if (val.endsWith(",") || val.endsWith("，")) {
                        val = val.substring(0, val.length() - 1);
                        continue;
                    }
                    if (val.startsWith("，") || val.startsWith(",")) {
                        val = val.substring(1, val.length());
                        continue;
                    }
                    if (val.startsWith(" ")) {
                        val = val.substring(1, val.length());
                        continue;
                    }
                    if (val.startsWith("\\")) {
                        val = val.substring(1, val.length());
                        continue;
                    }
                    break;
                } while (true);
                String s = String.format("\t<string name=\"%s\">%s</string>%n", unicode2string(e.getKey()), val);
                logInfo(s);
                fops.write(s.getBytes());
            }
            fops.write("</resources>".getBytes());
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fops != null) {
                try {
                    fops.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        log("===> PLEASE CHECK THE  GENERATE FILE \t[" + stringsXml.getAbsolutePath() + "]");
        return result;
    }

    public static LinkedHashMap<String, String> parsingXml(Document document) throws DocumentException {
        Element root = document.getRootElement();
        // iterate through child elements of root
        LinkedHashMap<String, String> tmp = new LinkedHashMap<>();
        for (Iterator<Element> it = root.elementIterator(); it.hasNext(); ) {
            Element element = it.next();
            // do something
            Attribute isSelfAttr = element.attribute("name");
            String key = isSelfAttr.getStringValue();
            String value = element.getStringValue();
            value = value.replace(SEPARATOR, "").replace("“", "\\\"").replace("”", "\\\"").replace("，", ",");
            logInfo(String.format("<string name=\"%s\">%s</string>", key, value));
            tmp.put(key, value);
        }
        logInfo("************************************************************");
        return tmp;
    }

    private static void printfHelpDetails() {
        StringBuilder sb = new StringBuilder("  Welcome use the peng.wang@pekall.com personal translation jar utils.")
                .append("\n")
                .append("\n")
                .append("  ").append("E.g").append("\t-f[--file] /project_path/resource_path/string.xml")
                .append("\n")
                .append("  ").append("   ").append("\tjar[COMMAND][VALUE]")
                .append("\n")
                .append("\n")
                .append("  ").append("[-f]").append("\t--file\tThe input source file path that will be translation.").append("\n")
                .append("  ").append("[-h]").append("\t--help\tGet help information.").append("\n")
                .append("  ").append("[-i]").append("\t--info\tPrint the process debug logs.").append("\n")
                .append("  ").append("[-m]").append("\t--mode\t[batch ] is batch translation,Low accuracy but fast.").append("\n")
                .append("  ").append("  ").append("\t      \t[single] is translation one by one,Time consuming but accurate.").append("\n")
                .append("  ").append("[-s]").append("\t --src\tOriginal language code.").append("\n")
                .append("  ").append("[-d]").append("\t --dst\tTranslation to language code.").append("\n");

        sb.append("  ").append(" ").append("\t   \t").append("*** This language support mode1 that batch translation.").append("\n");
        for (Map.Entry<String, String> entry : SUPPORTED_MAP_MODEL_1.entrySet()) {
            sb.append("  ").append(" ").append("\t   \t").append("[").append(entry.getKey()).append("\t").append(entry.getValue()).append("]").append("\n");
        }
        sb.append("  ").append(" ").append("\t   \t").append("*** This language support mode２ that one by one translation.").append("\n");
        for (Map.Entry<String, String> entry : SUPPORTED_MAP_MODEL_2.entrySet()) {
            sb.append("  ").append(" ").append("\t   \t").append("[").append(entry.getKey()).append("\t").append(entry.getValue()).append("]").append("\n");
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
