delete from stemming where id in 
(
select mxid from (select word, stemmed_word, lang, count(id), max(id) as mxid from stemming 
group by word, stemmed_word, lang having count(id) > 1) as dbt
);