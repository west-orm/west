package west.orm.core;

import com.bucuoa.west.orm.core.SQLConverter;

public class TestSQL {

	public static void main(String[] args) {
		
		Activity act = new Activity();
		act.setNumLimit(100);
		act.setTitle("生成代码");
		
		try {
			String insertSql = SQLConverter.insertSql(act);
			System.out.println(insertSql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
