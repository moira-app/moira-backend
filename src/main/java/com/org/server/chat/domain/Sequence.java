package com.org.server.chat.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Document
@Getter
@RequiredArgsConstructor
public class Sequence {

	@Id
	private String id;
	private long value;
}
