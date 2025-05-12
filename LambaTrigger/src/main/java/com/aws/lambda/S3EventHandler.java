package com.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;

public class S3EventHandler implements RequestHandler<S3Event, String> {

	private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB

	private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

	@Override
	public String handleRequest(S3Event event, Context context) {

			S3EventNotificationRecord record = event.getRecords().get(0);
			String bucketName = record.getS3().getBucket().getName();
			String fileName = record.getS3().getObject().getKey();

			System.out.println("Bucket: " + bucketName);
			System.out.println("File: " + fileName);

			// Get file metadata
			ObjectMetadata metadata = s3Client.getObjectMetadata(bucketName, fileName);
			long fileSize = metadata.getContentLength();

			String message;
			if (isSupportedFile(fileName) && fileSize <= MAX_FILE_SIZE_BYTES) {
				System.out.println("Inside success case");
				message = "✅ SUCCESS: File '" + fileName + "' uploaded to '" + bucketName + "' (size: " + fileSize + " bytes).";
				System.out.println("Message pushed to SQS SuccessQueue.");
				return message;
			} else {
				System.out.println("Inside failure case");
				message = "❌ FAILURE: File '" + fileName + "' is unsupported or too large (" + fileSize + " bytes).";
				System.out.println("Message pushed to SQS failureQueue.");
				throw new RuntimeException(message);

			}

	}

	private boolean isSupportedFile(String fileName) {
		return fileName.endsWith(".txt") || fileName.endsWith(".pdf") || fileName.endsWith(".docx") || fileName.endsWith(".csv");
	}
}