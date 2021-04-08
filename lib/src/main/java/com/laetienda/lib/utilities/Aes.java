package com.laetienda.lib.utilities;

import java.security.GeneralSecurityException;

public interface Aes {

	/**
	 * 
	 * @param textToHash Text to be hashed that will be written in text file or database field
	 * @param hashPhrase Phrase used to hash text, it is required otherwise it will throw exception 
	 * @return
	 * @throws Exception 
	 * @throws AppException
	 */
	String encrypt(String textToHash, String hashPhrase) throws Exception;

	/**
	 * 
	 * @param encryptedText
	 * @param hashPhrasse
	 * @return
	 * @throws GeneralSecurityException 
	 * @throws AppException
	 */
	String decrypt(String encryptedText, String hashPhrasse) throws GeneralSecurityException;

}