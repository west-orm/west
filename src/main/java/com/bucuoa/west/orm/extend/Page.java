package com.bucuoa.west.orm.extend;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("rawtypes")
public class Page implements  Serializable {

	private static final long serialVersionUID = 20159200000000L;
	
	private int pageNo = 1;
	private int pageSize = 30;
	private int totalCount;
	private int totalPage;
	private List data;

	public Page(){
		
	}

	public List getData() {
		return data;
	}

	public void setData(List data) {
		this.data = data;
	}
	public boolean haveNextPage(){
		return pageNo<totalPage;
	}
	public boolean havePrevPage(){
		return pageNo>1;
	}
	public int getNextPageNo(){
		return pageNo+1;
	}
	public int getPrevPageNo(){
		return pageNo-1<1?1:(pageNo-1);
	}
	public int getPageNo() {
		return pageNo<=0?1:pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		this.totalPage = (totalCount+pageSize-1)/pageSize;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		this.totalPage = (totalCount+pageSize-1)/pageSize;
	}
	public int getTotalPage() {
		return totalPage<=0?1:totalPage;
	}
	
	public int getCurrentIndex(){
		return (pageNo - 1) * pageSize;
	}

}
