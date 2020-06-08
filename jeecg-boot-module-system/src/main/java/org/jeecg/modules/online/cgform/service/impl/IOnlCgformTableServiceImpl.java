package org.jeecg.modules.online.cgform.service.impl;
import org.jeecg.modules.online.cgform.mapper.TableMapper;
import org.jeecg.modules.online.cgform.service.IOnlCgformTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("onlCgformTableService")
public  class IOnlCgformTableServiceImpl implements IOnlCgformTableService {

    @Autowired
    private TableMapper mapper;

    /**
     * 获取当前数据库下所有表信息
     * @return 返回所有表信息
     */
    public List<Map> getTabletList(){
        return this.mapper.listTable();
    }

    /**
     * 获取表下所有字段的所有
     * @param tableName 表名
     * @return 返回所有字段信息
     */
    public List<Map> listTableColumn(String tableName){
        return this.mapper.listTableColumn(tableName);
    }

}
