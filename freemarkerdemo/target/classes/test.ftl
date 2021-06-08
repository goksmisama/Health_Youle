<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="GBK">
    <title>Title</title>

</head>
<body>
<#--这是注释 -->
${name} ${message}
<br>

<#assign linkman="周"><br>
联系人：${linkman}<br>

<#assign info={"mobile":"13301231212",'address':'北京市昌平区王府街'}>
电话：${info.mobile} 地址: ${info.address}<br>

<#include "head.ftl">
<br>

<#if success=true>
    您已经通过验证了
<#else>
    未通过验证
</#if>

<br>
----商品表---
<#list goodslist as goods>
    ${goods_index} 名称${goods.name} 价格${goods.price}<br>
</#list>
共${goodslist?size} 条<br>

<#assign text="{'bank':'工商银行','account':'10101920201920212'}"/>
<#assign data=text?eval/>
开户行 ${data.bank} 账号 ${data.account}
<br>
当前日期${today?date}<br>
当前时间${today?time}<br>
当前日期和时间${today?datetime}<br>
日期格式化${today?string("yyyy年MM月dd日")}<br>

累计积分${point}<br>
积分没有分隔符
${point?c}<br>

判断是否存在
<#if aaa??>
    aaa变量存在
 <#else>
    aaa变量不存在
</#if>

<br>
${aaa!'-'}

</body>
</html>