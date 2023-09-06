package com.wip.controller.admin;

import com.github.pagehelper.PageInfo;
import com.wip.constant.LogActions;
import com.wip.constant.Types;
import com.wip.controller.BaseController;
import com.wip.dto.cond.ContentCond;
import com.wip.dto.cond.MetaCond;
import com.wip.dto.cond.TeachCond;
import com.wip.model.ContentDomain;
import com.wip.model.MetaDomain;
import com.wip.model.TeachDomain;
import com.wip.service.article.ContentService;
import com.wip.service.log.LogService;
import com.wip.service.meta.MetaService;
import com.wip.service.teach.TeachService;
import com.wip.utils.APIResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api("文章管理")
@Controller
@RequestMapping("/admin/teachAdmin")
public class TeachController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private TeachService teachService;

    @Autowired
    private MetaService metaService;

    @Autowired
    private LogService logService;

    @ApiOperation("文章页")
    @GetMapping(value = "")
    public String index(
            HttpServletRequest request,
            @ApiParam(name = "page", value = "页数", required = false)
            @RequestParam(name = "page", required = false, defaultValue = "1")
            int page,
            @ApiParam(name = "limit", value = "每页数量", required = false)
            @RequestParam(name = "limit", required = false, defaultValue = "15")
            int limit
    ) {
        //获取教程内容
        PageInfo<TeachDomain> articles = teachService.getArticlesByCond(new TeachCond(), page, limit);
        //教程存入request
        request.setAttribute("articles",articles);
        //转发到页面
        return "admin/teach_list";
    }

    @ApiOperation("发布新文章页")
    @GetMapping(value = "/publish")
    public String newArticle(HttpServletRequest request) {
        //new Meta查询条件
        MetaCond metaCond = new MetaCond();
        //设置类型
        metaCond.setType(Types.CATEGORY.getType());
        //获取项目列表
        List<MetaDomain> metas = metaService.getMetas(metaCond);
        //设置入request中
        request.setAttribute("categories",metas);
        //转发到教程编辑页面
        return "admin/teach_edit";
    }

    @ApiOperation("文章编辑页")
    @GetMapping(value = "/{tid}")
    public String editArticle(
            @ApiParam(name = "tid", value = "文章编号", required = true)
            @PathVariable
            Integer tid,
            HttpServletRequest request
    ) {
        //根据ID获取教程文章
        TeachDomain content = teachService.getArticleById(tid);
        //设置入request中
        request.setAttribute("contents", content);
        //new Meta查询条件
        MetaCond metaCond = new MetaCond();
        //设置类型
        metaCond.setType(Types.CATEGORY.getType());
        //获取项目列表
        List<MetaDomain> categories = metaService.getMetas(metaCond);
        //设置入request中
        request.setAttribute("categories", categories);
        request.setAttribute("active", "article");
        //转发到教程编辑页面
        return "admin/teach_edit";
    }

    @ApiOperation("编辑保存文章")
    @PostMapping("/modify")
    @ResponseBody
    public APIResponse modifyArticle(
            HttpServletRequest request,
            @ApiParam(name = "cid", value = "文章主键", required = true)
            @RequestParam(name = "cid", required = true)
            Integer cid,
            @ApiParam(name = "title", value = "标题", required = true)
            @RequestParam(name = "title", required = true)
            String title,
            @ApiParam(name = "titlePic", value = "标题图片", required = false)
            @RequestParam(name = "titlePic", required = false)
            String titlePic,
            @ApiParam(name = "slug", value = "内容缩略名", required = false)
            @RequestParam(name = "slug", required = false)
            String slug,
            @ApiParam(name = "content", value = "内容", required = true)
            @RequestParam(name = "content", required = true)
            String content,
            @ApiParam(name = "type", value = "文章类型", required = true)
            @RequestParam(name = "type", required = true)
            String type,
            @ApiParam(name = "status", value = "文章状态", required = true)
            @RequestParam(name = "status", required = true)
            String status,
            @ApiParam(name = "tags", value = "标签", required = false)
            @RequestParam(name = "tags", required = false)
            String tags,
            @ApiParam(name = "categories", value = "分类", required = false)
            @RequestParam(name = "categories", required = false, defaultValue = "默认分类")
            String categories,
            @ApiParam(name = "allowComment", value = "是否允许评论", required = true)
            @RequestParam(name = "allowComment", required = true)
            Boolean allowComment
    ) {
        //新建教程
        TeachDomain teachDomain = new TeachDomain();
        //设置文章各种数据
        teachDomain.setTitle(title);
        //设置文章ID
        teachDomain.setTid(cid);
        teachDomain.setTitlePic(titlePic);
        teachDomain.setSlug(slug);
        teachDomain.setContent(content);
        teachDomain.setType(type);
        teachDomain.setStatus(status);
        teachDomain.setTags(tags);
        teachDomain.setCategories(categories);
        teachDomain.setAllowComment(allowComment ? 1: 0);
        //保存文章
        teachService.updateArticleById(teachDomain);
        //成功
        return APIResponse.success();
    }


    @ApiOperation("发布新文章")
    @PostMapping(value = "/publish")
    @ResponseBody
    public APIResponse publishArticle(
            @ApiParam(name = "title", value = "标题", required = true)
            @RequestParam(name = "title", required = true)
            String title,
            @ApiParam(name = "titlePic", value = "标题图片", required = false)
            @RequestParam(name = "titlePic", required = false)
            String titlePic,
            @ApiParam(name = "slug", value = "内容缩略名", required = false)
            @RequestParam(name = "slug", required = false)
            String slug,
            @ApiParam(name = "content", value = "内容", required = true)
            @RequestParam(name = "content", required = true)
            String content,
            @ApiParam(name = "type", value = "文章类型", required = true)
            @RequestParam(name = "type", required = true)
            String type,
            @ApiParam(name = "status", value = "文章状态", required = true)
            @RequestParam(name = "status", required = true)
            String status,
            @ApiParam(name = "categories", value = "文章分类", required = false)
            @RequestParam(name = "categories", required = false, defaultValue = "默认分类")
            String categories,
            @ApiParam(name = "tags", value = "文章标签", required = false)
            @RequestParam(name = "tags", required = false)
            String tags,
            @ApiParam(name = "allowComment", value = "是否允许评论", required = true)
            @RequestParam(name = "allowComment", required = true)
            Boolean allowComment
    ) {
        //新建教程
        TeachDomain teachDomain = new TeachDomain();
        //设置文章各种数据
        teachDomain.setTitle(title);
        teachDomain.setTitlePic(titlePic);
        teachDomain.setSlug(slug);
        teachDomain.setContent(content);
        teachDomain.setType(type);
        teachDomain.setStatus(status);
        teachDomain.setHits(1);
        teachDomain.setCommentsNum(0);
        // 只允许博客文章有分类，防止作品被收入分类
        teachDomain.setTags(type.equals(Types.ARTICLE.getType()) ? tags : null);
        teachDomain.setCategories(type.equals(Types.ARTICLE.getType()) ? categories : null);
        teachDomain.setAllowComment(allowComment ? 1 : 0);

        // 添加文章
        teachService.addArticle(teachDomain);
        //成功
        return APIResponse.success();
    }

    @ApiOperation("删除文章")
    @PostMapping("/delete")
    @ResponseBody
    public APIResponse deleteArticle(
            @ApiParam(name = "tid", value = "文章ID", required = true)
            @RequestParam(name = "tid", required = true)
            Integer tid,
            HttpServletRequest request
    ) {
        // 删除文章
        teachService.deleteArticleById(tid);
        // 写入日志
        logService.addLog(LogActions.DEL_ARTICLE.getAction(), tid+"",request.getRemoteAddr(),this.getUid(request));
        return APIResponse.success();
    }


}
