package com.rivetlabs.scribe.oauth

import org.scribe.builder.api.Api
import org.scribe.model.SignatureType

class OAuthServiceConfig {

	String apiKey;
	String apiSecret;
	String callback;
	Class apiClass;
	String scope
	SignatureType signatureType
	Boolean debug
	String successUri
	String failureUri
	String protectedResource
	int connectTimeout
	int readTimeout
	
	public OAuthServiceConfig(ConfigObject conf) {
		apiKey = conf.apiKey
		apiSecret = conf.apiSecret
		callback = conf.callback
		apiClass = conf.apiClass
		
		if (conf.scope) scope = conf.scope
		if (conf.signatureType) signatureType = conf.signatureType
		debug = conf.debug
		successUri = conf.successUri
		failureUri = conf.failureUri
		protectedResource = conf.protectedResource
		
		validateRequiredParams()
	}
	
	public String toString() {
		return "OAuthServiceConfig [apiKey=" + apiKey + ", apiSecret=" + apiSecret + ", callback=" + callback + ", apiClass=" + apiClass + "]";
	}
	
	private void validateRequiredParams() {
		if (!apiKey) {
			throw new IllegalStateException("OAuth apiKey configuration not found")
		}
		if (!apiSecret) {
			throw new IllegalStateException("OAuth apiSecret configuration not found")
		}
	}

}
