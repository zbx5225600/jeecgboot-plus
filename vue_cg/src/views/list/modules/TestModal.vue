<template>
    <a-modal
            :title="title"
            :width="width"
            :visible="visible"
            :confirmLoading="confirmLoading"
            @ok="handleOk"
            @cancel="handleCancel"
            cancelText="关闭">
        <a-spin :spinning="confirmLoading">
            <a-form :form="form">

                <a-form-item label="年龄" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input-number v-decorator="[ 'testAge', validatorRules.testAge]" placeholder="请输入年龄" style="width: 100%"/>
                </a-form-item>
                <a-form-item label="图片" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <j-upload v-decorator="['testImg', validatorRules.testImg]" :trigger-change="true"></j-upload>
                </a-form-item>
                <a-form-item label="名字" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input v-decorator="[ 'testName', validatorRules.testName]" placeholder="请输入名字"></a-input>
                </a-form-item>
                <a-form-item label="富文本" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <j-editor v-decorator="['testUme',{trigger:'input'}]"/>
                </a-form-item>
                <a-form-item label="开关" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-switch v-decorator="['testSwitch', validatorRules.testSwitch]" checked-children="0" un-checked-children="1" default-checked/>
                </a-form-item>
                <a-form-item label="密码" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input type="password" v-decorator="[ 'testPassword', validatorRules.testPassword]" placeholder="请输入密码"></a-input>
                </a-form-item>
                <a-form-item label="邮件" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input v-decorator="[ 'testEmail', validatorRules.testEmail]" placeholder="请输入邮件"></a-input>
                </a-form-item>
                <a-form-item label="网址" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input v-decorator="[ 'testUrl', validatorRules.testUrl]" placeholder="请输入网址"></a-input>
                </a-form-item>
                <a-form-item label="时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <j-date placeholder="请选择时间" v-decorator="[ 'testTime', validatorRules.testTime]" :trigger-change="true" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%"/>
                </a-form-item>
                <a-form-item label="下拉选" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <j-search-select-tag v-decorator="['testSels', validatorRules.testSels]" dict="sys_user,realname,id" />
                </a-form-item>
                <a-form-item label="多行文本" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-textarea v-decorator="['testTxta', validatorRules.testTxta]" rows="4" placeholder="请输入多行文本"/>
                </a-form-item>
                <a-form-item label="日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <j-date placeholder="请选择日期" v-decorator="[ 'testDate', validatorRules.testDate]" :trigger-change="true" style="width: 100%"/>
                </a-form-item>
                <a-form-item label="下拉多选" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <j-multi-select-tag type="list_multi" v-decorator="['testSelm', validatorRules.testSelm]" :trigger-change="true" dictCode="" placeholder="请选择下拉多选"/>
                </a-form-item>
                <a-form-item label="文件" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <j-upload v-decorator="['testFile', validatorRules.testFile]" :trigger-change="true"></j-upload>
                </a-form-item>

            </a-form>
        </a-spin>
    </a-modal>
</template>

<script>

    import pick from 'lodash.pick'
    import { validateDuplicateValue } from '@/utils/util'
    import JDate from '@/components/jeecg/JDate'
    import JUpload from '@/components/jeecg/JUpload'
    import JMultiSelectTag from "@/components/dict/JMultiSelectTag"
    import JSearchSelectTag from '@/components/dict/JSearchSelectTag'
    import JEditor from '@/components/jeecg/JEditor'

    export default {
        name: "TestModal",
        components: {
            JDate,
            JUpload,
            JMultiSelectTag,
            JSearchSelectTag,
            JEditor,
        },
        data () {
            return {
                form: this.$form.createForm(this),
                title:"操作",
                width:800,
                visible: false,
                model: {},
                labelCol: {
                    xs: { span: 24 },
                    sm: { span: 5 },
                },
                wrapperCol: {
                    xs: { span: 24 },
                    sm: { span: 16 },
                },
                confirmLoading: false,
                validatorRules: {
                    testAge: {rules: [
                            {required: true, message: '请输入年龄!'},
                            {pattern:/^.{0,100}$/, message: '请输入0到100位任意字符!'},
                        ]},
                    testImg: {rules: [
                            {required: true, message: '请输入图片!'},
                        ]},
                    testName: {rules: [
                        ]},
                    testUme: {rules: [
                        ]},
                    testSwitch: {rules: [
                        ]},
                    testPassword: {rules: [
                            {required: true, message: '请输入密码!'},
                            {pattern:/^.{6,10}$/, message: '请输入6到10位任意字符!'},
                        ]},
                    testEmail: {rules: [
                            {pattern:/^([\w]+\.*)([\w]+)@[\w]+\.\w{3}(\.\w{2}|)$/, message: '请输入正确的电子邮件!'},
                        ]},
                    testUrl: {rules: [
                            {pattern:/^http[s]?:\/\/.*/, message: '请输入正确的网址!'},
                        ]},
                    testTime: {rules: [
                        ]},
                    testSels: {rules: [
                        ]},
                    testTxta: {rules: [
                        ]},
                    testDate: {rules: [
                        ]},
                    testSelm: {rules: [
                        ]},
                    testFile: {rules: [
                        ]},
                },
                url: {
                    add: "/test/test/add",
                    edit: "/test/test/edit",
                }
            }
        },
        created () {
        },
        methods: {
            add () {
                this.edit({});
            },
            edit (record) {
                this.form.resetFields();
                this.model = Object.assign({}, record);
                this.visible = true;
                this.$nextTick(() => {
                    this.form.setFieldsValue(pick(this.model,'testAge','testImg','testCheckbox','testName','testUme','testSwitch','testPassword','testEmail','testUrl','testTime','testSels','testTxta','testDate','testSelm','testFile'))
                })
            },
            close () {
                this.$emit('close');
                this.visible = false;
            },
            handleOk () {
                const that = this;
                // 触发表单验证
                this.form.validateFields((err, values) => {
                    if (!err) {
                        that.confirmLoading = true;
                        let httpurl = '';
                        let method = '';
                        if(!this.model.id){
                            httpurl+=this.url.add;
                            method = 'post';
                        }else{
                            httpurl+=this.url.edit;
                            method = 'put';
                        }
                        let formData = Object.assign(this.model, this.dateFormatDate(values));
                        console.log("表单提交数据",formData)
                        this.$httpReq(httpurl,formData,method).then((res)=>{
                            if(res.success){
                                that.$message.success(res.message);
                                that.$emit('ok');
                                that.close();
                            }else{
                                that.$message.warning(res.message);
                            }
                        }).finally(() => {
                            that.confirmLoading = false;
                        })
                    }

                })
            },
            handleCancel () {
                this.close()
            },
            popupCallback(row){
                this.form.setFieldsValue(pick(row,'testAge','testImg','testCheckbox','testName','testUme','testSwitch','testPassword','testEmail','testUrl','testTime','testSels','testTxta','testDate','testSelm','testFile'))
            },
            dateFormatDate(form){
                form['testDate'] = form['testDate']? formatDate(form['testDate'],"yyyy-MM-dd hh:mm:ss"):null;
                return form;
            },


        }
    }
</script>
