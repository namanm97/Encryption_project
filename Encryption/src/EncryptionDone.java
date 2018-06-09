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

public class EncryptionDone 
{
	private SecretKeySpec secretKey;
	private Cipher cipher;

	public EncryptionDone(String secret, int length, String algorithm)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException    // Error Throw
	{
		byte[ ] key = new byte[length];												
		key = fixSecret(secret, length );									
		this.secretKey = new SecretKeySpec( key, algorithm );						 // contains key and the algorithm used 
		this.cipher = Cipher.getInstance( algorithm );								 // cipher hold instance of algorithm
	}

	public EncryptionDone()
	{
	
	}

	private byte[ ] fixSecret(String s, int length) throws UnsupportedEncodingException 
	{
		if (s.length() < length) 												      // Check String size min. 16 
		{
			int missingLength = length - s.length();
			for (int i = 0; i < missingLength; i++) 								 // padding of bits
			{
				s += " ";
			}
		}
		return s.substring(0, length).getBytes("UTF-8"); 							   // Return Key with padding	
	}	

	public void encryptFile(File f)
			throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException 
	{
		long startTime = System.nanoTime();											   // Start timer
		System.out.println("Encrypting file: " + f.getName());
		this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);						   // Encrypt with this key 
	    this.writeToFile(f);
	    long endTime = System.nanoTime();
	    long duration = (endTime - startTime);
	    System.out.println("Time taken is : "+ duration+" nanoseconds");
	}

	public void writeToFile(File f) throws IOException, IllegalBlockSizeException, BadPaddingException 
	{
		FileInputStream in = new FileInputStream(f);
		byte[ ] input = new byte[ (int) f.length() ];
		in.read(input);

		FileOutputStream out = new FileOutputStream(f);
		byte[ ] output = this.cipher.doFinal(input);
		out.write(output);

		out.flush();															// Flushes this output stream and forces any buffered output bytes to be written out
		out.close();
		in.close();
	}
	
	public void main1() 
	{
		File dir = new File("src/cryptodir");
		File[ ] filelist = dir.listFiles(); 			    								// Extraction of all files in that directory 
		String key = UUID.randomUUID().toString();							  			    // Random key generator 
        key=key.replace("-", "");											            	// Eliminate "-"
        System.out.println("Key:"+key);
        try 
        {
            BufferedWriter out = new BufferedWriter(new FileWriter("test.txt")); 		    // Open test.txt
            out.write(key);														 		    // Key stored in test.txt
            out.close();
        }
        catch (IOException e)
        {
            System.out.println("Exception");
        }
        
		EncryptionDone ske;													
		try 
		{
			ske = new EncryptionDone(key, 16, "AES");			            		 		// 16 Bit Block , AES Encryption

			int choice = -2;
			while (choice != -1) 													
			{
				String[ ] options = { "Encrypt All", "Back"};							
				choice = JOptionPane.showOptionDialog(null, "Select an option", "Options", 0,
						JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

				switch (choice) 
				{
				case 0:																		// Encrypt all
					Arrays.asList(filelist).forEach(file -> {								// Store file list in array
						try 
						{
							 PDDocument document = PDDocument.load(file);
						     if(document.isEncrypted())
						      	{
						    	 	System.out.println("The file "+file.getName() +" is already Password Protected. No Encryption Required");
						      	}
						     else
							  ske.encryptFile(file);
						     document.close();
						} 
						catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
								| IOException e) 
								{
									System.err.println("Couldn't encrypt " + file.getName() + ": " + e.getMessage());
								}
					});
					System.out.println("Files encrypted successfully");
					break;
				default:																	// Exit
					choice = -1;
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