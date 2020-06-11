        validatorRules: {
<#list columns as po>
  <#if po.isShow == 'Y'>
    <#if po.fieldName != 'id'>
          ${po.fieldName}: {rules: [
      <#assign fieldValidType = po.fieldValidType!''>
      <#-- 非空校验 -->
      <#if po.nullable == 'N' || fieldValidType == '*' || fieldValidType?contains('req')>
            {required: true, message: '请输入${po.filedComment}!'},
      </#if>
      <#-- 唯一校验 -->
      <#if fieldValidType?contains('only')>
            { validator: (rule, value, callback) => validateDuplicateValue('${tableName}', '${po.fieldDbName}', value, this.model.id, callback)},
      <#-- limit到max位字符串 -->
      <#elseif fieldValidType?contains('limit')&&fieldValidType?contains('max')>
          {pattern:/^.{${(fieldValidType?split(","))[1]},${(fieldValidType?split(","))[3]}}$/, message: '请输入${(fieldValidType?split(","))[1]}到${(fieldValidType?split(","))[3]}位任意字符!'},
      <#-- 大于limit位字符串 -->
      <#elseif fieldValidType?contains('limit')>
          {pattern:/^.{${(fieldValidType?split(","))[1]}},$/, message: '请输入大于${(fieldValidType?split(","))[1]}位任意字符!'},
      <#-- 小于max位字符串 -->
      <#elseif fieldValidType?contains('max')>
          {pattern:/^.{${(fieldValidType?split(","))[1]}}$/, message: '请输入小于${(fieldValidType?split(","))[1]}位任意字符!'},
      <#-- 网址 -->
      <#elseif fieldValidType?contains('url')>
            {pattern:/^(?:([A-Za-z]+):)?(\/{0,3})([0-9.\-A-Za-z]+)(?::(\d+))?(?:\/([^?#]*))?(?:\?([^#]*))?(?:#(.*))?$/, message: '请输入正确的网址!'},
      <#-- 电子邮件 -->
      <#elseif fieldValidType?contains('email')>
           {pattern:/^([\w]+\.*)([\w]+)@[\w]+\.\w{3}(\.\w{2}|)$/, message: '请输入正确的电子邮件!'},
      <#-- 手机号码 -->
      <#elseif fieldValidType?contains('tel')>
            {pattern:/^1[3456789]\d{9}$/, message: '请输入正确的手机号码!'},
      <#-- 邮政编码 -->
<#--      <#elseif fieldValidType?contains('p')>-->
<#--           {pattern:/^[1-9]\d{5}$/, message: '请输入正确的邮政编码!'},-->
      <#-- 字母 -->
      <#elseif fieldValidType?contains('char')>
            {pattern:/^[A-Z|a-z]+$/, message: '请输入字母!'},
      <#-- 数字 -->
<#--      <#elseif fieldValidType?contains('e')>-->
<#--           {pattern:/^-?\d+\.?\d*$/, message: '请输入数字!'},-->
      <#-- 整数 -->
      <#elseif fieldValidType?contains('num')>
            {pattern:/^-?\d+$/, message: '请输入整数!'},
      <#-- 金额 -->
      <#elseif fieldValidType?contains('money')>
           {pattern:/^(([1-9][0-9]*)|([0]\.\d{0,2}|[1-9][0-9]*\.\d{0,2}))$/, message: '请输入正确的金额!'},
      <#-- 身份证号码 -->
      <#elseif fieldValidType?contains('card')>
          {pattern:/^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入18位身份证号码!'},
      <#-- 无校验 -->
      <#else>
        <#t>
      </#if>
          ]},
    </#if>
  </#if>
</#list>
        },
