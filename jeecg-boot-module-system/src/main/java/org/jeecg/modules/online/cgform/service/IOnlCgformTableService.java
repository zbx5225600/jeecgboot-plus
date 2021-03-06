package org.jeecg.modules.online.cgform.service;


import java.util.List;
import java.util.Map;

public interface IOnlCgformTableService {
    public List<Map> getTabletList();
    public List<Map> listTableColumn(String tableName);
    public String getOnlCgformHeadByTableName(String tableName);
    public void deleteAllHeadAndField();
    public List<String> getPRIType(String tableName);
}
