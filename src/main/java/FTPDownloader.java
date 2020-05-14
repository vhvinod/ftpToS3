import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPDownloader {

	FTPClient ftp = null;

	public FTPDownloader(String host, String user, String pwd) throws Exception {
		ftp = new FTPClient();
		ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		int reply;
		ftp.connect(host);
		reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			throw new Exception("Exception in connecting to FTP Server");
		}
		ftp.login(user, pwd);
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		ftp.enterLocalPassiveMode();
	}

	public void downloadFile(String remoteFilePath, String localFilePath) {
		try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
			this.ftp.retrieveFile(remoteFilePath, fos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		if (this.ftp.isConnected()) {
			try {
				this.ftp.logout();
				this.ftp.disconnect();
			} catch (IOException f) {
				// do nothing as file is already downloaded from FTP server
			}
		}
	}

	public static void main(String[] args) {
		try {

			System.out.println(
					"Started -->" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date()));
			String StrFile = args[0];
			String Bucket_name = args[1];
			FTPDownloader ftpDownloader = new FTPDownloader("<ftp_url>", "<ftp_username>", "ftp_password");
			Path path = Paths.get(StrFile);

			// call getFileName() and get FileName path object
			Path fileName = path.getFileName();
			System.out.println("FTP Download Started -->"
					+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date()));
			ftpDownloader.downloadFile("<ftp_path>" + fileName, StrFile);
			System.out.println(
					"Started -->" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date()));
			System.out.println("FTP File downloaded successfully");
			ftpDownloader.disconnect();
			System.out.println("FTP Download Completed -->"
					+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date()));
			FileUpload.UploadtoS3(StrFile,Bucket_name);
			System.out.println("S3 Uploaded Completed -->"
					+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date()));
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
}