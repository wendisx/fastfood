package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 实现公共字段填充
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     * 在mapper中匹配所有带注释的方法，在autofill注释中
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知
     */
    @SneakyThrows
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        //打印日志消息
        log.info("开始公共字段填充...");
        //获取数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        //获取数据库方法操作类型
        OperationType operationType = autoFill.value();

        //获取参数的实体对象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length ==0){
            return;
        }
        //获取实体类对象--方法参数
        Object entity = args[0];

        //准备赋值数据
        LocalDateTime nowTime = LocalDateTime.now();
        long currentId = BaseContext.getCurrentId();

        //为公共字段赋值
       if(operationType == OperationType.INSERT){
           Method setCreateTime = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
           Method setCreatUser = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
           Method setUpdateTime = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
           Method setUpdateUser = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
           //反射赋值
           setCreateTime.invoke(entity,nowTime);
           setCreatUser.invoke(entity,currentId);
           setUpdateTime.invoke(entity,nowTime);
           setUpdateUser.invoke(entity,currentId);
       }
       else if(operationType == OperationType.UPDATE){
           Method setUpdateTime = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
           Method setUpdateUser = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

           setUpdateTime.invoke(entity,nowTime);
           setUpdateUser.invoke(entity,currentId);
       }
    }
}
