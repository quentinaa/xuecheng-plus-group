package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements TeachplanService {

    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Transactional
    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //通过课程计划id判断是新增还是修改
        Long teachplanId = saveTeachplanDto.getId();
        if (teachplanId == null) {
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            //确定排序字段，找到他的同级目录元素的个数并加一
            Long parentid = saveTeachplanDto.getParentid();
            Long courseId = saveTeachplanDto.getCourseId();
            int count = getTeachplanCount(parentid, courseId);
            teachplan.setOrderby(count + 1);
            teachplan.setCreateDate(LocalDateTime.now());
            teachplanMapper.insert(teachplan);
        } else {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Transactional
    @Override
    public void delTeachPlan(Long teachPlanId) {
        if (teachPlanId == null) {
            XueChengPlusException.cast("课程计划id为空");
        }
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        //判断当前课程计划是章还是节
        if (teachplan.getGrade().equals(1)) {
            //（章）查询当前课程计划下是否有小节
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid, teachPlanId);
            int count = teachplanMapper.selectCount(queryWrapper);
            if (count > 0) {
                XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
            } else {
                teachplanMapper.deleteById(teachPlanId);
            }
        } else {
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper = queryWrapper.eq(TeachplanMedia::getTeachplanId, teachPlanId);
            teachplanMediaMapper.delete(queryWrapper);
            teachplanMapper.deleteById(teachPlanId);
        }
    }

    @Transactional
    @Override
    public void moveTeachPlan(String move, Long teachPlanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        //获取当前层级和orderby
        Integer orderby = teachplan.getOrderby();
        Integer grade = teachplan.getGrade();
        // 章节移动是比较同一课程id下的orderby
        Long courseId = teachplan.getCourseId();
        // 小节移动是比较同一章节id下的orderby
        Long parentid = teachplan.getParentid();
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        if (grade == 1) {
            if (move.equals("moveup")) {
                queryWrapper = queryWrapper.eq(Teachplan::getOrderby, orderby - 1).eq(Teachplan::getGrade, grade).eq(Teachplan::getCourseId, courseId);
                teachPlanMoveType(teachplan, orderby, queryWrapper);
            } else {
                queryWrapper = queryWrapper.eq(Teachplan::getOrderby, orderby + 1).eq(Teachplan::getGrade, grade).eq(Teachplan::getCourseId, courseId);
                teachPlanMoveType(teachplan, orderby, queryWrapper);
            }
        } else {
            if (move.equals("moveup")) {
                queryWrapper = queryWrapper.eq(Teachplan::getOrderby, orderby - 1).eq(Teachplan::getGrade, grade).eq(Teachplan::getParentid, parentid);
                teachPlanMoveType(teachplan, orderby, queryWrapper);
            } else {
                queryWrapper = queryWrapper.eq(Teachplan::getOrderby, orderby + 1).eq(Teachplan::getGrade, grade).eq(Teachplan::getParentid, parentid);
                teachPlanMoveType(teachplan, orderby, queryWrapper);
            }
        }


    }

    @Transactional
    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //教学计划id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (teachplan==null){
            XueChengPlusException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if (grade!=2){
            XueChengPlusException.cast("只允许第二级教学计划绑定媒资文件");
        }
        //课程id
        Long courseId = teachplan.getCourseId();
        //先删除原有记录，根据课程计划id删除它所绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, bindTeachplanMediaDto.getTeachplanId()));
        ////再添加教学计划与媒资的绑定关系
        TeachplanMedia teachplanMedia=new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    @Override
    public void unassociationMedia(Long teachPlanId, String mediaId) {
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getMediaId,mediaId).eq(TeachplanMedia::getTeachplanId,teachPlanId));
    }

    private void teachPlanMoveType(Teachplan teachplan, Integer orderby, LambdaQueryWrapper<Teachplan> queryWrapper) {
        Teachplan teachplanUp = teachplanMapper.selectOne(queryWrapper);
        if (teachplanUp == null) {
            XueChengPlusException.cast("已经到头啦，不能再移啦");
        }
        Integer orderbyUp = teachplanUp.getOrderby();
        teachplan.setOrderby(orderbyUp);
        teachplanUp.setOrderby(orderby);
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(teachplanUp);
    }

    /**
     * @param courseId 课程id
     * @param parentId 父课程计划id
     * @return int 最新排序号
     * @description 获取最新的排序号
     */
    private int getTeachplanCount(Long parentId, Long courseId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        return teachplanMapper.selectCount(queryWrapper);
    }
}
