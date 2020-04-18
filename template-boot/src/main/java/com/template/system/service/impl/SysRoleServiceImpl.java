package com.template.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.template.system.config.AdminConstant;
import com.template.system.domain.dto.RoleDTO;
import com.template.system.entity.SysMenu;
import com.template.system.entity.SysRole;
import com.template.system.mapper.SysRoleMapper;
import com.template.system.service.SysMenuService;
import com.template.system.service.SysRoleService;
import com.template.system.util.EntityUtil;
import com.template.system.util.RightsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-05-28
 * @version 1.0.0
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

	@Autowired
	private SysMenuService sysMenuService;

	@Override
	public RoleDTO get(Integer roleId) {
		RoleDTO role = BeanUtil.toBean(this.baseMapper.selectById(roleId), RoleDTO.class);
		if (RightsUtil.checkBigInteger(role.getRights())) {
			LambdaQueryWrapper<SysMenu> queryWrapper = Wrappers.<SysMenu>lambdaQuery().select(SysMenu::getMenuId);
			List<SysMenu> list = sysMenuService.list(queryWrapper);
			if (CollUtil.isNotEmpty(list)) {
				role.setMenuIds(list.stream().filter(m -> RightsUtil.testRights(role.getRights(), m.getMenuId()))
						.map(SysMenu::getMenuId).collect(Collectors.toList()));
			}
		}
		if (ObjectUtil.isNull(role.getMenuIds())) {
			role.setMenuIds(Collections.emptyList());
		}
		return role;
	}

	@Override
	public List<RoleDTO> list(String roles) {
		List<RoleDTO> list = EntityUtil.listConver(baseMapper.selectList(null), RoleDTO.class);
		if (!CollUtil.isEmpty(list) && StrUtil.isNotBlank(roles)) {
			list.stream().filter(r -> RightsUtil.testRights(roles, r.getRoleId())).forEach(r -> r.setRoleSign("true"));
		}
		return list;
	}

	@Override
	public boolean save(RoleDTO role) {
		// 计算权限
		if (CollUtil.isNotEmpty(role.getMenuIds())) {
			BigInteger rights = RightsUtil.sumRights(role.getMenuIds());
			role.setRights(rights.toString());
		}
		SysRole sysRole = BeanUtil.toBean(role, SysRole.class);
		int count = this.baseMapper.insert(sysRole);
		return count > 0;
	}

	@Override
	public boolean update(RoleDTO role) {
		// 计算权限
		if (CollUtil.isNotEmpty(role.getMenuIds())) {
			BigInteger rights = RightsUtil.sumRights(role.getMenuIds());
			role.setRights(rights.toString());
		} else {
			role.setRights(AdminConstant.RIGHTS_DEFAULT_VALUE);
		}
		int r = this.baseMapper.updateById(BeanUtil.toBean(role, SysRole.class));
		return r > 0;
	}

	@Override
	public boolean remove(Integer roleId) {
		int del = this.baseMapper.deleteById(roleId);
		return del > 0;
	}

	@Override
	public boolean batchRemove(Integer[] roleIds) {
		int del = this.baseMapper.deleteBatchIds(CollUtil.toList(roleIds));
		return del > 0;
	}
}
