package com.tfedorov.social.intention.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.tfedorov.social.intention.Purchase;

public class Intention2LevelResultSetExtractor implements
		ResultSetExtractor<Purchase> {

	@Override
	public Purchase extractData(ResultSet rs) throws SQLException,
			DataAccessException {

		Purchase il = new Purchase();
		il.setId(rs.getLong("id"));
		il.setPrimaryTest(rs.getString("primaryTest"));
		il.setSecondaryTest(rs.getString("secondaryTest"));
		il.setCategoryLevel1(rs.getString("categoryLevel1"));

		return il;
	}
}
