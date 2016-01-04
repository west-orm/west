package west.orm.core;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "activity")
public class Activity implements Serializable  {

	@Transient 
	private static final long serialVersionUID = -4439103201517663695L;

	@Column(name = "id")
	@Id()
	private int id; // id

	@Column(name = "title")
	private String title; // 名称

	@Column(name = "num_limit")
	private int numLimit; // 人数限制

	@Column(name = "start_time")
	private Date startTime; // 开始时间

	@Column(name = "create_time")
	private Date createTime; // 创建

	@Column(name = "place_name")
	private String placeName; // 活动场所

	@Column(name = "place_id")
	private int placeId; // 场所

	@Column(name = "address")
	private String address; // 地址

	@Column(name = "contract_name")
	private String contractName; // 联系人

	@Column(name = "status")
	private int status; // 状态

	@Column(name = "creater_id")
	private int createrId; // 创建人

	@Column(name = "creater")
	private String creater; // 创建人

	@Column(name = "memo")
	private String memo; // 备注
	
	@Column(name = "uuid")
	private String uuid; // uuid
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getNumLimit() {
		return numLimit;
	}

	public void setNumLimit(int numLimit) {
		this.numLimit = numLimit;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public int getPlaceId() {
		return placeId;
	}

	public void setPlaceId(int placeId) {
		this.placeId = placeId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCreaterId() {
		return createrId;
	}

	public void setCreaterId(int createrId) {
		this.createrId = createrId;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}
