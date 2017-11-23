ALTER TABLE stop_words
 ADD language CHAR(5) DEFAULT 'en' AFTER word;
 
 ALTER TABLE black_words
 ADD language CHAR(5) DEFAULT 'en' AFTER word;