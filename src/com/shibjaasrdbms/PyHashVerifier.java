package com.shibjaasrdbms;

import java.io.*;
import javax.xml.bind.DatatypeConverter;

public class PyHashVerifier {

	protected String pyExePath;
	protected String pyModulePath;

	public PyHashVerifier(String pyExePath, String pyModulePath) {
		this.pyExePath = pyExePath;
		this.pyModulePath = pyModulePath;
	}

	public Boolean verify(String pw, String hash) throws IOException {
                String pw1 = new String(DatatypeConverter.printBase64Binary(pw.getBytes()));
                String hash1 = new String(DatatypeConverter.printBase64Binary(hash.getBytes()));

                Process p = Runtime.getRuntime().exec(pyExePath + " " + pyModulePath + " " + pw1 + " " + hash1);

                BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

                String s = stdInput.readLine();

                if (s.equals("2"))
                        throw new UnsupportedOperationException("Hash algorithm not supported");
                else if (s.equals("1"))
                        return true;
		else
	                return false;
	}
}
