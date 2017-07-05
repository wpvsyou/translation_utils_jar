# Auto translation Adnroid resource strings.xml jar util.
######peng.wang@pekall.com persional translation utils jar. create on 2017-07-05

###自动翻译Android项目resource资源文件，方便快捷国际化。在项目根目录下运行如下命令查看效果

#请使用dev_1分支。

    java -cp build/libs/btrsn-1.0.jar Main -f test.xml; cat out/strings.xml

###暂时只支持标准string字符串的翻译。格式如下:

    <string name="string_name">string</string>

###支持的翻译语言范围如下：
<div class="second-wrap twocolumn">
<table class="info-table">
<tr>
<th>语言简写</th>
<th>名称</th>
</tr>
<tr>
<td>auto</td>
<td>自动检测</td>
</tr>
<tr>
<td>zh</td>
<td>中文</td>
</tr>
<tr>
<td>en</td>
<td>英语</td>
</tr>
<tr>
<td>yue</td>
<td>粤语</td>
</tr>
<tr>
<td>wyw</td>
<td>文言文</td>
</tr>
<tr>
<td>jp</td>
<td>日语</td>
</tr>
<tr>
<td>kor</td>
<td>韩语</td>
</tr>
<tr>
<td>fra</td>
<td>法语</td>
</tr>
<tr>
<td>spa</td>
<td>西班牙语</td>
</tr>
<tr>
<td>th</td>
<td>泰语</td>
</tr>
<tr>
<td>ara</td>
<td>阿拉伯语</td>
</tr>
<tr>
<td>ru</td>
<td>俄语</td>
</tr>
<tr>
<td>pt</td>
<td>葡萄牙语</td>
</tr>
<tr>
<td>de</td>
<td>德语</td>
</tr>
<tr>
<td>it</td>
<td>意大利语</td>
</tr>
<tr>
<td>el</td>
<td>希腊语</td>
</tr>
<tr>
<td>nl</td>
<td>荷兰语</td>
</tr>
<tr>
<td>pl</td>
<td>波兰语</td>
</tr>
<tr>
<td>bul</td>
<td>保加利亚语</td>
</tr>
<tr>
<td>est</td>
<td>爱沙尼亚语</td>
</tr>
<tr>
<td>dan</td>
<td>丹麦语</td>
</tr>
<tr>
<td>fin</td>
<td>芬兰语</td>
</tr>
<tr>
<td>cs</td>
<td>捷克语</td>
</tr>
<tr>
<td>rom</td>
<td>罗马尼亚语</td>
</tr>
<tr>
<td>slo</td>
<td>斯洛文尼亚语</td>
</tr>
<tr>
<td>swe</td>
<td>瑞典语</td>
</tr>
<tr>
<td>hu</td>
<td>匈牙利语</td>
</tr>
<tr>
<td>cht</td>
<td>繁体中文</td>
</tr>
<tr>
<td>vie</td>
<td>越南语</td>
</tr>
</table>
</div>
<div class="list-title" id="allDemos">

###to make the translation util jar.（使用gradle工具变异项目）

    "gradle release" 

###show help information.(使用如下命令去获取jar包帮助信息)

    "java -cp build/libs/btrsh-1.0.jar Main -h"

###声明,此项目不已盈利为目的，只为服务大众码农，代码有不足支出请指正。由于baiduAPI的限制，每月只能翻译低于200万个字符，否则将要收费。所以请各位自行更换代码中的key.
http://api.fanyi.baidu.com/api/trans/product/index

