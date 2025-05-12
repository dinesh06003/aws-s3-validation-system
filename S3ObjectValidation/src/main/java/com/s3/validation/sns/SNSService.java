package com.s3.validation.sns;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SNSService {

    @Autowired
    private AmazonSNS sns;

    @Value("${sns.topic.arn}")
    private String SNSTopicARN;


    public void pubFailureTopic(String message) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(message);

            JsonNode record = root.path("requestPayload").path("Records").get(0);
            JsonNode s3 = record.path("s3");
            JsonNode object = s3.path("object");
            JsonNode bucket = s3.path("bucket");

            String bucketName = bucket.path("name").asText();
            String fileName = object.path("key").asText();
            long fileSize = object.path("size").asLong();
            String eventTime = record.path("eventTime").asText();
            String sourceIp = record.path("requestParameters").path("sourceIPAddress").asText();

            String errorMessage = root.path("responsePayload").path("errorMessage").asText();
            String errorType = root.path("responsePayload").path("errorType").asText();
            String functionArn = root.path("requestContext").path("functionArn").asText();
            int retryCount = root.path("requestContext").path("approximateInvokeCount").asInt();

            // Format email message
            String formatedMessage = String.format(
                    "Hello,\n\n" +
                            "This is to inform you that a file upload to your S3 bucket triggered a Lambda function which failed to process the file.\n\n" +
                            "📄 File Details\n" +
                            "- Bucket Name: %s\n" +
                            "- File Name: %s\n" +
                            "- File Size: %d bytes\n" +
                            "- Upload Time: %s\n" +
                            "- Uploader IP: %s\n\n" +
                            "⚠️ Error Information\n" +
                            "- Error Message: %s\n" +
                            "- Error Type: %s\n" +
                            "- Failed Function ARN: %s\n" +
                            "- Invocation Attempt: %d (Retries exhausted)\n\n" +
                            "Please review the uploaded file format and size, and reprocess if appropriate.\n\n" +
                            "Regards,\nAWS Monitoring System",
                    bucketName, fileName, fileSize, eventTime, sourceIp,
                    errorMessage, errorType, functionArn, retryCount);
            PublishRequest request = new PublishRequest(SNSTopicARN, formatedMessage);
            String subject = "Invalid Entry in the S3 Bucket";
            request.setSubject(subject);
            sns.publish(request);
        }catch (Exception e){
            System.out.println("Failed to Send email: " + e.getMessage());
        }
    }
}
