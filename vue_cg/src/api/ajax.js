import {axios} from '@/utils/request'


//post
export function post(url, parameter) {
    return httpReq(url, parameter, "post")
}


//put
export function put(url, parameter) {
    return httpReq(url, parameter, "put")
}

//get
export function get(url, parameter) {
    return httpReq(url, parameter, "get")
}

//deleteAction
export function del(url, parameter) {
    return httpReq(url, parameter, "delete")
}

/**
 * 下载文件 用于excel导出
 * @param url
 * @param parameter
 * @returns {*}
 */
export function downFile(url,parameter){
    return axios({
        url: url,
        params: parameter,
        method:'get' ,
        responseType: 'blob',
        // headers: {
        //     'Content-Type': 'application/octet-stream',
        // }
    })
}


//post method= {post | put}
export function httpReq(url, parameter, method,header) {
    let pp = {
        url: url,
        method: method,

    }
    if(method == 'put' || method == "post"){
        pp.data = parameter;
    }else{
        pp.params = parameter;
    }
    if(header){
        pp.headers = header;
    }
    return axios(pp)
}
