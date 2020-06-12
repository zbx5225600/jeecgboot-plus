package org.jeecg.modules.online.cgform.a;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.aspectj.util.FileUtil;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.online.cgform.entity.OnlCgformField;
import org.jeecg.modules.online.cgform.entity.OnlCgformHead;
import org.jeecg.modules.online.cgform.entity.OnlCgformIndex;
import org.jeecg.modules.online.cgform.model.OnlGenerateModel;
import org.jeecg.modules.online.cgform.service.IOnlCgformFieldService;
import org.jeecg.modules.online.cgform.service.IOnlCgformHeadService;
import org.jeecg.modules.online.cgform.service.IOnlCgformTableService;
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
public class Aext extends a {
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
                if ("1".equals(mustinput)) {   //必填也就是非空
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
        // 生成器运行前初始化，将onl_cgform_head和onl_cgform_field清空，不请空，如果表中包含这个表名，则会报重复的key
        onlCgformTableService.deleteAllHeadAndField();
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
        JSONArray jsonArray = new JSONArray();
        tables.forEach(table -> {
            if(((String) table.get("TABLE_NAME")).length()<=1){
                throw new RuntimeException((String) table.get("TABLE_NAME")+"表名为单个字母，生成器不支持单个字母表名，请修改！");
            }
            // 调用接口给数据库插入表创建信息
            JSONObject result = runA(table);
            if (result.get("code") != null) {
                //组织数据并调用
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("projectPath", props.getProperty("project_path")); // 生成代码存储位置
                jsonObject.put("jspMode", "one");  // 单表
                jsonObject.put("ftlDescription", table.get("TABLE_COMMENT")); // 表描述
                jsonObject.put("jformType", "1");
                jsonObject.put("tableName_tmp", table.get("TABLE_NAME")); // 表名
                jsonObject.put("entityName", toUpperCaseFirstOne(lineToHump((String) table.get("TABLE_NAME")))); // 实体名称
                jsonObject.put("entityPackage", props.getProperty("entity_package")); // 包名
                jsonObject.put("packageStyle", "service"); // 业务分层
                jsonObject.put("codeTypes", "controller,service,dao,mapper,entity,vue"); // 要生成的模板
                jsonObject.put("code", result.get("code")); // 要生成的模板
                jsonObject.put("tableName", table.get("TABLE_NAME")); // 表名
                // 调用接口
                b(jsonObject);
            }
            jsonArray.addAll((JSONArray) result.get("message"));
        });
        jsonArray.forEach(l -> {
            System.err.println(l);
        });
        // mysql 代码块 --结束

    }

    private JSONObject runA(Map table) {
        JSONObject jsonObject = new JSONObject();
        // 查询表的字段信息
        JSONArray jsonArray = new JSONArray(); // 错误信息记录
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
        onlCgformHead.setFormCategory("bdfl_include"); // 导入表单
        onlCgformHead.setFormTemplate("99"); // form表单模板
        List<String> pRITypes = onlCgformTableService.getPRIType((String) table.get("TABLE_NAME"));
        if (pRITypes.size()!=1){
            jsonArray.add("=========================表"+(String) table.get("TABLE_NAME")+"有两个主键，生成器默认以第一个为主键生成策略");
        }
        onlCgformHead.setIdType(pRITypes.size()>0?("varchar".equals(pRITypes.get(0))?"UUID":"NATIVE"):"NATIVE");  // 主键生成策略
        onlCgformHead.setIsCheckbox("Y"); //
        onlCgformHead.setIsPage("Y");  // 分页
        onlCgformHead.setIsTree("N"); //  树结构
        onlCgformHead.setQueryMode("single");
        onlCgformHead.setScroll(0); // 滚动条
        onlCgformHead.setTableName((String) table.get("TABLE_NAME")); // 表名
        onlCgformHead.setTableTxt((String) table.get("TABLE_COMMENT")); // 表描述
        onlCgformHead.setTableType(1); // 表类型
        onlCgformHead.setThemeTemplate("normal");   // 模板主题
        b.setHead(onlCgformHead);

        List<OnlCgformField> list = new ArrayList<>(); // 列信息
        List<Map> columns = onlCgformTableService.listTableColumn((String) table.get("TABLE_NAME"));
        columns.forEach(column -> {
            String[] column_comments = ((String) column.get("COLUMN_COMMENT")).split("-");
            if (column_comments.length != 8) {
                jsonArray.add((String) table.get("TABLE_NAME") + "表的字段" + (String) column.get("COLUMN_NAME") + "的注释长度必须包含8个-分隔符，请检查!");
                return;
            }
            OnlCgformField object = new OnlCgformField();
            object.setDbFieldName((String) column.get("COLUMN_NAME"));
            object.setDbDefaultVal((String) column.get("COLUMN_DEFAULT"));
            object.setDbFieldTxt(column_comments[1]);
            object.setDbIsKey("PRI".equals(column.get("COLUMN_KEY")) ? 1 : 0);
            object.setDbIsNull(column_comments[3].contains("req") ? 1 : 0);
            // db_length后期需要优化
            int db_length = 99;
            if (column.get("CHARACTER_MAXIMUM_LENGTH") != null) {
                if ((new BigInteger(column.get("CHARACTER_MAXIMUM_LENGTH").toString())).compareTo(new BigInteger("2147483647")) == 1) {
                    db_length = 2147483647;
                } else {
                    db_length = Integer.valueOf(column.get("CHARACTER_MAXIMUM_LENGTH").toString());
                }
            } else {
                db_length = 2147483647;
            }
            // 说明(必填)0-名称(必填)1-控件类型(必填)2-验证规则(必填)3-Dict4-查询5-新增6-列表7
            object.setDbLength(db_length);
            object.setDbType((String) column.get("DATA_TYPE"));
            object.setFieldLength(db_length);
            object.setDbIsNull("YES".equals((String)column.get("IS_NULLABLE"))?1:0);
            object.setFieldMustInput(column_comments.length > 3 && column_comments[3].contains("req") ? "1" : "0");
            object.setFieldShowType(fieldShowTypeEscape(column_comments.length > 2 ? column_comments[2] : "text")); // 字段显示类型
            object.setIsQuery("PRI".equals(column.get("COLUMN_KEY")) ? 0 : isQueryEscape(column_comments.length > 5 ? column_comments[5] : "null"));
            object.setIsReadOnly("PRI".equals(column.get("COLUMN_KEY")) ? 1 : 0);
            object.setIsShowForm("PRI".equals(column.get("COLUMN_KEY")) ? 0 : isShowFormEscape(column_comments.length > 6 ? column_comments[6] : "null"));
            object.setIsShowList("PRI".equals(column.get("COLUMN_KEY")) ? 0 : isShowListEscape(column_comments.length > 7 ? column_comments[7] : "null"));
            object.setOrderNum("PRI".equals(column.get("COLUMN_KEY")) ? 1 : 0);
            object.setQueryMode(column_comments.length > 5 && "searplus".equals(column_comments[5]) ? "" : "single");
            object.setDictTable(column_comments.length > 4 && !"null".equals(column_comments[4]) ? column_comments[4].split(",")[0] : "");
            object.setDictField(column_comments.length > 4 && !"null".equals(column_comments[4]) && column_comments[4].split(",").length == 3 ? column_comments[4].split(",")[2] : "");
            object.setDictText(column_comments.length > 4 && !"null".equals(column_comments[4]) && column_comments[4].split(",").length >= 2 ? column_comments[4].split(",")[1] : "");
            object.setFieldValidType(fieldValidTypeCheck(column_comments.length > 3 ? column_comments[3] : ""));
//            fieldExtendJson: ""
//            fieldHref: ""
//            mainField: ""
//            mainTable: ""
            list.add(object);
        });
        if (list.size() > 0) {
            b.setFields(list);
            this.onlCgformHeadService.addAll(b);
            jsonObject.put("code", onlCgformTableService.getOnlCgformHeadByTableName((String) table.get("TABLE_NAME")));
        }
        jsonObject.put("message", jsonArray);
        return jsonObject;

    }

