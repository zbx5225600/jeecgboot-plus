package org.jeecg.common.util;

import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.online.cgform.entity.OnlCgformField;

import java.util.List;

/**
 * @Description: TODO
 * @author: wwl
 * @date: 2020年05月04日 20:36
 */
public final class ValidUtil {

    public static final String ADD_METHOD = "add";
    public static final String EDIT_METHOD = "edit";

    public static final int MUSTINPUT_WR_CODE = 301;
    public static final int FORMAT_WR_CODE = 302;

    /*
    {title:"空",value:""},
    {title:"唯一校验",value:"only"},
    {title:"6到16位数字",value:"n6-16"},
    {title:"6到16位任意字符",value:"*6-16"},
    {title:"网址",value:"url"},
    {title:"电子邮件",value:"e"},
    {title:"手机号码",value:"m"},
    {title:"邮政编码",value:"p"},
    {title:"字母",value:"s"},
    {title:"数字",value:"n"},
    {title:"整数",value:"z"},
    {title:"非空",value:"*"},
    {title:"6到18位字符串",value:"s6-18"},
    {title:"金额",value:"money"}*/

    /**
     * form校验
     * @param method   add 新增  edit 编辑
     * @param tableName
     * @param bean
     * @return
     */
    public static final String validForm(String method, String tableName, Object bean){
        return null;
    }

    /**
     * 数据格式校验
     * @param value 值
     * @param filedName 属性中文名称
     * @param format 需要校验格式
     * @return
     */
    public static final String valid(Object value, String filedName, String format){
        switch(format){
            case "" : { //{title:"空",value:""},
                return null;
            }
            case "only" : { //{title:"唯一校验",value:"only"},
                return null;
            }
            case "n6-16" : {//{title:"6到16位数字",value:"n6-16"},
                return null;
            }
            case "*6-16" : {//{title:"6到16位任意字符",value:"*6-16"},
                return null;
            }
            case "url" : {//{title:"网址",value:"url"},
                return null;
            }
            case "e" : {//{title:"电子邮件",value:"e"},
                return null;
            }
            case "m" : {//{title:"手机号码",value:"m"},
                return null;
            }
            case "p" : {//{title:"邮政编码",value:"p"},
                return null;
            }
            case "s" : {//{title:"字母",value:"s"},
                return null;
            }
            case "n" : {//{title:"数字",value:"n"},
                return null;
            }
            case "z" : {//{title:"整数",value:"z"},
                return null;
            }
            case "*" : {//{title:"非空",value:"*"},
                if(null == value || StringUtils.isEmpty(value.toString()))
                {
                    return filedName+"不能为空";
                }
                return null;
            }
            case "s6-18" : {//{title:"6到18位字符串",value:"s6-18"},
                if(null != value && !StringUtils.isEmpty(value.toString()))
                {
                    int len = value.toString().length();
                    if (len < 16 || len > 18)
                    {
                        return filedName+"只能是6到18位字符串";
                    }
                }
                return null;
            }
            case "money" : {//{title:"金额",value:"money"}*/
                return null;
            }
        }
        return null;
    }

}
