package com.deotis.digitalars.service.common;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import com.deotis.digitalars.mapper.common.MenuMapper;
import com.deotis.digitalars.model.MasterCustomMenuData;
import com.deotis.digitalars.model.MasterMenu;
import com.deotis.digitalars.model.MasterMenuData;
import com.deotis.digitalars.util.collection.DMap;

/**
 * 
 * @author jongjin
 * @description service for main menu
 */
@Service
public class MenuService {

	private final SqlSessionTemplate sqlSession;
	
	public MenuService(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

	/**
	 * 메뉴 마스터 조회
	 * @param DMap<String, Object>
	 * @return List<MasterMenu>
	 */
	public MasterMenu getArsMenu(DMap<String, Object> params) {

		MenuMapper mapper = sqlSession.getMapper(MenuMapper.class);
		
		MasterMenu resultList = mapper.getArsMenu(params);
		
		return resultList;
	}
	
	/**
	 * 메뉴 트리 조회
	 * @param DMap<String, Object>
	 * @return List<MasterMenuData>
	 */
	public List<MasterMenuData> getArsMenuDataTree(DMap<String, Object> params) {

		MenuMapper mapper = sqlSession.getMapper(MenuMapper.class);
		
		List<MasterMenuData> resultList = mapper.getArsMenuDataTree(params);
		/*
		if(resultList.size() > 0) {
			int max_depth = resultList.get(0).getMax_menu_depth();
			for(int i = max_depth;i>=2;i--) {
				menuDataTreeCreate(resultList, i);
			}
		}
		*/
		
		return resultList;
	}
	
	
	/**
	 * 메뉴 트리 조회
	 * @param DMap<String, Object>
	 * @return List<MasterMenuData>
	 */
	public List<MasterMenuData> getAllMenuList(DMap<String, Object> params) {

		MenuMapper mapper = sqlSession.getMapper(MenuMapper.class);
		
		List<MasterMenuData> resultList = mapper.getArsMenuDataTree(params);
		
		return resultList;
	}
	
	
	/**
	 * 추천메뉴 마스터 조회
	 * @param DMap<String, Object>
	 * @return MasterMenu
	 */
	public MasterMenu getSuggestMenu(DMap<String, Object> params) {

		MenuMapper mapper = sqlSession.getMapper(MenuMapper.class);
		
		MasterMenu resultList = mapper.getSuggestMenu(params);
		
		return resultList;
	}
	
	/**
	 * 추천 메뉴 조회
	 * @param DMap<String, Object>
	 * @return List<DMap<String, Object>>
	 */
	public List<MasterCustomMenuData> getSuggestMenuTree(DMap<String, Object> params) {

		MenuMapper mapper = sqlSession.getMapper(MenuMapper.class);
		
		List<MasterCustomMenuData> resultList = mapper.getSuggestMenuTree(params);
		
		return resultList;
	}
	
	/*
	 * 커스텀메뉴 중 상위 메뉴 안의 sub_menu 배열로 하위메뉴를 넣기 위한 메서드
	 * 커스텀메뉴 구현이 완료됐을 때 필요없을 경우 삭제 예정입니다.
	 * @author Byeongok
	private List<MasterCustomMenuData> suggestMenuDataTreeCreate(List<MasterCustomMenuData> menu_list, int menu_depth) {
		List<MasterCustomMenuData> temp_list = new ArrayList<MasterCustomMenuData>();
		List<MasterCustomMenuData> list = new ArrayList<MasterCustomMenuData>();
		
		for(MasterCustomMenuData m : menu_list) {
			if(m.getMenu_depth() == menu_depth) {
				temp_list.add(m);
			}
		}
		list = suggestMenuDataTreeSubCreate(menu_list, temp_list, menu_depth);
		
		return list;
	}
	
	private List<MasterCustomMenuData> suggestMenuDataTreeSubCreate(List<MasterCustomMenuData> menu_list, List<MasterCustomMenuData> sub_list, int menu_depth) {
		List<MasterCustomMenuData> temp_list = new ArrayList<MasterCustomMenuData>();
		List<MasterCustomMenuData> sub = new ArrayList<MasterCustomMenuData>();
		MasterCustomMenuData sub_obj = new MasterCustomMenuData();
		
		MasterCustomMenuData md = new MasterCustomMenuData();
		
		for(MasterCustomMenuData m : menu_list) {
			boolean value = false;
			Iterator<MasterCustomMenuData> it = sub_list.iterator();
			while(it.hasNext()) {
				md = it.next();
				if(m.getMenu_seq().equals(md.getMenu_seq_parent())) {
					sub.add(md);
				}
				
				md = null;
			}
			value = sub.size() > 0 ? true : false;
			if(value) {
				sub_obj = m;
				sub_obj.setSub_menu(sub);
				
				temp_list.add(sub_obj);
				sub = null;
				sub = new ArrayList<MasterCustomMenuData>();
				sub_obj = null;
				sub_obj = new MasterCustomMenuData();
			}
		}
		
		return temp_list;
	}*/
	
	
}