    private String fieldValidTypeCheck(String fieldValueRuleCode) {
        String temp = "";
        String[] arr = fieldValueRuleCode.split(",");
        if (arr.length == 1) {
            temp = arr[0];
        } else {
            for (String s : arr) {
                if (!"req".equals(s)) {
                    temp += s+",";
                }
            }
        }
        return temp;
    }

    /**
     * 为了后端生成代码和前端生成代码兼容，字段显示类型需调用转义方法转以后使用
     *
     * @param fieldShowType 字段显示类型
     * @return 返回转义后的字符串
     */
    private String fieldShowTypeEscape(String fieldShowType) {
        switch (fieldShowType) {
            case "txt":
                fieldShowType = "text";
                break;
            case "pwd":
                fieldShowType = "password";
                break;
            case "rad":
                fieldShowType = "radio";
                break;
            case "sels":
                fieldShowType = "sel_search";
                break;
            case "selm":
                fieldShowType = "list_multi";
                break;
            case "chb":
                fieldShowType = "checkbox";
                break;
            case "time":
                fieldShowType = "datetime";
                break;
            case "img":
                fieldShowType = "image";
                break;
            case "txta":
                fieldShowType = "textarea";
                break;
            case "ume":
                fieldShowType = "umeditor";
                break;
        }
        return fieldShowType;
    }

    /**
     * 查询 标识转义
     *
     * @param isQuery 查询标识
     * @return 返回转义后的字符串
     */
    private int isQueryEscape(String isQuery) {
        int temp = 1;
        switch (isQuery) {
            case "sear":
                temp = 1;
                break;
            case "nosea":
                temp = 0;
                break;
            case "searplus":
                temp = 1;
                break;
            default:
                temp = 1;
        }
        return temp;
    }

    /**
     * 新增 标识转义
     *
     * @param isShowForm 新增标识
     * @return 返回转义后的字符串
     */
    private int isShowFormEscape(String isShowForm) {
        int temp = 1;
        switch (isShowForm) {
            case "add":
                temp = 1;
                break;
            case "noadd":
                temp = 0;
                break;
            default:
                temp = 1;
        }
        return temp;
    }

    /**
     * 列表 标识转义
     *
     * @param isShowList 列表标识
     * @return 返回转义后的字符串
     */
    private int isShowListEscape(String isShowList) {
        int temp = 1;
        switch (isShowList) {
            case "list":
                temp = 1;
                break;
            case "nolist":
                temp = 0;
                break;
            default:
                temp = 1;
        }
        return temp;
    }

    public static void main(String[] args) {
        String a = toUpperCaseFirstOne(lineToHump("abv_Mdy"));
        System.out.println(a);
    }

}