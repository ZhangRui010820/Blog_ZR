package com.wip.dao;

import com.wip.dto.cond.TeachCond;
import com.wip.model.RelationShipDomain;
import com.wip.model.TeachDomain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章相关Dao接口
 */
@Mapper
public interface TeachDao {

    /**
     * 添加文章
     * @param teachDomain
     */
    void addArticle(TeachDomain teachDomain);

    /**
     * 根据编号获取文章
     * @param tid
     * @return
     */
    TeachDomain getArticleById(Integer tid);

    /**
     * 更新文章
     * @param teachDomain
     */
    void updateArticleById(TeachDomain teachDomain);

    /**
     * 根据条件获取文章列表
     * @param teachCond
     * @return
     */
    List<TeachDomain> getArticleByCond(TeachCond teachCond);

    /**
     * 删除文章
     * @param tid
     */
    void deleteArticleById(Integer tid);

    /**
     * 获取文章总数
     * @return
     */
    Long getArticleCount();

    /**
     * 通过分类名获取文章
     * @param category
     * @return
     */
    List<TeachDomain> getArticleByCategory(@Param("category") String category);

    /**
     * 通过标签获取文章
     * @param cid
     * @return
     */
    List<TeachDomain> getArticleByTags(List<RelationShipDomain> cid);
}
