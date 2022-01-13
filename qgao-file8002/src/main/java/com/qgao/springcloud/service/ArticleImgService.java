package com.qgao.springcloud.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface ArticleImgService {

    String createTempBufferFolder(long userId);

    String[] uploadArticleImg(Long userId, MultipartFile[] imgs) throws Exception;

    void deleteArticleImg(Long userId, String imgIdSuffix) throws Exception;

    void getArticleImg(String imgIdSuffix, HttpServletResponse response) throws Exception;

    void transferArticleImg(Long userId, Long articleId) throws Exception;

    void deleteCommonArticleImg(Long articleId) throws Exception;
}
