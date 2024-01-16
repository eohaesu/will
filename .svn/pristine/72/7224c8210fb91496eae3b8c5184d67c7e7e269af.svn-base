package com.deotis.digitalars.mapper.common;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.deotis.digitalars.model.MasterCustomMenuData;
import com.deotis.digitalars.model.MasterMenu;
import com.deotis.digitalars.model.MasterMenuData;
import com.deotis.digitalars.util.collection.DMap;

@Mapper
public interface MenuMapper {
	
	public MasterMenu getArsMenu(@Param("params") DMap<String, Object> params);
	public List<MasterMenuData> getArsMenuDataTree(@Param("params") DMap<String, Object> params);
	public MasterMenu getSuggestMenu(@Param("params") DMap<String, Object> params); 
	public List<MasterCustomMenuData> getSuggestMenuTree(@Param("params") DMap<String, Object> params); 
}
