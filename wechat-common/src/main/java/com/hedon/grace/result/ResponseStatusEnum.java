package com.hedon.grace.result;

public enum ResponseStatusEnum {
    SUCCESS(200, true, "操作成功"),
    ERROR(500, false, "操作失败"),

    SYSTEM_ERROR_BLACK_IP(5621, false, "请求过于频繁，请稍后重试！");

    private int status;
    private boolean success;
    private String message;

    ResponseStatusEnum(int status, boolean success, String message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
