package com.org.server.chat.service;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.org.server.chat.domain.Sequence;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SequenceService {


	private final MongoTemplate mongo;

	public long next(String name) {
		Query q = new Query(Criteria.where("_id").is(name));
		Update u = new Update().inc("value", 1);
		FindAndModifyOptions opt = FindAndModifyOptions.options().upsert(true).returnNew(true);
		Sequence seq = mongo.findAndModify(q, u, opt, Sequence.class);
		return seq.getValue();
	}
}
