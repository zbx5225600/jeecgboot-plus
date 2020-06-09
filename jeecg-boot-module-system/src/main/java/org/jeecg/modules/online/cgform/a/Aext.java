package org.jeecg.modules.online.cgform.a;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.aspectj.util.FileUtil;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.ValidUtil;
import org.jeecg.modules.online.cgform.entity.OnlCgformField;
import org.jeecg.modules.online.cgform.entity.OnlCgformHead;
import org.jeecg.modules.online.cgform.entity.OnlCgformIndex;
import org.jeecg.modules.online.cgform.model.OnlGenerateModel;
import org.jeecg.modules.online.cgform.service.IOnlCgformFieldService;
import org.jeecg.modules.online.cgform.service.IOnlCgformHeadService;
import org.jeecg.modules.online.cgform.service.IOnlCgformTableService;
import org.jeecg.modules.online.config.util.d;
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
import java.math.BigInteger;
import java.util.ArrayList;
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

    public String makeValidCode(List<OnlCgformField> fileds, String entityName) {
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
        ArrayList list = new ArrayList();
        tables.forEach(table -> {
            // 调用接口给数据库插入表创建信息
            String code = runA(table);
            if(!code.startsWith("数据库")){
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
                jsonObject.put("code",code); // 要生成的模板
                jsonObject.put("tableName",table.get("TABLE_NAME")); // 表名
                // 调用接口
                b(jsonObject);
            }else{
                list.add(code);
            }
        });
        list.forEach(l->{
            System.out.println(l);
        });
        // mysql 代码块 --结束
    }

    private String runA(Map table){
        // 1、调用创建addAll接口,设置表信息
        //组织数据
        /*
             {
                 deleteFieldIds: [],
                 deleteIndexIds: [],
                 fields: [
                    {
                        dbDefaultVal: ""
                        dbFieldName: "id"
                        dbFieldTxt: "主键"
                        dbIsKey: "1"
                        dbIsNull: "0"
                        dbLength: "36"
                        dbPointLength: "0"
                        dbType: "string"
                        dictField: ""
                        dictTable: ""
                        dictText: ""
                        fieldExtendJson: ""
                        fieldHref: ""
                        fieldLength: "120"
                        fieldMustInput: "0"
                        fieldShowType: "text"
                        fieldValueRuleCode: ""
                        id: "15916930484041"
                        isQuery: "0"
                        isReadOnly: "1"
                        isShowForm: "0"
                        isShowList: "0"
                        mainField: ""
                        mainTable: ""
                        orderNum: "1"
                        order_num: 0
                        queryMode: "single"
                    }
                 ],
                 head:{
                    formCategory: "bdfl_ptbd"
                    formTemplate: "99"
                    idType: "UUID"
                    isCheckbox: "Y"
                    isPage: "Y"
                    isTree: "N"
                    queryMode: "single"
                    scroll: 0
                    tableName: "ceshi"
                    tableTxt: "测试"
                    tableType: 1
                    themeTemplate: "normal"
                    },
                 indexs: []
             }
         */
        org.jeecg.modules.online.cgform.model.b b = new org.jeecg.modules.online.cgform.model.b();
        List<OnlCgformIndex> onlCgformindexs = new ArrayList<OnlCgformIndex>(); // 表信息
        b.setIndexs(onlCgformindexs);
        OnlCgformHead onlCgformHead = new OnlCgformHead(); // 表信息
        onlCgformHead.setFormCategory("bdfl_ptbd"); //
        onlCgformHead.setFormTemplate("99"); // form表单模板
        onlCgformHead.setIdType("uuid");  // 主键生成策略
        onlCgformHead.setIsCheckbox("y"); //
        onlCgformHead.setIsPage("y");  // 分页
        onlCgformHead.setIsTree("n"); //  树结构
        onlCgformHead.setQueryMode("single");
        onlCgformHead.setScroll(0); // 滚动条
        onlCgformHead.setTableName((String) table.get("TABLE_NAME")); // 表名
        onlCgformHead.setTableTxt((String) table.get("TABLE_COMMENT")); // 表描述
        onlCgformHead.setTableType(1); // 表类型
        onlCgformHead.setThemeTemplate("normal");   // 模板主题
        b.setHead(onlCgformHead);

        // 查询表的字段信息

        List<OnlCgformField> list = new ArrayList<>(); // 列信息
        List<Map> columns = onlCgformTableService.listTableColumn((String) table.get("TABLE_NAME"));
        columns.forEach(column->{
            OnlCgformField object = new OnlCgformField();
            object.setDbFieldName((String)column.get("COLUMN_NAME"));
            object.setDbDefaultVal((String)column.get("COLUMN_DEFAULT"));
            object.setDbFieldTxt((String)column.get("COLUMN_COMMENT"));
            object.setDbIsKey(column.get("COLUMN_KEY")=="PRI"?1:0);
            object.setDbIsNull(column.get("IS_NULLABLE")=="YES"?1:0);
            int db_length = 99;
            if(column.get("CHARACTER_MAXIMUM_LENGTH")!=null){
                if((new BigInteger(column.get("CHARACTER_MAXIMUM_LENGTH").toString())).compareTo(new BigInteger("2147483647"))==1){
                    db_length = 2147483647;
                }else{
                    db_length = Integer.valueOf(column.get("CHARACTER_MAXIMUM_LENGTH").toString());
                }
            }else {
                db_length = 2147483647;
            }

            object.setDbLength(db_length);
            object.setDbType((String) column.get("DATA_TYPE"));
            object.setFieldLength(db_length);
            object.setFieldMustInput("0");
            object.setFieldShowType("text"); // text,date
            object.setIsQuery(column.get("COLUMN_KEY")=="PRI"?0:1);
            object.setIsReadOnly(column.get("COLUMN_KEY")=="PRI"?1:0);
            object.setIsShowForm(column.get("COLUMN_KEY")=="PRI"?0:1);
            object.setIsShowList(column.get("COLUMN_KEY")=="PRI"?0:1);
            object.setOrderNum(column.get("COLUMN_KEY")=="PRI"?1:0);
            object.setQueryMode("single");
//            dictField: ""
//            dictTable: ""
//            dictText: ""
//            fieldExtendJson: ""
//            fieldHref: ""
//            fieldValueRuleCode: ""
//            id: "15916930484041"
//            mainField: ""
//            mainTable: ""
            list.add(object);
        });
        b.setFields(list);

        String var2 = b.getHead().getTableName();
//          if(d.a(var2)) {
//              return "数据库表[" + var2 + "]已存在";
//          }else{
              this.onlCgformHeadService.addAll(b);
              return onlCgformTableService.getOnlCgformHeadByTableName((String) table.get("TABLE_NAME"));
//          }
    }

    public static void main(String[] args) {
        String a = toUpperCaseFirstOne(lineToHump("abv_Mdy"));
        System.out.println(a);
    }

}