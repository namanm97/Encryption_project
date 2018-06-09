import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import org.apache.pdfbox.pdmodel.*;

public class DecryptionDone 
{
	private SecretKeySpec secretKey;
	private Cipher cipher;

	public DecryptionDone(String secret, int length, String algorithm)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException 	// Error Throw
	{
		byte[ ] key = new byte[length];
		key = fixSecret(secret, length);
		this.secretKey = new SecretKeySpec(key, algorithm);								// contains key and the algorithm used
		this.cipher = Cipher.getInstance(algorithm);									// cipher hold instance of algorithm
	}
	
	public DecryptionDone()
	{
	}

	private byte[ ] fixSecret(String s, int length) throws UnsupportedEncodingException 
	{
		if (s.length() < length) 												// Check String size min. 16
		{
			int missingLength = length - s.length();
			for (int i = 0; i < missingLength; i++) 			   				// padding of bits
			{
				s += " ";
			}
		}
		return s.substring(0, length).getBytes("UTF-8");						// Return Key with padding
	}

	public void decryptFile(File f)
			throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException 
	{
		
		long startTime = System.nanoTime();										// Start timer
		System.out.println("Decrypting file: " + f.getName());
		this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);					// Decrypt with this key
		this.writeToFile(f);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); 
	    System.out.println("Time taken is : "+ duration+" nanoseconds");
	
	}
	public void writeToFile(File f) throws IOException, IllegalBlockSizeException, BadPaddingException 
	{
		FileInputStream in = new FileInputStream(f);
		byte[] input = new byte[(int) f.length()];
		in.read(input);

		FileOutputStream out = new FileOutputStream(f);
		byte[] output = this.cipher.doFinal(input);
		out.write(output);

		out.flush();													// Flushes this output stream and forces any buffered output bytes to be written out
		out.close();
		in.close();
	}
	
	
	public void main2() {
		File dir = new File("src/cryptodir");										// Extraction of all files in that directory
		File[] filelist = dir.listFiles();
	    String key="";
	    try
	    {
	    BufferedReader br=new BufferedReader(new FileReader(new File("test.txt")));
	    String st;
	    while((st=br.readLine())!=null)
	    {
	    	key=key+st;															// store contents of test.txt in key
	    }
	    br.close();
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
		DecryptionDone ske;
		try 
		{
			ske = new DecryptionDone(key, 16, "AES");

			int choice = -2;
			while (choice != -1) {
				String[ ] options = {"Decrypt All", "Back" };
				choice = JOptionPane.showOptionDialog(null, "Select an option", "Options", 0,
						JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

				switch (choice) 
				{
				case 0:																				// Decrypt 
					Arrays.asList(filelist).forEach(file -> {
						try 
						{
							try
							{
								PDDocument document = PDDocument.load(file);
				     			if(document.isEncrypted())
						         {
				     				System.out.println("The File is Password Protected. No Decryption Required");
						         }
				     			document.close();
						    }
							catch(Exception e) 
							{
								ske.decryptFile(file);
							}
						} 
						
						catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException e) 
						{
							System.err.println("Couldn't decrypt " + file.getName() + ": " + e.getMessage());
						}
					});
					System.out.println("Files decrypted successfully");
					break;
				
				default:
					choice = -1;																// Exit

					break;
				}
			}
		} 
		
		catch (UnsupportedEncodingException ex) 
		{
			System.err.println(ex.getMessage());
		} 
		
		catch (NoSuchAlgorithmException | NoSuchPaddingException e) 
		{
			System.err.println(e.getMessage());
		}
	}
}