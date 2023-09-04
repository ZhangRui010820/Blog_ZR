/**
 * Created by IntelliJ IDEA.
 * User: Kyrie
 * DateTime: 2018/7/25 16:48
 **/
package com.wip.service.teach;

import com.github.pagehelper.PageInfo;
import com.wip.dto.cond.ContentCond;
import com.wip.dto.cond.TeachCond;
import com.wip.model.ContentDomain;
import com.wip.model.MetaDomain;
import com.wip.model.TeachDomain;

import java.util.List;

/**
 * 文章相关Service接口
 */
public interface TeachService {

    /***
     * 添加文章
     * @param teachDomain
     */
    void addArticle(TeachDomain teachDomain);

    /**
     * 根据编号获取文章
     * @param cid
     * @return
     */
    TeachDomain getArticleById(Integer cid);

    /**
     * 更新文章
     * @param teachDomain
     */
    void updateArticleById(TeachDomain teachDomain);

    /**
     * 根据条件获取文章列表
     * @param teachCond
     * @param page
     * @param limit
     * @return
     */
    PageInfo<TeachDomain> getArticlesByCond(TeachCond teachCond, int page, int limit);

    /**
     * 删除文章
     * @param cid
     */
    void deleteArticleById(Integer cid);

    /**
     * 添加文章点击量
     * @param content
     */
    void updateContentByCid(TeachDomain content);

    /**
     * 通过分类获取文章
     * @param category
     * @return
     */
    List<TeachDomain> getArticleByCategory(String category);

    /**
     * 通过标签获取文章
     * @param tags
     * @return
     */
    List<TeachDomain> getArticleByTags(MetaDomain tags);
}
