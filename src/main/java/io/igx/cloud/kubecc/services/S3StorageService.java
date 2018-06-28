package io.igx.cloud.kubecc.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3StorageService {
	
	@Value("${bucket.name}")
	String bucketName;

	private AmazonS3 s3client;
	
	private static final String SUFFIX = "/";

	private Logger logger = LoggerFactory.getLogger(S3StorageService.class);

	public S3StorageService(){
		s3client = AmazonS3ClientBuilder.defaultClient();
	}
	
	@SuppressWarnings("deprecation")
	public void s3Store(String folderName, String fileName, File file){
	    
	   logger.info("uploading s3 object {} on folder {}", fileName, folderName);
		/*s3client.createBucket(bucketName);
		
		// list buckets
		for (Bucket bucket : s3client.listBuckets()) {
			System.out.println(" - " + bucket.getName());
		}*/
		
		// create folder into bucket
		createFolder(bucketName, folderName, s3client);
		
		// upload file to folder and set it to public
		fileName = folderName + SUFFIX + fileName;
		s3client.putObject(new PutObjectRequest(bucketName, fileName, 
				file)
				.withCannedAcl(CannedAccessControlList.PublicRead));

	    
	}
	
	public static void createFolder(String bucketName, String folderName, AmazonS3 client) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
				folderName + SUFFIX, emptyContent, metadata);
		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}

}
