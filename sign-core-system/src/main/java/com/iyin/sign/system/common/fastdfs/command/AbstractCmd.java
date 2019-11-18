package com.iyin.sign.system.common.fastdfs.command;

import com.iyin.sign.system.common.fastdfs.FastdfsClient;
import com.iyin.sign.system.model.code.FileResponseCode;
import com.iyin.sign.system.model.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Arrays;

public abstract class AbstractCmd<T> implements Command<T> {
	
	protected byte requestCmd;
	protected byte responseCmd;
	protected long responseSize;
	protected byte[] body1;
	protected long body2Len = 0L;

	@Autowired
	private FastdfsClient fastdfsClient;

	protected void request(OutputStream socketOut)throws IOException {
		try {
			socketOut.write(getRequestHeaderAndBody1());
		}catch (SocketException se){
			if(null!=fastdfsClient){
				fastdfsClient.close();
			}
			throw new BusinessException(FileResponseCode.WRITE_FASTDFS_ERROR);
		}
	}
	
	protected void request(OutputStream socketOut,InputStream is)throws IOException {
		request(socketOut);
		int readBytes;
		byte[] buff = new byte[256 * 1024];
		while ((readBytes = is.read(buff)) >= 0) {
			if (readBytes == 0) {
				continue;
			}
			socketOut.write(buff, 0, readBytes);
		}
		is.close();
	}
	
	protected byte[] getRequestHeaderAndBody1() {
		if(body1==null){
			body1 = new byte[0];
		}
		byte[] header = new byte[FDFS_PROTO_PKG_LEN_SIZE + 2 + body1.length];
		Arrays.fill(header, (byte) 0);
		byte[] hexLen = long2buff(body1.length+body2Len);
		System.arraycopy(hexLen, 0, header, 0, hexLen.length);
		header[PROTO_HEADER_CMD_INDEX] = requestCmd;
		header[PROTO_HEADER_STATUS_INDEX] = (byte) 0;
		//copy body
		System.arraycopy(body1, 0, header, FDFS_PROTO_PKG_LEN_SIZE + 2, body1.length);
		return header;
	}
	
	protected Response response(InputStream socketIn)throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int code = response(socketIn,os);
		return new Response(code, os.toByteArray());
		
	}
	
	protected int response(InputStream socketIn,OutputStream os) throws IOException {
		byte[] header = new byte[FDFS_PROTO_PKG_LEN_SIZE + 2];
		
		int bytes = socketIn.read(header);
		
		if (bytes != header.length) {
			throw new IOException("recv package size " + bytes + " != "	+ header.length);
		}

		if (header[PROTO_HEADER_CMD_INDEX] != responseCmd) {
			throw new IOException("recv cmd: " + header[PROTO_HEADER_CMD_INDEX]	+ " is not correct, expect cmd: " + responseCmd);
		}

		if (header[PROTO_HEADER_STATUS_INDEX] != SUCCESS_CODE) {
			return header[PROTO_HEADER_STATUS_INDEX];
		}

		long respSize = buff2long(header, 0);
		if (respSize < 0) {
			throw new IOException("recv body length: " + respSize + " < 0!");
		}

		if (responseSize >= 0 && respSize != responseSize) {
			throw new IOException("recv body length: " + respSize + " is not correct, expect length: " + responseSize);
		}
		

		byte[] buff = new byte[2 * 1024];
		int totalBytes = 0;
		int remainBytes = (int) respSize;

		while (totalBytes < respSize) {
			int len = remainBytes;
			if(len>buff.length){
				len = buff.length;
			}
			
			if ((bytes = socketIn.read(buff, 0, len)) < 0) {
				break;
			}
			os.write(buff, 0, bytes);
			totalBytes += bytes;
			remainBytes -= bytes;
		}

		if (totalBytes != respSize) {
			throw new IOException("recv package size " + totalBytes + " != "+ respSize);
		}
		os.close();
		return SUCCESS_CODE;
	}
	
	public static byte[] long2buff(long n) {
		byte[] bs;

		bs = new byte[8];
		bs[0] = (byte) ((n >> 56) & 0xFF);
		bs[1] = (byte) ((n >> 48) & 0xFF);
		bs[2] = (byte) ((n >> 40) & 0xFF);
		bs[3] = (byte) ((n >> 32) & 0xFF);
		bs[4] = (byte) ((n >> 24) & 0xFF);
		bs[5] = (byte) ((n >> 16) & 0xFF);
		bs[6] = (byte) ((n >> 8) & 0xFF);
		bs[7] = (byte) (n & 0xFF);

		return bs;
	}
	
	public static int buff2int(byte[] bs, int offset) {
		return (((int) (bs[offset] >= 0 ? bs[offset] : 256 + bs[offset])) << 56)
				| (((int) (bs[offset + 1] >= 0 ? bs[offset + 1]
						: 256 + bs[offset + 1])) << 48)
				| (((int) (bs[offset + 2] >= 0 ? bs[offset + 2]
						: 256 + bs[offset + 2])) << 40)
				| (((int) (bs[offset + 3] >= 0 ? bs[offset + 3]
						: 256 + bs[offset + 3])) << 32);
	}
	
	public static long buff2long(byte[] bs, int offset) {
		return (((long) (bs[offset] >= 0 ? bs[offset] : 256 + bs[offset])) << 56)
				| (((long) (bs[offset + 1] >= 0 ? bs[offset + 1]
						: 256 + bs[offset + 1])) << 48)
				| (((long) (bs[offset + 2] >= 0 ? bs[offset + 2]
						: 256 + bs[offset + 2])) << 40)
				| (((long) (bs[offset + 3] >= 0 ? bs[offset + 3]
						: 256 + bs[offset + 3])) << 32)
				| (((long) (bs[offset + 4] >= 0 ? bs[offset + 4]
						: 256 + bs[offset + 4])) << 24)
				| (((long) (bs[offset + 5] >= 0 ? bs[offset + 5]
						: 256 + bs[offset + 5])) << 16)
				| (((long) (bs[offset + 6] >= 0 ? bs[offset + 6]
						: 256 + bs[offset + 6])) << 8)
				| ((long) (bs[offset + 7] >= 0 ? bs[offset + 7]
						: 256 + bs[offset + 7]));
	}
	
	public static String getIpAddress(byte[] bs, int offset) {
		if (bs[0] == 0 || bs[3] == 0){
			return "";
		}
		int n;
		StringBuilder sbResult = new StringBuilder(16);
		for (int i = offset; i < offset + 4; i++) {
			n = (bs[i] >= 0) ? bs[i] : 256 + bs[i];
			if (sbResult.length() > 0) {
				sbResult.append(".");
			}
			sbResult.append(String.valueOf(n));
		}
		return sbResult.toString();
	}
	
	protected class Response {
		
		private int code;
		private byte[] data;
		
		public Response(int code) {
			super();
			this.code = code;
		}
		public Response(int code, byte[] data) {
			super();
			this.code = code;
			this.data = data;
		}
		public boolean isSuccess(){
			return code == SUCCESS_CODE;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public byte[] getData() {
			return data;
		}
		public void setData(byte[] data) {
			this.data = data;
		}
		
	}
	
}
