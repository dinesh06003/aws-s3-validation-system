package com.s3.validation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.s3.validation.service.BucketService;


@RestController
@RequestMapping("s3bucket")
@CrossOrigin("*")
public class BucketController {

	@Autowired
	BucketService service;


	@GetMapping("/add/{bucketName}")
	public ResponseEntity<String> createBucket(@PathVariable String bucketName) {
		String result = service.createBucket(bucketName);
		if(result.contains("error")){
			return new ResponseEntity<>(result, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping(path = "/upload/file/{bucketName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file,
			@PathVariable String bucketName) {
		String uploadFileResult = service.uploadFile(file, bucketName);
		if(uploadFileResult.contains("error")){
			return new ResponseEntity<>(uploadFileResult, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(uploadFileResult, HttpStatus.OK);
	}


	@GetMapping("/get/{bucketName}")
	public ResponseEntity<String> getBucketItems(@PathVariable String bucketName) {
		String result = service.getBucketItems(bucketName);
		if(result.contains("error")){
			return new ResponseEntity<>(result, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	
	@DeleteMapping(path="/delete/file/{bucketName}/{fileName}")
	public ResponseEntity<String> deleteFile(@PathVariable String bucketName,@PathVariable String fileName)
	{
		String deleteResult = service.deleteFile(bucketName, fileName);
		if(deleteResult.contains("Failed")){
			return new ResponseEntity<>(deleteResult, HttpStatus.INTERNAL_SERVER_ERROR);
		}else if(deleteResult.contains("not")){
			return new ResponseEntity<>(deleteResult, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(deleteResult,HttpStatus.OK);
	}

	@DeleteMapping("/delete/bucket/{bucketName}")
	public ResponseEntity<String> deleteBucket(@PathVariable String bucketName) {
		String deleteBucketResult = service.deleteBucket(bucketName);
		if(deleteBucketResult.contains("does not exist")){
			return new ResponseEntity<>(deleteBucketResult, HttpStatus.BAD_REQUEST);
		}else if(deleteBucketResult.contains("contains")){
			return new ResponseEntity<>(deleteBucketResult, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(deleteBucketResult, HttpStatus.OK);
	}

}
