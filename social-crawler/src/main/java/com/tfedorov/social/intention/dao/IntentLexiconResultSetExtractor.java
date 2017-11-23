package com.tfedorov.social.intention.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.tfedorov.social.intention.IntentLexicon;
import com.tfedorov.social.intention.Qualification;
import com.tfedorov.social.qualification.util.QuailficationUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class IntentLexiconResultSetExtractor implements
		ResultSetExtractor<IntentLexicon> {

	@Override
	public IntentLexicon extractData(ResultSet rs) throws SQLException,
			DataAccessException {

		IntentLexicon il = new IntentLexicon();
		il.setId(rs.getLong("id"));
		il.setSearchTerm(rs.getString("term"));

		String qualificationsStr = rs.getString("qualification");
		List<Qualification> qualifications = QuailficationUtil.buildQuaificationStr(qualificationsStr);
		il.setQualifications(qualifications);
		return il;
	}


}
