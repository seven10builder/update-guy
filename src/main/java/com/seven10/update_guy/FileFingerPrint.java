package com.seven10.update_guy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.input.AutoCloseInputStream;

public class FileFingerPrint
{
	public static final int encodedLength = 32;	// for MD5 or SHA128
	// public static final int encodedLength = 64; // if we are using SHA256
	
	public static String create(File file) throws FileNotFoundException, IOException
	{
		final InputStream fis = new AutoCloseInputStream(new FileInputStream(file));
		return new String(Hex.encodeHex(DigestUtils.md5(fis)));
	}
	public static String create(String json) throws FileNotFoundException, IOException
	{
		return new String(Hex.encodeHex(DigestUtils.md5(json)));
	}
	public static String create(byte[] bytes) throws FileNotFoundException, IOException
	{
		return new String(Hex.encodeHex(DigestUtils.md5(bytes)));
	}

}
