#!/usr/bin/env python
# -*- coding: utf-8 -*-

import json
import re
import time
import urllib
import urllib2
import MySQLdb
from tweepy.streaming import StreamListener
from tweepy import OAuthHandler
from tweepy import Stream
from tweepy.utils import import_simplejson, urlencode_noplus
json = import_simplejson()

class Connector(object):
  def __init__(self):
    self._cursor = None
    self._connection = None

  def close(self):
    if self._cursor:
      self._cursor.close()
    if self._connection:
      self._connection.commit()
      self._connection.close()
    self._connection = None

  def commit(self):
    if self._connection:
      self._connection.commit()

  def connect(self, **param):
    if self._connection == None:
      charset = ''
      if 'charset' in param:
        charset = param['charset']
      self._connection = MySQLdb.connect(**param)
      if charset:
        self._connection.set_character_set(charset)
      self._cursor = self._connection.cursor()
      if charset:
        self._cursor.execute('SET NAMES ' + charset)
        self._cursor.execute('SET CHARACTER SET ' + charset)
        self._cursor.execute('SET character_set_connection=' + charset)

  def create(self, table, types):
    query = 'CREATE TABLE `%s` (%s)' % (table, u', '.join(self._prepareTypes(types)))
    return self.execute(query)

  def delete(self, table, criteria=[]):
    (conditions, params) = self._prepareCriteria(criteria)
    query = [
      'DELETE FROM `%s`' % table,
      conditions and ' WHERE %s' % ' AND '.join(conditions) or ''
    ]
    return self.execute(''.join(query), params)

  def describe(self, table):
    return self.execute('DESCRIBE `%s`' % table)

  def drop(self, table):
    return self.execute('DROP TABLE `%s`' % table)

  def execute(self, query, param=()):
    query = unicode(query)
    opts = ()
    for value in param:
      opts += (value,)
    self._cursor.execute(query, opts)
    return self._cursor.fetchall()

  def getLastId(self):
    return self._connection.insert_id()

  def getTables(self):
    result = []
    for item in self.execute('SHOW TABLES'):
      result.append(item[0])
    return result

  def insert(self, table, obj):
    keys = []
    values = []
    params = ()
    for key in obj:
      keys.append('`' + key + '`')
      values.append('%s')
      params += (unicode(obj[key]),)
    query = [
      'INSERT INTO `%s`' % table,
      ' (%s)' % ', '.join(keys),
      ' VALUES (%s)' % ', '.join(values)
    ]
    return self.execute(''.join(query), params)

  def select(self, table, columns=[], criteria=[], order=[], limit=0, offset=0):
    (conditions, params) = self._prepareCriteria(criteria)
    query = [
      'SELECT',
      columns and ' `%s`' % '`, `'.join(columns) or ' *',
      ' FROM `%s`' % table,
      conditions and ' WHERE %s' % ' AND '.join(conditions) or '',
      order and ' ORDER BY %s' % ', '.join(order) or '',
      limit and ' LIMIT %d,%d' % (offset, limit) or ''
    ]
    return self.execute(''.join(query), params)

  def update(self, table, obj, criteria=[]):
    (conditions, params) = self._prepareCriteria(criteria)
    keys = []
    values = ()
    for key in obj:
      keys.append('`' + key + '` = %s')
      values += (obj[key],)
    query = [
      'UPDATE `%s` SET' % table,
      ' %s' % ', '.join(keys),
      conditions and ' WHERE %s' % ' AND '.join(conditions) or ''
    ]
    return self.execute(''.join(query), values + params)

  def getDescription(self):
    return self._cursor.description

  def _prepareCriteria(self, criteria):
    conditions = []
    params = ()
    for c in criteria:
      if type(c) == tuple and len(c) == 3:
        conditions.append('`%s` %s %%s' % (c[0], c[1]))
        params += (c[2],)
      else:
        conditions.append('%s' % c)
    return (conditions, params)

  def _prepareTypes(self, types):
    result = []
    for key in types.keys():
      result.append('`%s` %s' % (key, types[key]))
    return result


