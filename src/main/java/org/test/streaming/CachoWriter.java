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
		int b = this.desiredBytesps > 0 ? this.desiredBytesps : 1024 * 256;
		try {
			log.debug("Uploading cacho...");
			int s = lenght / b;
			int r = lenght % b;
			for (int i = 0; i < s; i++) {
				long t = System.currentTimeMillis();
				write(output, input, b);
				long delay = System.currentTimeMillis() - t;
				if (this.desiredBytesps > 0) {
					Thread.sleep(1000 - delay);
				}
			}

			if (r != 0) {
				long t = System.currentTimeMillis();
				write(output, input, r);
				long delay = System.currentTimeMillis() - t;
				if (this.desiredBytesps > 0) {
					Thread.sleep(1000 - delay);
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
		output.write(outBuffer).sync().sync();
		written += readableBytes;
		double progress = ((double) written / (double) total) * 100d;
		System.out.println("Written " + written + " " + (total - written) + " to go ( " + progress + "%)");
		return readableBytes;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
	}
}
