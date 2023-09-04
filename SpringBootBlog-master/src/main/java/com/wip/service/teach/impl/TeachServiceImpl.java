/**
 * Created by IntelliJ IDEA.
 * User: Kyrie
 * DateTime: 2018/7/25 16:50
 **/
package com.wip.service.teach.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wip.constant.ErrorConstant;
import com.wip.constant.Types;
import com.wip.constant.WebConst;
import com.wip.dao.CommentDao;
import com.wip.dao.RelationShipDao;
import com.wip.dao.TeachDao;
import com.wip.dto.cond.TeachCond;
import com.wip.exception.BusinessException;
import com.wip.model.*;
import com.wip.service.meta.MetaService;
import com.wip.service.teach.TeachService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeachServiceImpl implements TeachService {

    @Autowired
    private TeachDao teachDao;

    @Autowired
    private MetaService metaService;

    @Autowired
    private RelationShipDao relationShipDao;

    @Autowired
    private CommentDao commentDao;

    @Transactional
    @Override
    @CacheEvict(value = {"articleCache", "articleCaches"}, allEntries = true, beforeInvocation = true)
    public void addArticle(TeachDomain teachDomain) {
        if (null == teachDomain)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);

        if (StringUtils.isBlank(teachDomain.getTitle()))
            throw BusinessException.withErrorCode(ErrorConstant.Article.TITLE_CAN_NOT_EMPTY);

        if (teachDomain.getTitle().length() > WebConst.MAX_TITLE_COUNT)
            throw BusinessException.withErrorCode(ErrorConstant.Article.TITLE_IS_TOO_LONG);

        if (StringUtils.isBlank(teachDomain.getContent()))
            throw BusinessException.withErrorCode(ErrorConstant.Article.CONTENT_CAN_NOT_EMPTY);

        if (teachDomain.getContent().length() > WebConst.MAX_CONTENT_COUNT)
            throw BusinessException.withErrorCode(ErrorConstant.Article.CONTENT_IS_TOO_LONG);

        // 取到标签和分类
        String tags = teachDomain.getTags();
        String categories = teachDomain.getCategories();

        // 添加文章
        teachDao.addArticle(teachDomain);

        // 添加分类和标签
        int tid = teachDomain.getTid();
        metaService.addMetas(tid, tags, Types.TAG.getType());
        metaService.addMetas(tid, categories, Types.CATEGORY.getType());


    }

    @Override
    @Cacheable(value = "articleCache", key = "'articleById_' + #p0")
    public TeachDomain getArticleById(Integer tid) {
        if (null == tid)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        return teachDao.getArticleById(tid);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"articleCache", "articleCaches"}, allEntries = true, beforeInvocation = true)
    public void updateArticleById(TeachDomain teachDomain) {
        // 标签和分类
        String tags = teachDomain.getTags();
        String categories = teachDomain.getCategories();

        // 更新文章
        teachDao.updateArticleById(teachDomain);
        int tid = teachDomain.getTid();
        relationShipDao.deleteRelationShipByCid(tid);
        metaService.addMetas(tid,tags,Types.TAG.getType());
        metaService.addMetas(tid,categories,Types.CATEGORY.getType());

    }

    @Override
    @Cacheable(value = "articleCaches", key = "'articlesByCond_' + #p1 + 'type_' + #p0.type")
    public PageInfo<TeachDomain> getArticlesByCond(TeachCond teachCond, int pageNum, int pageSize) {
        if (null == teachCond)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        PageHelper.startPage(pageNum,pageSize);
        List<TeachDomain> contents = teachDao.getArticleByCond(teachCond);
        PageInfo<TeachDomain> pageInfo = new PageInfo<>(contents);
        return pageInfo;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"articleCache","articleCaches"},allEntries = true, beforeInvocation = true)
    public void deleteArticleById(Integer tid) {
        if (null == tid)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        // 删除文章
        teachDao.deleteArticleById(tid);

        // 同时要删除该 文章下的所有评论
        List<CommentDomain> comments = commentDao.getCommentByCId(tid);
        if (null != comments && comments.size() > 0) {
            comments.forEach(comment -> {
                commentDao.deleteComment(comment.getCoid());
            });
        }

        // 删除标签和分类关联
        List<RelationShipDomain> relationShips = relationShipDao.getRelationShipByCid(tid);
        if (null != relationShips && relationShips.size() > 0) {
            relationShipDao.deleteRelationShipByCid(tid);
        }


    }

    @Override
    @CacheEvict(value = {"articleCache","articleCaches"}, allEntries = true, beforeInvocation = true)
    public void updateContentByCid(TeachDomain teachDomain) {
        if (null != teachDomain && null != teachDomain.getTid()) {
            teachDao.updateArticleById(teachDomain);
        }
    }

    @Override
    @Cacheable(value = "articleCache", key = "'articleByCategory_' + #p0")
    public List<TeachDomain> getArticleByCategory(String category) {
        if (null == category)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        return teachDao.getArticleByCategory(category);
    }

    @Override
    @Cacheable(value = "articleCache", key = "'articleByTags_'+ #p0")
    public List<TeachDomain> getArticleByTags(MetaDomain tags) {
        if (null == tags)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        List<RelationShipDomain> relationShip = relationShipDao.getRelationShipByMid(tags.getMid());
        if (null != relationShip && relationShip.size() > 0) {
            return teachDao.getArticleByTags(relationShip);
        }
        return null;
    }
}
