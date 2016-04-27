package com.bucuoa.west.orm.extend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bucuoa.west.orm.extend.Page;

@Service
public abstract class BaseService<T,PK extends Serializable>   {

	public abstract BaseDao<T,PK> getDao();

	@Transactional
	public Object saveEntity(T entity) throws Exception {
		 long start = System.currentTimeMillis();
		 Object saveEntity = getDao().saveEntity(entity);
		 long end = System.currentTimeMillis();
		 System.out.println("写入耗时："+(end-start));
		 return saveEntity;
	}
	
	public T save(T entity) throws Exception {
		 Object id = getDao().saveEntity(entity);
//		 entity.setId(Integer.parseInt(id+""));
		 return entity;
	}

	@Transactional
	public void update(T entity) throws Exception {
		getDao().updateEntity(entity);
	}

	public T findEntityById(PK id) throws Exception {
		return getDao().findEntityById(id);
	}
	
	public T findEntityByUUID(String uuid) throws Exception {
		return getDao().findEntityBy("uuid", "'"+uuid+"'");
	}

	public boolean deleteEntityById(PK id) throws Exception {
		return getDao().deleteEntityById(id);
	}
	
	public int getEntityCount(String[] keyName, String[] operate,
			Object[] keyValue, Object... ext) throws Exception {
		return getDao().getEntityCount(keyName, operate, keyValue, ext);
	}

	public List<T> findEntityList(String[] keyName,
			String[] operate, Object[] keyValue,String[] orderby,
			String[] pattern, int pageSize, int pageNo)
			throws Exception {
		return getDao().findEntityList(keyName, operate, keyValue,orderby,pattern, pageSize, pageNo);
	}
	
	public List<T> findEntityList(String[] keyName,
			String[] operate, Object[] keyValue, int pageSize, int pageNo)
			throws Exception {
		return getDao().findEntityList(keyName, operate, keyValue, pageSize, pageNo);
	}

	public List<T> myEntityList(String userId) throws Exception {
		return getDao().findEntityList(new String[] { "creater_id" },
				new String[] { "=" }, new Object[] { "'"+userId+"' " });
	}

	

}
