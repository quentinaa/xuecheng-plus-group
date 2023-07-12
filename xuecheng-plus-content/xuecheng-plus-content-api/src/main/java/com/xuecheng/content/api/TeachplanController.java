package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xiong
 * @version 1.0
 * @description 课程计划相关接口
 * @date 2023/7/4 15:21:10
 */
@Api(value = "课程计划管理接口",tags = "课程计划管理接口")
@RestController
public class TeachplanController {
    @Autowired
    private TeachplanService teachplanService;

    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId",name = "课程基础Id值",required = true,dataType = "Long",paramType = "path")
    @GetMapping("teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        return teachplanService.findTeachplanTree(courseId);
    }

    @ApiOperation("课程计划添加或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SaveTeachplanDto teachplanDto){
        teachplanService.saveTeachplan(teachplanDto);
    }

    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{teachPlanId}")
    public void delTeachPlan(@PathVariable Long teachPlanId){
        teachplanService.delTeachPlan(teachPlanId);
    }

    @ApiOperation("课程计划移动")
    @PostMapping("/teachplan/{move}/{teachPlanId}")
    public void moveTeachPlan(@PathVariable String move,@PathVariable Long teachPlanId){
        teachplanService.moveTeachPlan(move,teachPlanId);
    }

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
    public void unassociationMedia(@PathVariable Long teachPlanId,@PathVariable String mediaId){
       teachplanService.unassociationMedia(teachPlanId,mediaId);
    }
}
