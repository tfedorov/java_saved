select  biterms.terms_count,biterms.term  FROM (SELECT id, topic_id, SUBSTRING_INDEX(term, ' ',1) as term1, SUBSTRING_INDEX(term, ' ',-1) as term2, term, terms_count, adatetime 
FROM topic_bi_terms_p where topic_id = 9 and adatetime = '2013-04-24 07:00:00' and terms_count > 1 and period = 1 order by terms_count desc limit 2415) 
as biterms  JOIN (SELECT term, terms_count FROM topic_terms_p where topic_id = 9 and adatetime = '2013-04-23 07:00:00'  and period = 1 and terms_count > 0 
order by terms_count desc limit 70) singleterm1  ON (biterms.term1 = singleterm1.term)  JOIN  (SELECT term, terms_count FROM topic_terms_p where topic_id = 5 
and adatetime = '2013-04-23 07:00:00'  and period = 1 and terms_count > 0 order by terms_count desc limit 70) singleterm2  ON (biterms.term2 = singleterm2.term)


select  biterms.terms_count,biterms.term  FROM (SELECT id, topic_id, SUBSTRING_INDEX(term, ' ',1) as term1, SUBSTRING_INDEX(term, ' ',-1) as term2, term, terms_count, adatetime 
FROM topic_bi_terms where topic_id = 9 and adatetime = '2013-04-23 07:00:00' and terms_count > 0  order by terms_count desc limit 2415) 
as biterms  JOIN (SELECT term, terms_count FROM topic_terms where topic_id = 9 and adatetime = '2013-04-23 07:00:00'  and terms_count > 0 
order by terms_count desc limit 70) singleterm1  ON (biterms.term1 = singleterm1.term)  JOIN  (SELECT term, terms_count FROM topic_terms where topic_id = 5 
and adatetime = '2013-04-23 07:00:00'  and terms_count > 0 order by terms_count desc limit 70) singleterm2  ON (biterms.term2 = singleterm2.term)

select * from topic_terms where topic_id=9 order by terms_count desc