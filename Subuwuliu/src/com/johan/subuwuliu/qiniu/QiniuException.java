package com.johan.subuwuliu.qiniu;

import java.io.IOException;

public class QiniuException extends IOException {
	private static final long serialVersionUID = 1L;
	public final Response response;

	public QiniuException(Response response) {
		this.response = response;
	}

	public QiniuException(Exception e) {
		super(e);
		this.response = null;
	}

	public String url() {
		return response.url();
	}

	public int code() {
		return response == null ? -1 : response.statusCode;
	}
}