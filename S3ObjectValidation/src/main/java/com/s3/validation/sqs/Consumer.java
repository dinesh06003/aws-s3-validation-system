package com.s3.validation.sqs;

import com.s3.validation.sns.SNSService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Consumer {

	@Autowired
	private SNSService snsService;



	@SqsListener(value="successQueue")
	public void mySuccess(String message)
	{
		System.out.println("Success Message received:"+message);
	}


	@SqsListener(value="failureQueue")
	public void myFailure(String message)
	{
		snsService.pubFailureTopic(message);
		System.out.println("Failed Message received:"+message);
	}
}
