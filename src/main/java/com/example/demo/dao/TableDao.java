package com.example.demo.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.example.demo.entity.TableData;

public class TableDao {
	// 假数据，暂只支持一个字段（年龄）排序
	public List<TableData> queryUserList(int startIndex, int length, String sortName, final String sortType) {
		List<TableData> dataList = new ArrayList<TableData>();
		TableData tableData;
		SimpleDateFormat aDate=new SimpleDateFormat("yyyy-MM-dd");
		// 所有数据
		for (int i = 0; i < 1000; i++) {
			tableData = new TableData();
			tableData.setId(String.valueOf(i));
			tableData.setAge(10 + i);
			tableData.setCharacter("性格" + i);
			tableData.setCountry("国家" + i);
			tableData.setDate(aDate.format(new Date()));
			tableData.setHeight(String.valueOf(100 + i));
			tableData.setModel("轿车" + i);
			tableData.setWealth(String.valueOf(100000 + i));
			dataList.add(tableData);
		}
		// 只对年龄排序
		if ("age".equals(sortName)) {
			Collections.sort(dataList, new Comparator<TableData>() {
		          /**
		           * 返回负数表示：sprpi1小于sprpi2，  
		           * 返回0 表示：sprpi1和sprpi2相等，  
		           * 返回正数表示：sprpi1大于sprpi2。  
		           */
		          public int compare(TableData sprpi1, TableData sprpi2) {
		            if (sprpi1.getAge() >= sprpi2.getAge()) {
		            	if ("desc".equals(sortType)) {
		            		return -1;
		            	} else {
		            		return 1;
		            	}
		            } else {
		            	if ("asc".equals(sortType)) {
		            		return -1;
		            	} else {
		            		return 1;
		            	}
		            }
		          }
		    });
		}
		
		List<TableData> resultList = new ArrayList<TableData>();
		for (int i = startIndex; i < startIndex + length; i++) {
			resultList.add(dataList.get(i));
		}
		return resultList;
	}
}