class ProtoListener(StreamListener):
  """ A listener handles tweets are the received from the stream. 
  This is a basic listener that just prints received tweets to stdout.
  https://github.com/tweepy/tweepy/blob/master/tweepy/streaming.py
  """
  keywords = {}
  env = {}

  def setEnv(self, env):
    self.env = env

  def setKeywords(self, keywords):
    self.keywords = keywords

  def convertDate(self, d):
    d = d.split(' ')
    return d[5] + '-' + '%2d' % (['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep',
        'Oct', 'Nov', 'Dec'].index(d[1]) + 1) + '-' + d[2] + ' ' + d[3]

  def tweetModel(self, topic, keyword, obj):
    model = {
      'topic_id': topic,
      'keyword': keyword
    }
    for key in [
        ('id', 'id', 0),
        ('text', 'text', ''),
        ('from_user_id', 'from_user_id', 0),
        ('from_user', 'from_user', ''),
        ('from_user_name', 'from_user_name', ''),
        ('to_user_id', 'to_user_id', 0),
        ('to_user', 'to_user', ''),
        ('to_user_name', 'to_user_name', ''),
        ('iso_language_code', 'iso_language_code', ''),
        ('profile_image_url', 'profile_image_url', ''),
        ('profile_image_url_https', 'profile_image_url_https', ''),
        ('source', 'source', ''),
        ('created_at', 'created_at', 'date'),
        ('retweet_count', 'retweet_count', 0)
        ]:
      if key[1] in obj:
        if key[2] == 'date':
          model[key[0]] = self.convertDate(obj[key[1]])
        else:
          model[key[0]] = obj[key[1]]
      else:
        if key[2] == 'date':
          model[key[0]] = '0000-00-00 00:00:00'
        else:
          model[key[0]] = key[2]
    return model

  def on_data(self, data):
    result = super(self.__class__, self).on_data(data)
    if not result is False:
      tweet = json.loads(data)
      if 'text' in tweet:
        for topic in self.env['topics']:
          for keyword in self.env['topics'][topic]:
            if (' ' + self.env['separator'].sub(' ',
                tweet['text'].lower()) + ' ').find(' ' + keyword + ' ') >= 0:
              print 'Matched: ' + keyword + ', ' + tweet['text']

            # store raw tweets by topic and keyword
            # for topic in self.env['keywords'][keyword]:
            #   self.env['db'].insert('tweet', self.tweetModel(topic, keyword, tweet))

            # aggregate topic mentions timeline
              topics = self.env['db'].select('agg_tweets_by_topics', ['tweets_count'],
                  [('topic_id', '=', topic),
                  ('day', '=', self.convertDate(tweet['created_at'])[:10])])
              if topics:
                self.env['db'].update('agg_tweets_by_topics',
                    {'tweets_count': topics[0][0] + 1},
                    [('topic_id', '=', topic),
                    ('day', '=', self.convertDate(tweet['created_at'])[:10])])
              else:
                self.env['db'].insert('agg_tweets_by_topics', {'topic_id': topic,
                    'day': self.convertDate(tweet['created_at'])[:10],
                    'tweets_count': 1})

              # aggregate top retweets
              if 'retweet_count' in tweet and tweet['retweet_count'] > 0 and \
                  'retweeted_status' in tweet:
                retweeted = tweet['retweeted_status']
                if (' ' + self.env['separator'].sub(' ',
                    retweeted['text'].lower()) + ' ').find(' ' + keyword + ' ') >= 0:
                  print 'Matched retweeted: ' + keyword + ', ' + retweeted['text']

                  followers = 0
                  if 'user' in tweet and 'followers_count' in tweet['user']:
                    followers += tweet['user']['followers_count']

                  tweets = self.env['db'].select('popular_tweets', ['recent_retweets',
                      'estimated_reach'], [('topic_id', '=', topic),
                      ('tweet_id', '=', retweeted['id'])])
                  if tweets:
                    self.env['db'].update('popular_tweets',
                        {'recent_retweets': tweet['retweet_count'],
                        'estimated_reach': tweets[0][1] + followers},
                        [('topic_id', '=', topic),
                        ('tweet_id', '=', retweeted['id'])])
                  else:
                    if 'user' in retweeted and 'followers_count' in retweeted['user']:
                      followers += retweeted['user']['followers_count']
                    self.env['db'].insert('popular_tweets', {'topic_id': topic,
                        'tweet_id': retweeted['id'], 'text': retweeted['text'],
                        'created_at': self.convertDate(retweeted['created_at']),
                        'recent_retweets': tweet['retweet_count'], 'replies': 0,
                        'estimated_reach': followers,
                        'from_user_id': retweeted['user']['id'],
                        'from_user': retweeted['user']['name']})

              # aggregate tag of terms
              for term in self.env['separator'].sub(' ', tweet['text'].lower()).split(' '):
                if len(term) >= 3 and not term in self.env['topics'][topic] and \
                    not term in self.env['stopwords'] and \
                    not self.env['excludes'].search(term):
                  terms = self.env['db'].select('agg_terms_by_topics', ['terms_count'],
                      [('topic_id', '=', topic),
                      ('term', '=', term),
                      ('day', '=', self.convertDate(tweet['created_at'])[:10])])
                  if terms:
                    self.env['db'].update('agg_terms_by_topics',
                        {'terms_count': terms[0][0] + 1},
                        [('topic_id', '=', topic),
                        ('term', '=', term),
                        ('day', '=', self.convertDate(tweet['created_at'])[:10])])
                  else:
                    self.env['db'].insert('agg_terms_by_topics', {'topic_id': topic,
                        'day': self.convertDate(tweet['created_at'])[:10],
                        'term': term, 'terms_count': 1})

        # aggregate intents
        if 'retweet_count' in tweet and tweet['retweet_count'] > 0 and \
            'retweeted_status' in tweet:
          retweeted = tweet['retweeted_status']
          # TODO: rewrite 60 days calculation
          if 'user' in retweeted:
            users = self.env['db'].select('popular_tweets', ['topic_id'],
                [('from_user_id', '=', retweeted['user']['id']),
                ('created_at', '>', '0000-00-00')])
            if users:
              found = None
              text = ' ' + self.env['separator'].sub(' ', retweeted['text'].lower()) + ' '
              for term in self.env['intents']:
                a = text.find(' ' + term + ' ')
                if a >= 0:
                  for intent in self.env['intents'][term]:
                    if len(intent) == 0:
                      found = (term, '')
                      break
                    elif len(intent) == 3:
                      b = text.find(' ' + intent[0] + ' ')
                      if b >= 0:
                        if text.count(' ', min(a, b), max(a, b)) <= intent[int(a > b) + 1]:
                          print 'Found:', a, b, text.count(' ', min(a, b), max(a, b)), term, intent, tweet['text']
                          found = (term, intent[0])
                          break
              if found:
                text = ' ' + retweeted['text'] + ' '
                if found[0]:
                  r = re.compile(r'([^a-zA-Z0-9\'])(' + found[0] + ')([^a-zA-Z0-9\'])', re.I)
                  text = r.sub(r'\1<span class="intent_term">\2</span>\3', text)
                if found[1]:
                  r = re.compile(r'([^a-zA-Z0-9\'])(' + found[1] + ')([^a-zA-Z0-9\'])', re.I)
                  text = r.sub(r'\1<span class="intent_qualification">\2</span>\3', text)
                text = text.strip()

                followers = 0
                if 'user' in tweet and 'followers_count' in tweet['user']:
                  followers += tweet['user']['followers_count']

                tweets = self.env['db'].select('intention_tweets', ['recent_retweets',
                    'estimated_reach'], [('topic_id', '=', users[0][0]),
                    ('tweet_id', '=', retweeted['id'])])
                if tweets:
                  self.env['db'].update('intention_tweets',
                      {'recent_retweets': tweet['retweet_count'],
                      'estimated_reach': tweets[0][1] + followers},
                      [('topic_id', '=', users[0][0]),
                      ('tweet_id', '=', retweeted['id'])])
                else:
                  if 'user' in retweeted and 'followers_count' in retweeted['user']:
                    followers += retweeted['user']['followers_count']
                  self.env['db'].insert('intention_tweets', {'topic_id': users[0][0],
                      'tweet_id': retweeted['id'], 'text': text,
                      'created_at': self.convertDate(retweeted['created_at']),
                      'recent_retweets': tweet['retweet_count'], 'replies': 0,
                      'estimated_reach': followers,
                      'from_user_id': retweeted['user']['id'],
                      'from_user': retweeted['user']['name']})

        # cron job for topic status
        if time.time() > self.env['timestamp'] + 300:
          self.env['timestamp'] = time.time()
          for topic in self.env['db'].select('topic', ['id', 'status'],
              ['status >= 0']):
            status = 0
            if self.env['db'].select('agg_tweets_by_topics', [],
                [('topic_id', '=', topic[0])], [], 1):
              status += 1
            if self.env['db'].select('agg_terms_by_topics', [],
                [('topic_id', '=', topic[0])], [], 1):
              status += 1
            if self.env['db'].select('popular_tweets', [],
                [('topic_id', '=', topic[0])], [], 1):
              status += 1
            #if self.env['db'].select('intention_tweets', [],
            #    [('topic_id', '=', topic[0])], [], 1):
            #  status += 1
            if status == 0:
              self.env['db'].update('topic', {'status': 0}, [('id', '=', topic[0])])
            elif status == 3:
              self.env['db'].update('topic', {'status': 3}, [('id', '=', topic[0])])
            else:
              self.env['db'].update('topic', {'status': 1}, [('id', '=', topic[0])])

          topics = {}
          keywords = {}
          for topic in self.env['db'].select('topic', ['id', 'keywords'], ['status >= 0']):
            topics[topic[0]] = [x.strip().lower() for x in topic[1].split(',')]
            for keyword in topics[topic[0]]:
              if keyword in keywords:
                keywords[keyword].append(topic[0])
              else:
                keywords[keyword] = [topic[0]]
          self.env['topics'] = topics
          self.env['keywords'] = keywords

          stopwords = []
          for stopword in self.env['db'].select('stop_words', ['word']):
            stopwords.append(stopword[0])
          self.env['stopwords'] = stopwords

          intents = {}
          f = open('intents.tsv', 'r')
          for row in f.readlines():
            intent = row.split('\t')
            term = intent[0].replace('"', '').replace('*', '').strip()
            if not term in intents:
              intents[term] = []
            for qualification in intent[1].strip().split(','):
              if not qualification.strip():
                intents[term].append(())
              else:
                rule = qualification.strip().split(' ')
                if len(rule) == 2 and rule[0].startswith('&'):
                  direction = rule[0][1:].split('.')
                  if len(direction) == 2:
                    try:
                      intents[term].append((rule[1], int(direction[0]) + 1, int(direction[1]) + 1))
                    except: pass
          f.close()
          self.env['intents'] = intents

        self.env['db'].commit()

    #return False #result
    return result

  def on_error(self, status):
    print 'status:', status
    return False


