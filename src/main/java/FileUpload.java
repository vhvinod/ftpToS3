import java.io.File;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

public class FileUpload {
	
	public static void UploadtoS3(String StrFile, String Bucket_name)
			throws AmazonServiceException, AmazonClientException, InterruptedException {

		System.out.println("File Upload Started ");

		 AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	              .withCredentials(new InstanceProfileCredentialsProvider(false))
	              .build();
		TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(s3Client)
				.withMultipartUploadThreshold((long) (5 * 1024 * 1025)).build();

		File file = new File(StrFile);

		Upload upload = xfer_mgr.upload(Bucket_name, file.getName(), new File(file.getPath()));
		while (upload.isDone() == false) {
			upload.getDescription();
			upload.getState();
			upload.getProgress().getBytesTransferred();
		}
		upload.waitForCompletion();
		xfer_mgr.shutdownNow();
		System.out.println("File Upload Completed");
	}
}