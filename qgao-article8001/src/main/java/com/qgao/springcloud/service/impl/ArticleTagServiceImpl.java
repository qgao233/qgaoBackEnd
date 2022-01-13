package com.qgao.springcloud.service.impl;

import com.qgao.springcloud.dao.ArticleTagDao;
import com.qgao.springcloud.dto.ArticleReceiveDto;
import com.qgao.springcloud.entity.ArticleTag;
import com.qgao.springcloud.service.ArticleTagService;
import com.qgao.springcloud.utils.util.SnowFlakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ArticleTagServiceImpl implements ArticleTagService{

    @Resource
    private ArticleTagDao articleTagDao;

    @Override
    @Transactional
    public List<ArticleTag> createArticleTags(ArticleReceiveDto articleSaveDto) throws Exception {
        log.debug("insert: table articleTag.");

        if(articleSaveDto.getArticle_tag().equals("") || articleSaveDto.getArticle_tag() == null){
            return null;
        }

        String[] tag = articleSaveDto.getArticle_tag().split(",");
        List<ArticleTag> tags = new ArrayList<>();
        for (String t : tag) {
            tags.add(new ArticleTag(SnowFlakeIdUtil.generateID(false,null), t));
        }

        int rows;
        if((rows = articleTagDao.insertBatch(tags)) < 0) {
            throw new RuntimeException("insert articleTags failure, affected rows: "+rows);
        }
        //因为插入时可能重复的tag_name未能插入,所以id需要重新从数据库取
        List<ArticleTag> realTags = new ArrayList<>();
        ArticleTag articleTag = null;
        for(String t : tag){
            if((articleTag = articleTagDao.queryByTagName(t)) == null)
                throw new RuntimeException("query articleTags failure, affected rows: "+rows);
            realTags.add(articleTag);
        }

        return realTags;
    }


}
