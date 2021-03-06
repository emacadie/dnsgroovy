package info.shelfunit.DNS

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Security
import java.security.Signature

import spock.lang.Specification

import org.xbill.DNS.DNSSEC.Algorithm

public class DNSSECWithProviderSpockTest extends Specification {
  
  private static final String SIGNATURE_ALGORITHM = "SHA1withRSA"
  private static final String KEY_ALGORITHM = "RSA"
  int algorithm = Algorithm.RSASHA1
  byte[] toSign = "The quick brown fox jumped over the lazy dog.".getBytes()

  public void setup() { }

  public void tearDown() { }

  def "testSignSoftware"() throws Exception {

    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM)
    keyPairGenerator.initialize(512)
    KeyPair keyPair = keyPairGenerator.generateKeyPair()

    Signature signer = Signature.getInstance(SIGNATURE_ALGORITHM)
    signer.initSign(keyPair.getPrivate())
    signer.update(toSign)
    byte[] signature = signer.sign()
    expect: 
    signature != null

    // verify the signature
    when:
    Signature verifier = Signature.getInstance(SIGNATURE_ALGORITHM)
    verifier.initVerify(keyPair.getPublic())
    verifier.update(toSign)
    boolean verify = verifier.verify(signature)
    then:
    verify

  }
}
