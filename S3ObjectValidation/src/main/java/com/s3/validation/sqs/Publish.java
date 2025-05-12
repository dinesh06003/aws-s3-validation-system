
package com.s3.validation.sqs;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("SQS")
public class Publish {

	@Autowired
	private SqsTemplate sqs;

	@Value("${sqs.url}")
	private String sqsUrl;

	@PostMapping("/publish/{msg}")
	public ResponseEntity<String> postMessage(@PathVariable("msg") String message) {
		sqs.send(sqsUrl, message);
		System.out.println("Inside Publish message");
		return new ResponseEntity<>("Message Published", HttpStatus.OK);
	}

}