def main():
  # Go to http://dev.twitter.com and create an app. 
  # The consumer key and secret will be generated for you after
  consumer_key = 'smjHQmVSsXa5sX540s96zg'
  consumer_secret = 'JjJTbIayXCgVh60QNDp1xe4rAEwSair0m7yrBvnAWI'

  # After the step above, you will be redirected to your app's page.
  # Create an access token under the the "Your access token" section
  access_token = '943168566-JU9LZeSoJ0z5H0ODUzdPZpg0pm851tzGz32ANbb5'
  access_token_secret = '4NAKHmdIZcgPZVtdrs1BnoTsSPCy6xqAF827T95HA'

  # tweepy.debug(True, 10)
  env = {}
  db = Connector()
  db.connect(host='localhost', user='dmp', passwd='dmp01', db='scorecard',
      charset='utf8', use_unicode=True)
  proto = ProtoListener()

  topics = {}
  keywords = {}
  for topic in db.select('topic', ['id', 'keywords'], ['status >= 0']):
    topics[topic[0]] = [x.strip().lower() for x in topic[1].split(',')]
    for keyword in topics[topic[0]]:
      if keyword in keywords:
        keywords[keyword].append(topic[0])
      else:
        keywords[keyword] = [topic[0]]

  stopwords = []
  for stopword in db.select('stop_words', ['word']):
    stopwords.append(stopword[0])

  intents = {}
  f = open('intents.tsv', 'r')
  for row in f.readlines():
    intent = row.split('\t')
    term = intent[0].replace('"', '').replace('*', '').strip()
    if not term in intents:
      intents[term] = []
    for qualification in intent[1].strip().split(','):
      if not qualification.strip():
        intents[term].append(())
      else:
        rule = qualification.strip().split(' ')
        if len(rule) == 2 and rule[0].startswith('&'):
          direction = rule[0][1:].split('.')
          if len(direction) == 2:
            try:
              intents[term].append((rule[1], int(direction[0]) + 1, int(direction[1]) + 1))
            except: pass
  f.close()

  env['db'] = db
  env['topics'] = topics
  env['keywords'] = keywords
  env['stopwords'] = stopwords
  env['intents'] = intents
  env['timestamp'] = 0
  env['separator'] = re.compile(r'[\,\.\;\:\!\?\[\]\{\}\(\)\s]+', re.M)
  env['excludes'] = re.compile(r'[^a-zA-Z0-9\']+')
  proto.setEnv(env)

  auth = OAuthHandler(consumer_key, consumer_secret)
  auth.set_access_token(access_token, access_token_secret)
  stream = Stream(auth, proto)
  stream.sample()

  db.close()


if __name__ == "__main__":
  main()
