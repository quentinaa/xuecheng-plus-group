package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程分类 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService {
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> categoryTreeDtos=courseCategoryMapper.selectTreeNodes(id);
        //封装数据
        //先将list转成map
        Map<String,CourseCategoryTreeDto> dtoMap=categoryTreeDtos.stream().filter(item->!id.equals(item.getId())).collect(Collectors.toMap(key->key.getId(),value->value,(k1,k2)->k2));
        //定义一个list作为最终返回的list
        List<CourseCategoryTreeDto> categoryTreeDtoList=new ArrayList<>();
        //一边遍历一边找子节点放在父节点
        categoryTreeDtos.stream().filter(item->!id.equals(item.getId())).forEach(item->{
            if (item.getParentid().equals(id)){
                categoryTreeDtoList.add(item);
            }
            //找到节点的父节点
            CourseCategoryTreeDto courseCategoryTreeDto = dtoMap.get(item.getParentid());
            //父节点不为空
            if (courseCategoryTreeDto!=null){
                //如果该节点的getChildrenTreeNodes为空要new一个list集合
                if (courseCategoryTreeDto.getChildrenTreeNodes()==null){
                    courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<>());
                }
                //下边开始往ChildrenTreeNodes属性中放子节点
                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
            }
        });
        return categoryTreeDtoList;
    }
}
