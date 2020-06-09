package org.jeecg.modules.online.cgform.a;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.aspectj.util.FileUtil;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.ValidUtil;
import org.jeecg.modules.online.cgform.entity.OnlCgformField;
import org.jeecg.modules.online.cgform.entity.OnlCgformHead;
import org.jeecg.modules.online.cgform.model.OnlGenerateModel;
import org.jeecg.modules.online.cgform.service.IOnlCgformFieldService;
import org.jeecg.modules.online.cgform.service.IOnlCgformHeadService;
import org.jeecg.modules.online.cgform.service.IOnlCgformTableService;
import org.jeecg.modules.system.model.SysDepartTreeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping({"/online/cgform/apii"})
public class Aext extends a{
    @Autowired
    private IOnlCgformHeadService onlCgformHeadService;
    @Autowired
    private IOnlCgformFieldService onlCgformFieldService;
    @Autowired
    private IOnlCgformTableService onlCgformTableService;

    private static Properties props = null;
    public static String VALID_CODE = "//[formvalidcode]";

    private static Pattern linePattern = Pattern.compile("_(\\w)");


    static {
        try {
            Resource resource = new ClassPathResource("jeecg/jeecg_config.properties");
            InputStream is = resource.getInputStream();
            props = new Properties();
            props.load(new InputStreamReader(is, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Aext() {
        System.out.println("==========Aext无参构造=============");
    }

    @PostMapping({"/codeGenerate"})
    @RequiresRoles({"admin"})
    public Result<?> b(@RequestBody JSONObject var1) {
        OnlGenerateModel var2 = (OnlGenerateModel) JSONObject.parseObject(var1.toJSONString(), OnlGenerateModel.class);
        System.out.println("==========var1=============" + var1);

        try {
            if ("1".equals(var2.getJformType())) {
                this.onlCgformHeadService.generateCode(var2);
            } else {
                this.onlCgformHeadService.generateOneToMany(var2);
            }
            String tableName = var1.get("tableName").toString();
            System.out.println(tableName + "=======tableName======" + tableName);

            List<OnlCgformField> fileds = onlCgformFieldService.queryFormFieldsByTableName(tableName);
            if (fileds != null) {
                for (OnlCgformField filed : fileds) {
                    String filedValidType = filed.getFieldValidType();
                    String filedMustInput = filed.getFieldMustInput();

                    System.out.println(tableName + "=======filedValidType======" + filedValidType);
                    System.out.println(tableName + "=======filedMustInput======" + filedMustInput);
                }
            }
            String cpath = props.getProperty("controller_path");
            String entityPackage = var1.get("entityPackage").toString();
            String entityName = var1.get("entityName").toString();
            String controller = cpath + "\\" + entityPackage + "\\controller\\" + entityName + "Controller.java";
            String filename = FileUtil.readAsString(new File(controller));

            String bean = toLowerCaseFirstOne(entityName);
//            filename = filename.replace(VALID_CODE_ADD+"-1", "String result = ValidUtil.validForm(ValidUtil.ADD_METHOD, \""+tableName+"\", "+bean+");\r\n        if (StringUtils.isEmpty(result)){");
//            filename = filename.replace(VALID_CODE_ADD+"-2", "}else{\r\n            return Result.ok(result);\r\n    }");
//
//            filename = filename.replace(VALID_CODE_EDIT+"-1", "String result = ValidUtil.validForm(ValidUtil.EDIT_METHOD, \""+tableName+"\", "+bean+");\r\n        if (StringUtils.isEmpty(result)){");
//            filename = filename.replace(VALID_CODE_EDIT+"-2", "}else{\r\n            return Result.ok(result);\r\n    }");

            String addValidcode = makeValidCode(fileds, entityName);
            filename = filename.replace(VALID_CODE, addValidcode);

            FileUtil.writeAsString(new File(controller), filename);
            return Result.ok(1);
        } catch (Exception var4) {
            var4.printStackTrace();
            return Result.error(var4.getMessage());
        }
    }

    //首字母转小写
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    //首字母转大
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    /**
     * 下划线转驼峰
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String makeValidCode(List<OnlCgformField> fileds, String entityName) {
        StringBuffer sb = new StringBuffer();
        sb.append("String result = null;\r\n");
        if (fileds != null) {
            for (OnlCgformField filed : fileds) {
                String format = filed.getFieldValidType();
                String dbFieldTxt = filed.getDbFieldTxt();
                String dbFieldName = filed.getDbFieldName();
                String mustinput = filed.getFieldMustInput();
                if ("1".equals(mustinput) && !"*".equals(format)) {   //必填也就是非空
                    String method = toLowerCaseFirstOne(entityName) + ".get" + toUpperCaseFirstOne(lineToHump(dbFieldName)) + "()";
                    sb.append("\t   result = ValidUtil.valid(" + method + ", \"" + dbFieldTxt + "\", \"*\");\r\n");
                    sb.append("\t   if(!StringUtils.isEmpty(result))\r\n");
                    sb.append("\t   {\r\n");
                    sb.append("     \t   return Result.error(ValidUtil.FORMAT_WR_CODE, result);\r\n");
                    sb.append("\t   }\r\n");
                }

                if (!StringUtils.isEmpty(format)) {
                    String method = toLowerCaseFirstOne(entityName) + ".get" + toUpperCaseFirstOne(lineToHump(dbFieldName)) + "()";
                    sb.append("\t   result = ValidUtil.valid(" + method + ", \"" + dbFieldTxt + "\", \"" + format + "\");\r\n");
                    sb.append("\t   if(!StringUtils.isEmpty(result))\r\n");
                    sb.append("\t   {\r\n");
                    sb.append("     \t   return Result.error(ValidUtil.FORMAT_WR_CODE, result);\r\n");
                    sb.append("\t   }\r\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 根据数据库表自动生成单表增删改查代码
     */
    public void runB() {
        List<Map> tables = onlCgformTableService.getTabletList();
        System.out.println("========数据库中的Tables:" + tables);
        // 循环所有表，从表中获取相关数据，组织数据，调用生成代码的接口
        /*
          // jeecg-boot生成代码后端接口(online/cgform/apii/codeGenerate)所要数据
          {
            "projectPath": "d:\\workspace-ui",
            "jspMode": "one",
            "ftlDescription": "测试用户表",
            "jformType": "1",
            "tableName_tmp": "test_demo",
            "entityName": "TestDemo",
            "entityPackage": "test_package",
            "packageStyle": "service",
            "codeTypes": "controller,service,dao,mapper,entity,vue",
            "code": "d35109c3632c4952a19ecc094943dd71",
            "tableName": "test_demo"
          }
         */
        // mysql 代码块 --开始
        tables.forEach(table -> {
            //组织数据并调用
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("projectPath",props.getProperty("project_path")); // 生成代码存储位置
            jsonObject.put("jspMode","one");  // 单表
            jsonObject.put("ftlDescription",table.get("TABLE_COMMENT")); // 表描述
            jsonObject.put("jformType","1");
            jsonObject.put("tableName_tmp",table.get("TABLE_NAME")); // 表名
            jsonObject.put("entityName",toUpperCaseFirstOne(lineToHump((String) table.get("TABLE_NAME")))); // 实体名称
            jsonObject.put("entityPackage",""); // 包名
            jsonObject.put("packageStyle","service"); // 业务分层
            jsonObject.put("codeTypes","controller,service,dao,mapper,entity,vue"); // 要生成的模板
            jsonObject.put("code","d35109c3632c4952a19ecc094943dd71"); // 要生成的模板
            jsonObject.put("tableName",table.get("TABLE_NAME")); // 表名
            // 调用接口
            b(jsonObject);
        });
        // mysql 代码块 --结束
    }

    public static void main(String[] args) {
        String a = toUpperCaseFirstOne(lineToHump("abv_Mdy"));
        System.out.println(a);
    }

}