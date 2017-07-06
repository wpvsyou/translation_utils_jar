**# Auto translation Android resource strings.xml jar util.
peng.wang@pekall.com personal translation utils jar. create on 2017-07-05**

**## translation_util_jar 一个能批量自动翻译Android资源文件的java工具．**

自动翻译Android项目resource资源文件，方便快捷国际化。在项目根目录下运行如下命令查看效果

    gradle clean; gradle release; java -cp build/libs/translation_utils_jar-1.0.jar Main -f test.xml; cat out/strings.xml
    
使用帮助

    -f	--file	The input source file path that will be translation.
指定原文件路径，一次只能指定一个源文件。

    -h	--help	Get help information.
帮助

    -i	--info	Print the process debug logs.
打印脚本执行详情。

    -m	--mode	［batch］ is batch translation,Low accuracy but fast.
                ［single］ is translation one by one,Time consuming but accurate.
模式(模式1为批量翻译，将源文件解析成gson传，一次翻译多条，节省时间。
模式2为逐条翻译，每次只翻译一行，效率低但是准确率高。

    -s	 --src	Src code.
原文件语言编码，如若不知道请使用auto。

    -d	 --dst	Dst code.
将被翻译成的语言编码，不能是auto。
    
　　因为某些语种翻译后的返回值难于分片(因为批翻译是解析所有串的值用\\n来做分割合成一个大串在做翻译，此时返回值由于语种
不同，差异很大，导致代码不能准确定位到\\n分隔符去做分片，还原数据，导致数据丢失或者不准确)，或通过json还原，所以会强制
走模式single.强烈建议大家在使用时加上参数-m single,此模式会大大提高翻译准确率与成功率，只有中文简体/繁体之间的转换可
以用batch模式.

暂时只支持标准string字符串的翻译。格式如下:

    <string name="string_name">string</string>

支持的翻译语言范围如下：
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

to make the translation util jar.（使用gradle工具变异项目）

    "gradle release" 

show help information.(使用如下命令去获取jar包帮助信息)

    "java -cp build/libs/translation_utils_jar-1.0.jar Main -h"

声明,此项目不已盈利为目的，只为服务大众码农，代码有不足支出请指正。由于baiduAPI的限制，每月只能翻译低于200万个字符，否则将要收费。所以请各位自行更换代码中的key.

http://api.fanyi.baidu.com/api/trans/product/index

