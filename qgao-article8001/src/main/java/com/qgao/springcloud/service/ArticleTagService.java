package com.qgao.springcloud.service;

import com.qgao.springcloud.dto.ArticleReceiveDto;
import com.qgao.springcloud.entity.ArticleTag;

import java.util.List;

public interface ArticleTagService {

    List<ArticleTag> createArticleTags(ArticleReceiveDto articleSaveDto)throws Exception;

}
