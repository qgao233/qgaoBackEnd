package com.qgao.springcloud.service.impl;

import com.qgao.springcloud.dao.UnionArticleImgDao;
import com.qgao.springcloud.entity.UnionArticleImg;
import com.qgao.springcloud.helper.article.ArticleImgHelper;
import com.qgao.springcloud.service.ArticleImgService;
import com.qgao.springcloud.utils.util.SnowFlakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ArticleImgServiceImpl implements ArticleImgService {

    @Value("${qgao.img.article.identifyKey}")
    private String identifyKey;
    @Resource
    private ArticleImgHelper articleFileHelper;

    @Resource
    private UnionArticleImgDao unionArticleImgDao;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public String createTempBufferFolder(long userId) {
        //redis的key为userId+identifyKey, value为临时文件夹的id
        String tempBufferId = String.valueOf(SnowFlakeIdUtil.generateID());
        String redisKey = identifyKey+String.valueOf(userId);
        // redis set value
        redisTemplate.opsForValue().set(redisKey,tempBufferId);

        return tempBufferId;
    }

    @Override
    public String[] uploadArticleImg(Long userId, MultipartFile[] imgs)throws Exception {
        log.debug("upload article img");

        if(userId == null){
            throw new RuntimeException("only user can do upload.");
        }
        String redisKey = identifyKey+String.valueOf(userId);
        String tempBufferId = (String) redisTemplate.opsForValue().get(redisKey);// 从redis中取出

        return articleFileHelper.uploadFile(tempBufferId,imgs);
    }

    @Override
    public void deleteArticleImg(Long userId, String imgIdSuffix) throws Exception {
        log.debug("delete article img");

        if(userId == null){
            throw new RuntimeException("only user can do delete.");
        }

        String redisKey = identifyKey+String.valueOf(userId);
        String tempBufferId = (String) redisTemplate.opsForValue().get(redisKey);// 从redis中取出


        ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();
        threadLocal.set(false);

        if(!articleFileHelper.deleteFile(tempBufferId,imgIdSuffix)){
            throw new RuntimeException("file being deleted does not exist");
        }
        if(threadLocal.get()){
            unionArticleImgDao.deleteByArticleImgId(imgIdSuffix);
        }

    }

    @Override
    public void getArticleImg(String imgIdSuffix, HttpServletResponse response) throws Exception {
        log.debug("get article img");
        if(!articleFileHelper.getFile(imgIdSuffix,response)){
            throw new RuntimeException("file does not exist");
        }

    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void transferArticleImg(Long userId, Long articleId) throws Exception {
        log.debug("transfer article img");

        if(userId == null){
            throw new RuntimeException("only user can do transfer.");
        }

        String redisKey = identifyKey+String.valueOf(userId);
        String tempBufferId = (String) redisTemplate.opsForValue().get(redisKey);// 从redis中取出


        List<String> imgIdSuffixs = articleFileHelper.transferFile(tempBufferId);
        if(imgIdSuffixs == null){
            throw new RuntimeException("temp dir does not exist.");
        }

        List<UnionArticleImg> unionArticleImgs = new ArrayList<>();
        for (String imgIdSuffix : imgIdSuffixs){
            unionArticleImgs.add(new UnionArticleImg(imgIdSuffix,articleId));
        }

        int rows;
        if((rows = unionArticleImgDao.insertBatch(unionArticleImgs)) <= 0){
            throw new RuntimeException("insert union_article_img failure, affected rows: "+rows);
        }

        // 删除redis的id
        redisTemplate.delete(redisKey);
    }

    @Override
    public void deleteCommonArticleImg(Long articleId) throws Exception {
        log.debug("delete article img in common area");

        List<UnionArticleImg> unionArticleImgs = unionArticleImgDao.queryAll(new UnionArticleImg(articleId));

        for (UnionArticleImg unionArticleImg : unionArticleImgs){
            articleFileHelper.deleteFile(null,unionArticleImg.getArticleImgId());
        }

        unionArticleImgDao.deleteByArticleId(articleId);


    }

    //todo 定时删除临时存储区的所有临时id文件夹
}
