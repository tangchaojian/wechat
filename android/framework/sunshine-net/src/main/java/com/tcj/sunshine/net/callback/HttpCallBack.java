package com.tcj.sunshine.net.callback;

import com.tcj.sunshine.net.entity.ResponseEntity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Stefan Lau on 2019/11/11.
 */
public abstract class HttpCallBack<T> implements ParameterizedType {

    private ResponseEntity<T> result;

    public abstract void success(String code, String msg, T t);

    public abstract void error(String code, String msg);

    public abstract void complete(String code, String msg, T t);


    @Override
    public Type[] getActualTypeArguments() {
        Class clz = this.getClass();
        //这里必须注意在外面使用new GsonResponsePasare<GsonResponsePasare.DataInfo>(){};实例化时必须带上{},否则获取到的superclass为Object
        Type superclass = clz.getGenericSuperclass(); //getGenericSuperclass()获得带有泛型的父类
        if (superclass instanceof Class) {
            return new Type[]{ResponseEntity.class};
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments();
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    @Override
    public Type getRawType() {
        return ResponseEntity.class;
    }

    public ResponseEntity<T> getResult() {
        return result;
    }

    public void setResult(ResponseEntity<T> result) {
        this.result = result;
    }
}