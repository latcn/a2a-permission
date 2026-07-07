package io.github.latcn.a2a.permission.remote.controller;

import io.github.latcn.a2a.permission.api.dto.UserFullPermissionDTO;
import io.github.latcn.a2a.permission.common.utils.ResultUtil;
import io.github.latcn.a2a.permission.remote.client.RemotePermissionQueryService;
import io.github.latcn.archbase.foundation.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RemotePermissionQueryService permissionQueryService;

    @GetMapping("/full-permissions")
    public UserFullPermissionDTO test01(Long userId) {
        try {
            Result<UserFullPermissionDTO> result =  permissionQueryService.getUserFullPermissions(userId);
            return ResultUtil.extractValue(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
