import java.io.*;
import java.security.*;
import java.util.Iterator;
import org.bouncycastle.bcpg.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.*;

public class Sign {
    public static void main(String[] args) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
        FileInputStream fin = new FileInputStream(args[1]);
        System.out.println(fin);
        InputStream in = PGPUtil.getDecoderStream(fin);

        PGPSecretKeyRingCollection skrc = new PGPSecretKeyRingCollection(
                in, new JcaKeyFingerprintCalculator());
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
                        args[2].toCharArray()));

        PGPSignatureGenerator sGen = new PGPSignatureGenerator(
                new JcaPGPContentSignerBuilder(key.getPublicKey().getAlgorithm(),
                        PGPUtil.SHA1).setProvider("BC"));

        sGen.init(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);

        PGPCompressedDataGenerator cGen =
                new PGPCompressedDataGenerator(PGPCompressedData.ZLIB);
        FileOutputStream out = new FileOutputStream(args[0] + ".bpg");
        BCPGOutputStream bOut = new BCPGOutputStream(cGen.open(out));
        FileInputStream fIn = new FileInputStream(args[0]);
        int ch;
        while ((ch = fIn.read()) >= 0) { sGen.update((byte)ch); }
        sGen.generate().encode(bOut);
        cGen.close();
        out.close();
        fIn.close();
    }
}