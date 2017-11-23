import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.*;
import java.security.*;
import java.util.Iterator;

//import com.sun.xml.internal.fastinfoset.util.CharArray;
import org.bouncycastle.bcpg.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.*;


public class Applet extends JApplet implements ActionListener {
    String[] members = {"Sidi", "Nikita"};
    ButtonGroup roleButtons = new ButtonGroup();
    JButton submit = new JButton("Create Signature");
    JButton open_document = new JButton("Open File");
    JButton open_pkey = new JButton("Open Keychain");
    JPanel[] p = new JPanel[6];
    JRadioButton[] radios = new JRadioButton[2];
    JPasswordField pwd = new JPasswordField(20);
    JTextArea selected_document = new JTextArea();
    JTextArea selected_pkey = new JTextArea();
    JFileChooser fc_document = new JFileChooser();
    JFileChooser fc_pkey = new JFileChooser();
    JTextArea info_message = new JTextArea();


	private char[] passphrase = null;
	private String doc_path = null;
	private String key_path = null;


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
		open_document.addActionListener(this);
		open_pkey.addActionListener(this);

		// title
		p[0].add(new JLabel("Create Digital Signature"));
		// add upload label and upload selections
		p[1].add(new JLabel("Select Message "));
		p[1].add(open_document);
		p[1].add(selected_document);

        p[2].add(new JLabel("Select Keychain "));
        p[2].add(open_pkey);
        p[2].add(selected_pkey);
		// add member selection label and radio butt
		// add pass phrase label and text filed
        p[3].add(new JLabel("Info:"));
        p[3].add(info_message);

		p[4].add(new JLabel("Passphrase "));
		p[4].add(pwd);


		// add the submit button
		p[5].add(submit);

	}
	public void actionPerformed(ActionEvent e) {

		//Handle selection of message action.
		if (e.getSource() == open_document) {
			int returnVal = fc_document.showOpenDialog(Applet.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc_document.getSelectedFile();
				doc_path = file.getAbsolutePath();
				selected_document.setText(doc_path);
            } else {

			}
		}

		//Handle selection of keychain
		else if (e.getSource() == open_pkey){
            int returnVal = fc_pkey.showOpenDialog(Applet.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc_pkey.getSelectedFile();
                key_path = file.getAbsolutePath();
                selected_pkey.setText(key_path);
            } else {

            }
        }
		//Handle create signature action
		else if (e.getSource() == submit) {
            passphrase = pwd.getPassword();
            if (passphrase != null) {
                info_message.setText(signFile(doc_path,key_path,passphrase));
            } else {
                info_message.setText("Passphrase is empty!");
            }
		}

	}

	private String signFile(String doc_path, String key_path, char[] passphrase){
        Security.addProvider(new BouncyCastleProvider());
        try {

            FileInputStream pkFile = new FileInputStream(key_path);

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

            FileOutputStream out = new FileOutputStream(doc_path+".bpg");


            BCPGOutputStream bOut = new BCPGOutputStream(cGen.open(out));
            FileInputStream fIn = new FileInputStream(doc_path);
            int ch;
            while ((ch = fIn.read()) >= 0) { sGen.update((byte)ch); }

            sGen.generate().encode(bOut);
            cGen.close();
            out.close();
            fIn.close();
            return "Success: " + doc_path + ".bpg writtent to disk!";
        } catch (Exception ex){
            ex.printStackTrace();

            return "Wrong Key";
        }



    }
}
