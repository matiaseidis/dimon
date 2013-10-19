package org.test.streaming;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import junit.framework.Assert;

import org.junit.Test;

public class CachoPusherTest {

	/**
	 * Asume que el dimon esta ejecutando con la configuracion por default y que
	 * el directorio determinado por la property video.dir.cachos en
	 * alt-test-conf.properties contiene la pelicula determinada por
	 * test.video.file.name del mismo archivo. Pushea la pelicula de a cachos de
	 * 64M, y luego la stremea. El archivo ssandonga1.mp4 en el working dir
	 * contiene la pelicula original, luego de ser pusheada y strimiada.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPush() throws Exception {

		Conf conf = new Conf("/alt-test-conf.properties");
		new DefaultMovieSharingPlanInterpreter(conf).interpret(new DummyMovieRetrievalPlan("videoId", conf));
		
		Thread.sleep(20000);

		BufferedOutputStream baos = new BufferedOutputStream(new FileOutputStream(new File("sandonga1.mp4")));
		DummyMovieRetrievalPlan plan = new DummyMovieRetrievalPlan(conf.get("test.video.file.name"), conf);
		CompositeMovieRetrievalPlan compositeMovieRetrievalPlan = new CompositeMovieRetrievalPlan(plan, 6);
		new CompositePlanInterpreter(conf.getCachosDir(), conf.getTempDir()).interpret(compositeMovieRetrievalPlan, baos, null);
		baos.flush();
		baos.close();
		
		File streamedData = new File("sandonga1.mp4");
		Assert.assertTrue(streamedData.exists());
		Assert.assertEquals(Integer.parseInt(conf.get("test.video.file.size")), streamedData.length());

		DigestInputStream dis = new DigestInputStream(new FileInputStream(streamedData), MessageDigest.getInstance("MD5"));
		byte[] buffer = new byte[1204 * 256];
		while (dis.read(buffer) != -1) {
		}
		byte[] digest = dis.getMessageDigest().digest();
		StringBuilder sb = new StringBuilder();
		for (byte b : digest) {
			sb.append(String.format("%02X", b));
		}
		Assert.assertEquals(conf.get("test.video.md5").toLowerCase(), sb.toString().toLowerCase());

	}
}
