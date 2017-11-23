import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.*;
import java.security.*;
import java.util.Iterator;

import com.sun.xml.internal.fastinfoset.util.CharArray;
import org.bouncycastle.bcpg.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.*;


public class Applet extends JApplet implements ActionListener {

	String[] members = {"Sidi", "Nikita"};
	ButtonGroup roleButtons = new ButtonGroup();
	JButton submit = new JButton("Create Signature");
	JButton open = new JButton("Open File");
	JPanel[] p = new JPanel[6];
	JRadioButton[] radios = new JRadioButton[2];
	JPasswordField pwd = new JPasswordField(20);
	JTextArea selected_file = new JTextArea();
	JFileChooser fc = new JFileChooser();
//    FileInputStream pkFile = new FileInputStream("./private.key");
	private char[] passphrase = null;
//	FileInputStream msgFile = null;

    public Applet() throws FileNotFoundException {
    }

    public void init() {
		getContentPane().setLayout(new GridLayout(6, 1));
		for (int i=0; i<6; i++) {
			p[i] = new JPanel();
			if ( i != 0 ) p[i].setLayout(new FlowLayout(FlowLayout.LEFT));
			if ( i == 4 ) p[i].setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(p[i]);
		}

		// add even listeners on the buttons
		pwd.addActionListener(this);
		submit.addActionListener(this);
		open.addActionListener(this);

		// title
		p[0].add(new JLabel("Create Digital Signature"));
		// add upload label and upload selections
		p[1].add(new JLabel("Select Message "));
		p[1].add(open);

        p[2].add(new JLabel("Filename:"));
        p[2].add(selected_file);

		// add member selection label and radio buttons
		p[3].add(new JLabel("Select Member "));

		for (int i=0; i<radios.length; i++) {
			radios[i] = new JRadioButton(members[i]);
			radios[i].addActionListener(this);
			roleButtons.add(radios[i]);
			p[2].add(radios[i]);
		}
		// add pass phrase label and text filed
		p[4].add(new JLabel("Passphrase "));
		p[4].add(pwd);


		// add the submit button
		p[5].add(submit);

	}
	public void actionPerformed(ActionEvent e) {

		//Handle upload action.
		if (e.getSource() == open) {
			int returnVal = fc.showOpenDialog(Applet.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
                try {
//                    msgFile = new FileInputStream(file);
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            } else {

			}
		}
		//Handle create signature action
		else if (e.getSource() == submit) {
            passphrase = pwd.getPassword();
            selected_file.removeAll();

            selected_file.setText(new String(passphrase));
            selected_file.setText(System.getProperty("user.dir"));
            if (passphrase != null) {
                signFile();
            }
		}

	}

	private void signFile(){
        //FileInputStream msgFile, FileInputStream pkFile
        Security.addProvider(new BouncyCastleProvider());

        try {
            String inFile = "./cns-test/test.txt";
//            FileInputStream docFile = new FileInputStream(inFile);
            FileInputStream pkFile = new FileInputStream("./cns-test/private.key");

            InputStream instream = PGPUtil.getDecoderStream(pkFile);

            PGPSecretKeyRingCollection skrc = new PGPSecretKeyRingCollection(
                    instream, new JcaKeyFingerprintCalculator());

            Iterator<PGPSecretKeyRing> kri = skrc.getKeyRings();
            PGPSecretKey key = null;
            while (key == null && kri.hasNext()) {
                PGPSecretKeyRing keyRing = (PGPSecretKeyRing)kri.next();
                Iterator<PGPSecretKey> ki = keyRing.getSecretKeys();
                while (key == null && ki.hasNext()) {
                    PGPSecretKey k = (PGPSecretKey)ki.next();
                    if (k.isSigningKey()) { key = k; }
                }
            }
            if ( key == null )
                throw new IllegalArgumentException("Can't find key");

            PGPPrivateKey pgpPrivKey = key.extractPrivateKey(
                    new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(
                            passphrase));

            PGPSignatureGenerator sGen = new PGPSignatureGenerator(
                    new JcaPGPContentSignerBuilder(key.getPublicKey().getAlgorithm(),
                            PGPUtil.SHA1).setProvider("BC"));
            //sig generator
            sGen.init(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);

            PGPCompressedDataGenerator cGen =
                    new PGPCompressedDataGenerator(PGPCompressedData.ZLIB);
            FileOutputStream out = new FileOutputStream("./cns-test/outfile.bpg");


            BCPGOutputStream bOut = new BCPGOutputStream(cGen.open(out));
            FileInputStream fIn = new FileInputStream(inFile);
            int ch;
            while ((ch = fIn.read()) >= 0) { sGen.update((byte)ch); }

            sGen.generate().encode(bOut);
            cGen.close();
            out.close();
            fIn.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }






//        PGPCompressedDataGenerator cGen =
//                new PGPCompressedDataGenerator(PGPCompressedData.ZLIB);
//        FileOutputStream out = new FileOutputStream(args[0] + ".bpg");
//        BCPGOutputStream bOut = new BCPGOutputStream(cGen.open(out));
//        FileInputStream fIn = new FileInputStream(args[0]);
//        int ch;
//        while ((ch = fIn.read()) >= 0) { sGen.update((byte)ch); }
//        sGen.generate().encode(bOut);
//        cGen.close();
//        out.close();
//        fIn.close();

    }
}
