package org.test.streaming;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

public class CachoWriter implements ChannelFutureListener {

	protected static Log log = LogFactory.getLog(CachoWriter.class);

	int total;
	int written;
	int desiredBytesps = -1;

	public CachoWriter() {
		this(-1);
	}

	public CachoWriter(int desiredBytesps) {
		this.desiredBytesps = desiredBytesps;
	}

	public void uploadCacho(Channel output, InputStream input, int lenght) throws IOException {
		this.total = lenght;
		int b = 1024 * 256 * 2;
		try {
			log.debug("Uploading cacho...");
			int s = lenght / b;
			int r = lenght % b;
			long randomJitter = (long) ((b / desiredBytesps) * 1000 * 0.2);
			for (int i = 0; i < s; i++) {
				long currentTimeMillis = System.currentTimeMillis();
				int write = write(output, input, b);
				long t = System.currentTimeMillis() - currentTimeMillis;
				if (this.desiredBytesps != -1) {
					double shouldTake = (write / (double) desiredBytesps) * 1000;
					long latency = (long) (shouldTake - t);
					// if (randomJitter < 0) {
					// randomJitter = Math.max(-randomJitter, latency -
					// -randomJitter);
					// }
					// latency += randomJitter;
					// randomJitter = -randomJitter;
					if (latency > 0) {
						Thread.sleep(latency);
						System.out.println(write + " took " + t + ", should take " + shouldTake + "ms at " + this.desiredBytesps + ", slept for " + latency);
					}
				}
			}

			if (r != 0) {
				long currentTimeMillis = System.currentTimeMillis();
				int write = write(output, input, r);
				long t = System.currentTimeMillis() - currentTimeMillis;
				if (this.desiredBytesps != -1) {
					int shouldTake = (write / desiredBytesps) * 1000;
					long millis = (shouldTake - t);
					if (millis > 0) {
						Thread.sleep(millis);
						System.out.println(write + " took " + t + ", should take " + shouldTake + "s at " + this.desiredBytesps + ", slept for " + millis);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			input.close();
		}
		log.debug("Uploaded " + this.written + " bytes.");
	}

	private int write(Channel output, InputStream input, int b) throws IOException, InterruptedException {
		ChannelBuffer outBuffer = ChannelBuffers.buffer(b);
		outBuffer.writeBytes(input, outBuffer.writableBytes());
		int readableBytes = outBuffer.readableBytes();
		output.write(outBuffer).sync();
		written += readableBytes;
		double progress = ((double) written / (double) total) * 100d;
		System.out.println("Written " + written + " " + (total - written) + " to go ( " + progress + "%)");
		return readableBytes;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
	}
}
