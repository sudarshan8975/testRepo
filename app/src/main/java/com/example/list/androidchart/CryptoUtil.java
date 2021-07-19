package com.example.list.androidchart;

import android.os.Environment;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.Key;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

public class CryptoUtil
{
		static SecretKeySpec originalKey;
		static KeyGenerator keyGen;
		static Key _key;
        private static int AES_128 = 128;
		public static String generateXML(String[] xmlTags, String[] valuesToEncrypt)
		{
		    	//Security.addProvider(new BouncyCastleProvider());
			String xml = new String();
			StringWriter writer = null;
			StreamResult streamResult = null;
			TransformerHandler hd = null;
			AttributesImpl atts = null;
			try
			{
				writer = new StringWriter();
				streamResult = new StreamResult(writer);
				SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory
						.newInstance();
				hd = tf.newTransformerHandler();
				Transformer serializer = hd.getTransformer();
				serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
				serializer.setOutputProperty(OutputKeys.INDENT, "yes");
				hd.setResult(streamResult);
				hd.startDocument();
				atts = new AttributesImpl();
				hd.startElement("", "", "XMLDATA", atts); //<XMLDATA>
				
			}
			catch (Exception e) {
				// TODO: handle exception
				System.out.println("In generateXML Exc-001 :"+e.toString());
				return null;
			}
			
			System.out.println("xmlTags.length :"+xmlTags.length);
			System.out.println("valuesToEncrypt.length :"+valuesToEncrypt.length);
			for (int i = 0; i< xmlTags.length ; i++)
			{
				String tag = new String();
				tag = xmlTags[i].toUpperCase();
				System.out.println("tag :"+tag);
				
				String[] xmlTag_values = new String[3];
				String valToEncrpt = valuesToEncrypt[i];
				for(int j=0 ; j < 16 - ((valuesToEncrypt[i].length())%16); j++)
				{
					valToEncrpt = valToEncrpt + " ";
				}//FOR j

				xmlTag_values = CryptoUtil.getEncyptedData(valToEncrpt);	
				String encrypted_val = xmlTag_values[0];
				System.out.println("encrypted_val :"+encrypted_val);
				String wrapped_key = xmlTag_values[1];
				System.out.println("wrapped_key :"+wrapped_key);
                String wrapped_IV = xmlTag_values[2];
				String[] xmlTag_MACValues = new String[2];
				xmlTag_MACValues = CryptoUtil.getEncyptedMACData(valuesToEncrypt[i]);
				String encrpted_MAC = xmlTag_MACValues[0];
				System.out.println("encrpted_MAC :"+encrpted_MAC);
				String wrapped_MAC_key = xmlTag_MACValues[1];
				System.out.println("wrapped_MAC_key :"+wrapped_MAC_key);
				try
				{
						hd.startElement("", "", xmlTags[i]+"", atts);	//<CUSTID>
						atts.clear();
						hd.startElement("", "", xmlTags[i]+"_VAL", atts);	//<CUSTID_VAL>
						hd.characters(encrypted_val.toCharArray(), 0,encrypted_val.toCharArray().length);
						hd.endElement("", "", xmlTags[i]+"_VAL");			//<CUSTID_VAL> END
						System.out.println("List"+"1");
						
						hd.startElement("", "", xmlTags[i]+"_KEY", atts);	//<CUSTID_KEY>
						hd.characters(wrapped_key.toCharArray(), 0,wrapped_key.toCharArray().length);
						hd.endElement("", "", xmlTags[i]+"_KEY");			//<CUSTID_KEY> END
						System.out.println("List"+"2");
						
						hd.startElement("", "", xmlTags[i]+"_MAC", atts);	//<CUSTID_MAC>
						hd.characters(encrpted_MAC.toCharArray(), 0,encrpted_MAC.toCharArray().length);
						hd.endElement("", "", xmlTags[i]+"_MAC");			//<CUSTID_MAC> END
						System.out.println("List"+"3");
						
						hd.startElement("", "", xmlTags[i]+"_MAC_KEY", atts);	//<CUSTID_MAC_KEY>
						hd.characters(wrapped_MAC_key.toCharArray(), 0,wrapped_MAC_key.toCharArray().length);
						hd.endElement("", "", xmlTags[i]+"_MAC_KEY");			//<CUSTID_MAC_KEY> END
						System.out.println("List"+"4");

                        hd.startElement("", "", xmlTags[i] + "_IV", atts); // <CUSTID_IV>
                        hd.characters(wrapped_IV.toCharArray(), 0, wrapped_IV.toCharArray().length);
                        hd.endElement("", "", xmlTags[i] + "_IV"); // <CUSTID_IV>
                    // END
                    //System.out.println("List" + "5");
					
						hd.endElement("", "", xmlTags[i]+""); //<CUSTID> END
						
						
				}//try
				catch (Exception e)
				{
					// TODO: handle exception
					System.out.println("In generateXML Exc-002 :"+e.toString());
					return null;
				}//catch
				
			}// FOR - i
			try {
				hd.endElement("", "", "XMLDATA");		
				hd.endDocument();
				writer.close();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} ////<XMLDATA> END
			
			return writer.toString();
		}

