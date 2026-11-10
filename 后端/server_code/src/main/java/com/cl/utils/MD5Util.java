package com.cl.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码工具类（纯 JDK 实现，无外部依赖）
 */
public class MD5Util {

    /**
     * 固定盐值，用于密码加密（生产环境应改为配置项）
     */
    private static final String PASSWORD_SALT = "agri_platform_2026_salt";

    /**
     * 纯 MD5（不带盐）
     */
    public static String md5(String text) {
        if (text == null) {
            return null;
        }
        return digest("MD5", text);
    }

    /**
     * 带固定盐的密码加密：MD5(盐 + 明文 + 盐)
     */
    public static String md5WithSalt(String password) {
        if (password == null) {
            return null;
        }
        return digest("MD5", PASSWORD_SALT + password + PASSWORD_SALT);
    }

    /**
     * 验证密码是否匹配
     *
     * @param rawPassword      用户输入的明文密码
     * @param encryptedPassword 数据库中存储的密文
     * @return 是否匹配
     */
    public static boolean verifyPassword(String rawPassword, String encryptedPassword) {
        if (rawPassword == null || encryptedPassword == null) {
            return false;
        }
        return md5WithSalt(rawPassword).equalsIgnoreCase(encryptedPassword);
    }

    /**
     * 通用消息摘要
     */
    private static String digest(String algorithm, String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                // 转两位十六进制
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(algorithm + " algorithm not available", e);
        }
    }
}
