/*
 * Copyright 2015-2101 gaoxianglong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gxl.kratos.jdbc.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gxl.kratos.utils.sequence.DbConnectionManager;
import com.gxl.kratos.utils.sequence.SequenceIDManger;

@RunWith(SpringJUnit4ClassRunner.class)
/* 库内分片配置文件 */
@ContextConfiguration(locations = "classpath*:kratos1-context.xml")
/* 一库一片配置文件 */
//@ContextConfiguration(locations = "classpath*:kratos2-context.xml")
public class CRUDTest {
	static {
		/* 初始化数据源信息 */
		DbConnectionManager.init("root", "88888888", "jdbc:mysql://114.215.110.169:3306/um_id", "com.mysql.jdbc.Driver");
	}

	@Resource
	private UserDao userDao;

	/**
	 * 库内分片,insert测试
	 * 
	 * @author gaoxianglong
	 */
	public @Test void testInsert() {
		long sequenceID = SequenceIDManger.getSequenceId(1, 1, 500);
		System.out.println("sequenceID->" + sequenceID);
		userDao.addUser(sequenceID);
	}

	/**
	 * 库内分片,批量insert测试
	 * 
	 * @author gaoxianglong
	 */
	public @Test void testInsert2() {
		for (int i = 0; i < 20; i++) {
			long sequenceID = SequenceIDManger.getSequenceId(1, 1, 500);
			System.out.println("sequenceID->" + sequenceID);
			System.out.println(sequenceID % 16);
			//userDao.addUser(sequenceID);
		}
	}

	/**
	 * 库内分片,根据分库分表条件进行检索数据
	 * 
	 * @author gaoxianglong
	 */
	public @Test void testQuery1() {
		User user = userDao.getUserbyId(18566689320L);
		System.out.println(user.getUserinfo_Id());
	}

	/**
	 * 库内分片,insert反向索引表
	 * 
	 * @author gaoxianglong
	 */
	public @Test void testInsert3() {
		final String EMAIL = "huangfeihong9@sina.com";
		long sequenceID = SequenceIDManger.getSequenceId(1, 1, 500);
		System.out.println("sequenceID->" + sequenceID);
		userDao.addUser(sequenceID);
		EmailIndex emailIndex = new EmailIndex();
		emailIndex.setEmail(EMAIL);
		emailIndex.setEmail_hash(Math.abs(EMAIL.hashCode()));
		emailIndex.setUserinfo_id(sequenceID);
		userDao.addEmailIndex(emailIndex);
	}

	/**
	 * 库内分片,根据反向索引表检索主表数据
	 * 
	 * @author gaoxianglong
	 */
	public @Test void testQuery2() {
		final String EMAIL = "huangfeihong9@sina.com";
		EmailIndex emailIndex = new EmailIndex();
		emailIndex.setEmail(EMAIL);
		emailIndex.setEmail_hash(Math.abs(EMAIL.hashCode()));
		emailIndex = userDao.getEmailIndex(emailIndex);
		System.out.println(userDao.getUserbyId(emailIndex.getUserinfo_id()).getUserinfo_Id());
	}

	/**
	 * 库内分片,批量insert反向索引表
	 * 
	 * @author gaoxianglong
	 */
	public @Test void testInsert4() {
		final String EMAILS[] = { "huangfeihong2@sina.com", "Johngao12@sina.com", "wanglaowu21@sina.com",
				"lisi12@sina.com", "zhangsan3@sina.com" };
		for (int i = 0; i < EMAILS.length; i++) {
			long sequenceID = SequenceIDManger.getSequenceId(1, 1, 500);
			System.out.println("sequenceID->" + sequenceID);
			userDao.addUser(sequenceID);
			EmailIndex emailIndex = new EmailIndex();
			emailIndex.setEmail(EMAILS[i]);
			emailIndex.setEmail_hash(Math.abs(EMAILS[i].hashCode()));
			emailIndex.setUserinfo_id(sequenceID);
			userDao.addEmailIndex(emailIndex);
		}
	}
}