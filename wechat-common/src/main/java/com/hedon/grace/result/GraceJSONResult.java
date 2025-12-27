package com.hedon.grace.result;

import java.util.Map;

public class GraceJSONResult {
    // 响应业务状态码
    private Integer status;
    // 响应信息
    private String msg;
    // 是否成功
    private Boolean success;
    // 响应数据
    private Object data;

    public static GraceJSONResult ok(Object data) {
        return new GraceJSONResult(data);
    }

    public static GraceJSONResult error() {
        return new GraceJSONResult(ResponseStatusEnum.ERROR);
    }

    public static GraceJSONResult error(String msg) {
        return new GraceJSONResult(ResponseStatusEnum.ERROR, msg);
    }

    public static GraceJSONResult error(ResponseStatusEnum status) {
        return new GraceJSONResult(status);
    }

    public static GraceJSONResult error(ResponseStatusEnum status, String msg) {
        return new GraceJSONResult(status, msg);
    }

    public static GraceJSONResult errorMap(Map<String, String> map) {
        return new GraceJSONResult(ResponseStatusEnum.ERROR, map);
    }

    private GraceJSONResult(ResponseStatusEnum status, Object data) {
        this.status = status.getStatus();
        this.msg = status.getMessage();
        this.success = status.isSuccess();
        this.data = data;
    }

    private GraceJSONResult(Object data) {
        this.status = ResponseStatusEnum.ERROR.getStatus();
        this.msg = ResponseStatusEnum.ERROR.getMessage();
        this.success = ResponseStatusEnum.ERROR.isSuccess();
        this.data = data;
    }

    private GraceJSONResult(ResponseStatusEnum status) {
        this.status = status.getStatus();
        this.msg = status.getMessage();
        this.success = status.isSuccess();
        this.data = null;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Object getData() {
        return data;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
