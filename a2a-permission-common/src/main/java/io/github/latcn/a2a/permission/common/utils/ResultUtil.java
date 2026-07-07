package io.github.latcn.a2a.permission.common.utils;

import io.github.latcn.archbase.foundation.result.Result;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResultUtil {

    public static <T> T extractValue(Result<T> result) {
        if (result.isSuccess()) {
            return result.getData();
        } else {
            log.error("extractValue error: {}", result);
            return null;
        }
    }

}
