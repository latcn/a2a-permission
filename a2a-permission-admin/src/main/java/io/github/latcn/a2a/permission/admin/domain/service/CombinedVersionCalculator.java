package io.github.latcn.a2a.permission.admin.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Component
public class CombinedVersionCalculator {

    public String calculate(Long userPermVersion, Map<Long, Long> roleVersions) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(String.valueOf(userPermVersion).getBytes(StandardCharsets.UTF_8));

            TreeMap<Long, Long> sortedVersions = new TreeMap<>(roleVersions);
            for (Map.Entry<Long, Long> entry : sortedVersions.entrySet()) {
                String roleVersionKey = entry.getKey() + ":" + entry.getValue();
                md.update(roleVersionKey.getBytes(StandardCharsets.UTF_8));
            }

            byte[] digest = md.digest();
            return HexFormat.of().formatHex(digest);

        } catch (NoSuchAlgorithmException e) {
            log.error("MD5 algorithm not available", e);
            return fallbackCalculate(userPermVersion, roleVersions);
        }
    }

    private String fallbackCalculate(Long userPermVersion, Map<Long, Long> roleVersions) {
        StringBuilder sb = new StringBuilder();
        sb.append(userPermVersion);

        TreeMap<Long, Long> sorted = new TreeMap<>(roleVersions);
        for (Map.Entry<Long, Long> entry : sorted.entrySet()) {
            sb.append("-").append(entry.getKey()).append(":").append(entry.getValue());
        }

        return sb.toString();
    }

    public boolean validateVersion(String combinedVersion) {
        if (combinedVersion == null || combinedVersion.isEmpty()) {
            return false;
        }

        if (combinedVersion.length() == 32) {
            return combinedVersion.matches("[0-9a-f]{32}");
        }

        return true;
    }
}