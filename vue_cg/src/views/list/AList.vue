<template>
    <a-card :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
                <a-row :gutter="24">
                    <a-col :xl="10" :lg="11" :md="12" :sm="24">
                        <a-form-item label="年龄">
                            <a-input placeholder="请输入最小值" class="query-group-cust" v-model="queryParam.testAge_begin"></a-input>
                            <span class="query-group-split-cust"></span>
                            <a-input placeholder="请输入最大值" class="query-group-cust" v-model="queryParam.testAge_end"></a-input>
                        </a-form-item>
                    </a-col>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
                        <a-form-item label="名字">
                            <a-input placeholder="请输入名字" v-model="queryParam.testName"></a-input>
                        </a-form-item>
                    </a-col>
                    <template v-if="toggleSearchStatus">
                        <a-col :xl="10" :lg="11" :md="12" :sm="24">
                            <a-form-item label="时间">
                                <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.testTime_begin"></j-date>
                                <span class="query-group-split-cust"></span>
                                <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.testTime_end"></j-date>
                            </a-form-item>
                        </a-col>
                        <a-col :xl="10" :lg="11" :md="12" :sm="24">
                            <a-form-item label="日期">
                                <j-date placeholder="请选择开始日期" class="query-group-cust" v-model="queryParam.testDate_begin"></j-date>
                                <span class="query-group-split-cust"></span>
                                <j-date placeholder="请选择结束日期" class="query-group-cust" v-model="queryParam.testDate_end"></j-date>
                            </a-form-item>
                        </a-col>
                    </template>
                    <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
                    </a-col>

                </a-row>
            </a-form>
        </div>
        <!-- 查询区域-END -->

        <!-- 操作按钮区域 -->
        <div class="table-operator">
            <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
            <a-button type="primary" icon="download" @click="handleExportXls('')">导出</a-button>
            <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
                <a-button type="primary" icon="import">导入</a-button>
            </a-upload>
            <a-dropdown v-if="selectedRowKeys.length > 0">
                <a-menu slot="overlay">
                    <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
                </a-menu>
                <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
            </a-dropdown>
        </div>

        <!-- table区域-begin -->
        <div>
            <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
                <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
                <a style="margin-left: 24px" @click="onClearSelected">清空</a>
            </div>

            <a-table
                    ref="table"
                    size="middle"
                    bordered
                    rowKey="id"
                    :columns="columns"
                    :dataSource="dataSource"
                    :pagination="ipagination"
                    :loading="loading"
                    :rowSelection="{fixed:true,selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"

                    @change="handleTableChange">

                <template slot="htmlSlot" slot-scope="text">
                    <div v-html="text"></div>
                </template>
                <template slot="imgSlot" slot-scope="text">
                    <span v-if="!text" style="font-size: 12px;font-style: italic;">无此图片</span>
                    <img v-else :src="getImgView(text)" height="25px" alt="图片不存在" style="max-width:80px;font-size: 12px;font-style: italic;"/>
                </template>
                <template slot="fileSlot" slot-scope="text">
                    <span v-if="!text" style="font-size: 12px;font-style: italic;">无此文件</span>
                    <a-button
                            v-else
                            :ghost="true"
                            type="primary"
                            icon="download"
                            size="small"
                            @click="uploadFile(text)">
                        下载
                    </a-button>
                </template>

                <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a>删除</a>
          </a-popconfirm>
        </span>

            </a-table>
        </div>

        <test-modal ref="modalForm" @ok="modalFormOk"></test-modal>
    </a-card>
</template>

<script>

    import { JeecgListMixin } from '@/mixins/JeecgListMixin'
    import TestModal from './modules/TestModal'
    import JDate from '@/components/jeecg/JDate.vue'
    import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'

    export default {
        name: "AList",
        mixins:[JeecgListMixin],
        components: {
            JDate,
            TestModal
        },
        data () {
            return {
                description: '管理页面',
                // 表头
                columns: [
                    {
                        title: '序号',
                        dataIndex: '',
                        key:'rowIndex',
                        width:60,
                        align:"center",
                        customRender:function (t,r,index) {
                            return parseInt(index)+1;
                        }
                    },
                    {
                        title:'年龄',
                        align:"center",
                        dataIndex: 'testAge'
                    },
                    {
                        title:'图片',
                        align:"center",
                        dataIndex: 'testImg',
                        scopedSlots: {customRender: 'imgSlot'}
                    },
                    {
                        title:'多选',
                        align:"center",
                        dataIndex: 'testCheckbox_dictText'
                    },
                    {
                        title:'名字',
                        align:"center",
                        dataIndex: 'testName'
                    },
                    {
                        title:'富文本',
                        align:"center",
                        dataIndex: 'testUme',
                        scopedSlots: {customRender: 'htmlSlot'}
                    },
                    {
                        title:'开关',
                        align:"center",
                        dataIndex: 'testSwitch'
                    },
                    {
                        title:'密码',
                        align:"center",
                        dataIndex: 'testPassword'
                    },
                    {
                        title:'邮件',
                        align:"center",
                        dataIndex: 'testEmail'
                    },
                    {
                        title:'网址',
                        align:"center",
                        dataIndex: 'testUrl'
                    },
                    {
                        title:'时间',
                        align:"center",
                        dataIndex: 'testTime'
                    },
                    {
                        title:'下拉选',
                        align:"center",
                        dataIndex: 'testSels_dictText'
                    },
                    {
                        title:'多行文本',
                        align:"center",
                        dataIndex: 'testTxta'
                    },
                    {
                        title:'日期',
                        align:"center",
                        dataIndex: 'testDate',
                        customRender:function (text) {
                            return !text?"":(text.length>10?text.substr(0,10):text)
                        }
                    },
                    {
                        title:'下拉多选',
                        align:"center",
                        dataIndex: 'testSelm_dictText'
                    },
                    {
                        title:'文件',
                        align:"center",
                        dataIndex: 'testFile',
                        scopedSlots: {customRender: 'fileSlot'}
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        align:"center",
                        scopedSlots: { customRender: 'action' }
                    }
                ],
                url: {
                    list: "/test/test/list",
                    delete: "/test/test/delete",
                    deleteBatch: "/test/test/deleteBatch",
                    exportXlsUrl: "/test/test/exportXls",
                    importExcelUrl: "test/test/importExcel",
                },
                dictOptions:{},
            }
        },
        computed: {
            importExcelUrl: function(){
                return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
            }
        },
        methods: {
            initDictConfig(){
            }
        }
    }
</script>
<style scoped>
    @import '~@assets/less/common.less';
</style>
