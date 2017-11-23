UPDATE topic_terms set term = left(term, 50);

ALTER TABLE topic_terms
 CHANGE term term CHAR(50) NOT NULL;
 
 UPDATE topic_bi_terms set term = left(term,100);
 
 ALTER TABLE topic_bi_terms
 CHANGE term term CHAR(100) NOT NULL;
 
 UPDATE topic_tri_terms set term = left(term, 150);
 
 ALTER TABLE topic_tri_terms
 CHANGE term term CHAR(150) NOT NULL;