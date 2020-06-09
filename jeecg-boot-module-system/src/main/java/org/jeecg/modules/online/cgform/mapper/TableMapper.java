package org.jeecg.modules.online.cgform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.online.cgform.entity.OnlCgformHead;

import java.util.List;
import java.util.Map;

public interface TableMapper extends BaseMapper {
    @Select("select * from information_schema.TABLES where TABLE_SCHEMA=(select database())")
    List<Map> listTable();

    @Select("select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database()) and TABLE_NAME=#{tableName}")
    List<Map> listTableColumn(String tableName);

    @Select("select id from onl_cgform_head where table_name=#{tableName}")
    String getOnlCgformHeadByTableName(String tableName);
}