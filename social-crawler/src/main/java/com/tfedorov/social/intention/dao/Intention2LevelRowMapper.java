package com.tfedorov.social.intention.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.tfedorov.social.intention.Purchase;

public class Intention2LevelRowMapper implements RowMapper<Purchase> {

	@Override
	public Purchase mapRow(ResultSet rs, int rowNum) throws SQLException {
		Intention2LevelResultSetExtractor extractor = new Intention2LevelResultSetExtractor();
		return extractor.extractData(rs);
	}

}
