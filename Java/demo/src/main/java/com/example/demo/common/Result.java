package com.example.demo.common;

import lombok.Data;

@Data
public class Result {

    private int code;
    private String msg;
    private Object data;
    private long ts;

    public static Result success() {
        Result result = new Result();
        result.setCode(0);
        result.setMsg("success");
        result.setTs(System.currentTimeMillis());
        return result;
    }

    public static Result success(Object data) {
        Result result = success();
        result.setData(data);
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.setCode(1);
        result.setMsg(msg);
        result.setTs(System.currentTimeMillis());
        return result;
    }
}
