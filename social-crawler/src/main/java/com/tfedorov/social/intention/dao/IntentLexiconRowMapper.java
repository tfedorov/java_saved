package com.tfedorov.social.intention.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.tfedorov.social.intention.IntentLexicon;
import org.springframework.jdbc.core.RowMapper;

public class IntentLexiconRowMapper implements RowMapper<IntentLexicon> {

	@Override
	public IntentLexicon mapRow(ResultSet rs, int rowNum) throws SQLException {
		IntentLexiconResultSetExtractor extractor = new IntentLexiconResultSetExtractor();
		return extractor.extractData(rs);
	}

}