		public static boolean getMACString(String para_mac, String para_macKey, String decryptedString)
		{
				boolean result=false;
				System.out.println("***** CryptoUtil *** getMACString()");
			 	byte[] MAC_arr = Base64.decodeBase64(para_mac.getBytes());
	            byte[] macKey = Base64.decodeBase64(para_macKey.getBytes());
	            originalKey = new SecretKeySpec(macKey,"AES");
	            String gen_MAC=new String(generateMAC(decryptedString,originalKey));
	            String originalMac=new String(MAC_arr);
	            if(gen_MAC.equals(originalMac))
	            {//if 1
	            	 result=true;
	            }//if 1
	            return result;
		}//end getStringMAC		
		
		public static String[] getEncyptedData(String param_value)
		{
			String encryptedString[]=new String[3];
			try
			{//	Security.addProvider(new BouncyCastleProvider());
				System.out.println("***** CryptoUtil ***IN getDecryptedValue()"+param_value);

				keyGen=getKeyGenerator("AES");
				_key=keyGen.generateKey();
				originalKey=new SecretKeySpec(_key.getEncoded(),"AES");

                KeyGenerator keyGenerator = KeyGenerator.getInstance(CryptoMngr.ALGORITHM);
                keyGenerator.init(AES_128);
                //Generate Key
                SecretKey key = keyGenerator.generateKey();
                //Initialization vector
                SecretKey IV = keyGenerator.generateKey();

				byte[] staticKey = "AristADepT@12345".getBytes();
                byte[] encrypted_byte =CryptoMngr.encrypt(staticKey,IV.getEncoded(), param_value.getBytes());
				//byte[] encrypted_byte=Crypto.encrypt(param_value,originalKey);
				//byte[] encrypted_string =Base64.encodeBase64(encrypted_byte);
                byte[] encrypted_string = org.apache.commons.codec.binary.Base64.encodeBase64(encrypted_byte);
				//System.out.println(""+encrypted_string);
                encryptedString[0] = new String(encrypted_string);
                //System.out.println("***** CryptoUtil *** encryptedString[0]"+ encryptedString[0]);
                //byte[] key_byte = originalKey.getEncoded();
                // byte[] encoded_key =Base64.encodeBase64(key_arr);
                byte[] encoded_key = org.apache.commons.codec.binary.Base64.encodeBase64(key.getEncoded());

                encryptedString[1] = new String(encoded_key);

                byte[] encoded_IV = org.apache.commons.codec.binary.Base64.encodeBase64(IV.getEncoded());
                encryptedString[2] = new String(encoded_IV);
			}
			catch(Exception e)
			{
				System.out.println("CryptoUtil.getEncyptedData()"+"Exception:"+e);
				encryptedString[0]="fail";
			}
				return encryptedString;
		}//end getDecryptedValue

