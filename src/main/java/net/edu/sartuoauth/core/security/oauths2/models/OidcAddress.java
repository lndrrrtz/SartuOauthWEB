package net.edu.sartuoauth.core.security.oauths2.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("address")
public class OidcAddress implements Serializable {

	private static final long serialVersionUID = 5490370206884272540L;

	private String streetAddress;
	private String locality; // city
	private String region; // state
	private String postalCode;// zip/postcode
	private String country;

	@Override
	public String toString() {
		return "{" +
				"\"streetAddress\": \"" + streetAddress + '\"' +
				", \"locality\": \"" + locality + '\"' +
				", \"region\": \"" + region + '\"' +
				", \"postalCode\": \"" + postalCode + '\"' +
				", \"country\": \"" + country + '\"' +
				'}';
	}
}
