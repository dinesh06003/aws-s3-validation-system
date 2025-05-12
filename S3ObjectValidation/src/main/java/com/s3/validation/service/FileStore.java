package com.s3.validation.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.model.ObjectListing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;


@Service
public class FileStore {

	Logger logger = LogManager.getLogger(this.getClass().getName());

	@Autowired
	private AmazonS3 amazonS3;


	public String createBucket(String bucketName) {
		logger.info("Inside method createBucket");
		try {
			if (bucketAlreadyExists(bucketName)) {
				logger.error("bucket already exist");
				return "Bucket already exist with Name: " + bucketName;
			}else{
				amazonS3.createBucket(bucketName);
			}

		} catch (AmazonS3Exception s3Exception) {
			logger.error("Unable to create bucket :" + s3Exception.getMessage());
			return "Conflict/error to create bucket : " + s3Exception.getMessage();
		}
		return "Bucket created with name:" + bucketName;
	}


	private boolean bucketAlreadyExists(String bucketName) {
		logger.info("Inside method bucketAlreadyExists");
		return amazonS3.doesBucketExistV2(bucketName);
	}


	public String uploadFiletoBucket(MultipartFile multiPart,String bucketName) throws Exception {
		logger.info("Inside method upload");
		String fileName = multiPart.getOriginalFilename();
		File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + multiPart.getOriginalFilename());
		multiPart.transferTo(convFile);
		try {
			if (amazonS3.doesObjectExist(bucketName, fileName)) {
				logger.warn("File already exists in bucket and updating with new version: " + fileName);
				return "File already exists in bucket and updating with new version: " + fileName; // Or handle overwriting/versioning based on your use case
			}
			amazonS3.putObject(bucketName, convFile.getName(), convFile);
			
		} catch (AmazonS3Exception s3Exception) {
			logger.error("Unable to upload file :" + s3Exception.getMessage());
			return "Error in uploading file name: " + fileName;
		}
		return "File Uploaded Successfully: " + fileName;
	}

	public String deleteBucket(String bucketName) {
		try {
			if (!amazonS3.doesBucketExistV2(bucketName)) {
				return "⚠️ Bucket '" + bucketName + "' does not exist.";
			}

			int objectCount = 0;
			ObjectListing objectListing = amazonS3.listObjects(bucketName);

			while (true) {
				objectCount += objectListing.getObjectSummaries().size();
				if (objectListing.isTruncated()) {
					objectListing = amazonS3.listNextBatchOfObjects(objectListing);
				} else {
					break;
				}
			}

			if (objectCount > 0) {
				return "⚠️ Bucket '" + bucketName + "' contains " + objectCount + " object(s). Please empty it before deletion.";
			}

			amazonS3.deleteBucket(bucketName);
			return "✅ Bucket '" + bucketName + "' was successfully deleted. It contained " + objectCount + " object(s).";

		} catch (AmazonS3Exception e) {
			return "❌ Failed to delete bucket: " + e.getMessage();
		}
	}


	public String deleteFile(String bucketName, String fileName) {
		try {
			if (amazonS3.doesObjectExist(bucketName, fileName)) {
				amazonS3.deleteObject(bucketName, fileName);
				return "✅ File '" + fileName + "' was successfully deleted from bucket '" + bucketName + "'.";
			} else {
				return "⚠️ File '" + fileName + "' does not exist in bucket '" + bucketName + "'.";
			}
		} catch (AmazonS3Exception e) {
			return "❌ Failed to delete file: " + e.getMessage();
		}
	}


	public String getBucketItems(String bucketName) {
		try {
			if (!amazonS3.doesBucketExistV2(bucketName)) {
				return "⚠️ Bucket '" + bucketName + "' does not exist.";
			}

			List<String> objectKeys = new ArrayList<>();
			ObjectListing objectListing = amazonS3.listObjects(bucketName);

			while (true) {
				objectListing.getObjectSummaries().forEach(s3Object -> objectKeys.add(s3Object.getKey()));

				if (objectListing.isTruncated()) {
					objectListing = amazonS3.listNextBatchOfObjects(objectListing);
				} else {
					break;
				}
			}

			if (objectKeys.isEmpty()) {
				return "📂 Bucket '" + bucketName + "' is empty.";
			}

			StringBuilder result = new StringBuilder("✅ Bucket '" + bucketName + "' contains " + objectKeys.size() + " object(s):\n");
			for (String key : objectKeys) {
				result.append(" - ").append(key).append("\n");
			}

			return result.toString();

		} catch (AmazonS3Exception e) {
			return "❌ Failed to retrieve objects: " + e.getMessage();
		}
	}

}