		public static String[] readXML(String xmlString, String[] XMLTags) {

			System.out.println("****** START readXML *******");
				//Security.addProvider(new BouncyCastleProvider());
			System.out.println("******xmlString  ******* :"+xmlString);
			int length = 0;
			length = XMLTags.length;
			length = length * 4;
			int c = 0;
			String result = "";
			String[] allData = new String[length];
			System.out.println("****** 111 *******");
			StringReader reader = new StringReader(xmlString);
			System.out.println("****** 222 *******");
			String xmlName = "data.xml";
			System.out.println("Environment.getExternalStorageState() :"+ Environment.getExternalStorageState());
			
			File xmlFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),xmlName);
			System.out.println("****** 333 *******");

			FileWriter f1;
			PrintWriter pw;
			try {

				//File f=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),xmlName);
				f1 = new FileWriter(xmlFile);
				pw = new PrintWriter(f1);
				pw.println("----------");
				pw.close();

			} catch (Exception e) {
				System.out.println("Error writefile null :" + e);

			}
			xmlFile.delete();
			System.out.println("****** 444 *******");
			try {
				System.out.println("****** 555 *******");
				FileOutputStream xmlfos = new FileOutputStream(xmlFile);
				
				System.out.println("****** 666 *******");
				while ((c = reader.read()) != -1)
					xmlfos.write((byte) c);
				//////xmlfos.close();
					System.out.println("****** 666 - 1 *******");
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					System.out.println("****** 666 - 2 *******");
					DocumentBuilder db = dbf.newDocumentBuilder();
					System.out.println("****** 666 - 3 *******");
					Document doc = db.parse(xmlFile);
					System.out.println("****** 666 - 4 *******");
					doc.getDocumentElement().normalize();
					System.out.println("****** 666 - 5 *******");

				int count = 0;
				System.out.println("****** 777 *******");
				for (int k = 0; k < XMLTags.length; k++) {// 1
					System.out.println("XMLTags[k]:" + k + " " + XMLTags[k]);
					NodeList cl = doc.getElementsByTagName(XMLTags[k]);
					String[] xmlTag_values = new String[5];
					for (int j = 0; j < cl.getLength(); j++) {// 2
						Node cn = cl.item(j);
						NodeList acl = cn.getChildNodes();
						for (int i = 0; i < acl.getLength(); i++) {// 3
							if (acl.item(i).getNodeType() == Node.ELEMENT_NODE) {// 4
								Node n = acl.item(i).getFirstChild();
								String tag = XMLTags[k];
								System.out.println("tag:" + tag);
								String fieldName = acl.item(i).getNodeName();
								System.out.println("fieldName:" + fieldName);
								if (fieldName.equals(tag + "_VAL")) {
									//allData[count] = n.getNodeValue().trim();
									xmlTag_values[0] = n.getNodeValue().trim();
								} else if (fieldName.equals(tag + "_KEY")) {
									//allData[count] = n.getNodeValue().trim();
									xmlTag_values[1] = n.getNodeValue().trim();
								} else if (fieldName.equals(tag + "_MAC")) {
									//allData[count] = n.getNodeValue().trim();
									xmlTag_values[2] = n.getNodeValue().trim();
								} else if (fieldName.equals(tag + "_MAC_KEY")) {
									//allData[count] = n.getNodeValue().trim();
									xmlTag_values[3] = n.getNodeValue().trim();
								}
                                else if (fieldName.equals(tag + "_IV"))
                                {
                                    //allData[count] = n.getNodeValue().trim();
                                    xmlTag_values[4] = n.getNodeValue().trim();
                                }
								count++;

							}// 4
						}// 3
					}// 2
					//String decVal = getDecryptedData(xmlTag_values);
	String strVal = xmlTag_values[0];
				String strKey = xmlTag_values[1];
				String strIV = xmlTag_values[4];

				System.out.println("*****Debug Before decode\nstrVal: "+strVal);
				System.out.println("strKey: "+strKey);
				System.out.println("strIV: "+strIV);

				byte[] arrVal = Base64.decodeBase64(strVal.getBytes());
				byte[] arrKey = Base64.decodeBase64(strKey.getBytes());
				byte[] arrIV = Base64.decodeBase64(strIV.getBytes());

				System.out.println("*****Debug After decode\narrVal: "+new String(arrVal));
				System.out.println("arrKey: "+new String(arrKey));
				System.out.println("arrIV: "+new String(arrIV));
                //    String decVal = new String(CryptoMngr.decrypt(xmlTag_values[1].getBytes(),xmlTag_values[4].getBytes(), xmlTag_values[0].getBytes()));//getDecryptedData(xmlTag_values);
					byte[] staticArrKey = "AristADepT@12345".getBytes();
					String decVal = new String(CryptoMngr.decrypt(staticArrKey,arrIV, arrVal));
					allData[k] = decVal;
				}// 1

				System.out.println("*********** XML DATA **********");
				for (String s : allData)
				{
					System.out.println("" + s);
				}

			}
			catch (Exception e)
			{
				System.out.println("Exception in readXML():" + e);
				allData[0]="FAILED#";
			}
			System.out.println("****** EXIT readXML *******");
			return allData;

		}// end readXML
		
		
		public static String[] getEncyptedMACData(String para_value)
		{
			String encodedString[]=new String[3];
			try
			{
			    	//Security.addProvider(new BouncyCastleProvider());
				System.out.println("***** CryptoUtil *** getEncyptedMACData()");
				keyGen=getKeyGenerator("AES");
				_key=keyGen.generateKey();
				originalKey=new SecretKeySpec(_key.getEncoded(),"AES");
				byte[] mac_byte=generateMAC(para_value,originalKey);
				//byte[] encoded_mac =Base64.encodeBase64(mac_arr);
				byte[] encoded_mac = Base64.encodeBase64(mac_byte);
				
				encodedString[0]=new String(encoded_mac);
				System.out.println("***** CryptoUtil.getEncyptedMACData() *** encodedString[0]"+encodedString[0]);
				
				byte[] key_arr=originalKey.getEncoded();
			//	byte[] encoded_Mackey =Base64.encodeBase64(key_arr);
				byte[] encoded_Mackey = Base64.encodeBase64(key_arr);
				
				encodedString[1]=new String(encoded_Mackey);
				System.out.println("***** CryptoUtil.getEncyptedMACData() *** encodedString[1]"+encodedString[1]);
			}
			catch(Exception e)
			{
				System.out.println("CryptoUtil.getEncyptedMACData()"+"Exception:"+e);
				encodedString[0]="fail";
			}
				return encodedString;
		}//end getStringMAC	


		public static KeyGenerator getKeyGenerator(String algorithm)
		{

			//Log.i("=== in KeyGenerator ====","1111");
			try
			{
				//Log.i("=== in KeyGenerator ====","2222");
				KeyGenerator keyGenerator= KeyGenerator.getInstance(algorithm,"BC");
				//Log.i("=== in KeyGenerator ====", "3333");
				keyGenerator.init(128);
				//keyGenerator.init(Cipher.WRAP_MODE);
				return keyGenerator;
			}

			catch(Exception e)
			{
				//Log.i("== LoginActivity.getKey():Exception ==",e.getMessage());
				return null;
			}
		}//end getKeyGenerator
		public static byte[] generateMAC(String strToEncrypt, Key key1)
		{
		try
		{
			//Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			Mac mac = Mac.getInstance("HMAC-MD5","BC");
			mac.init(key1);
			return mac.doFinal(strToEncrypt.getBytes());
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("Crypto Class"+"Exception in generateMAC():"+e);
			return null;
		}
	}
}//end GeneralClass
