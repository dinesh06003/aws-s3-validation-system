# 🗂️ AWS S3 File Upload Validation System

This project implements an automated file validation workflow using **AWS S3**, **Lambda**, **SQS**, **SNS**, and a **Spring Boot** microservice.

## 📌 Overview

When a user uploads a file to an S3 bucket, an event is triggered. The system uses an AWS Lambda function to validate the uploaded file based on predefined rules. The validation result is then sent to either a success or failure queue. A Spring Boot application listens to the **Failure Queue**, and sends an email alert to the user using AWS SNS.

---

## ✅ Validation Rules

The Lambda function performs the following checks:

- Accepted file types:
  - `.docx`
  - `.txt`
  - `.pdf`
  - `.csv`
- File size must be **less than 5MB**

---

## 🔁 Workflow

1. **User/Client** uploads a file to the **S3 Bucket**
2. **S3 Event Trigger** invokes the **AWS Lambda Function**
3. **Lambda** validates:
   - ✅ If valid → sends message to **Success Queue**
   - ❌ If invalid → sends message to **Failure Queue**
4. **Spring Boot Application** listens to **Failure Queue**
5. **Spring Boot App** sends a message to an **SNS Topic**
6. **SNS Topic** sends an **email alert** to the user

---

## 🧱 Tech Stack

- **AWS S3** – File storage
- **AWS Lambda** – File validation logic
- **AWS SQS** – Message queuing (Success & Failure Queues)
- **AWS SNS** – Notification service
- **Spring Boot** – Backend listener & SNS integration

---

## 🛠️ Setup Instructions

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/dinesh06003/aws-s3-validation-system.git
```

### 2️⃣ Lambda Setup

1. Open the **LambdaTrigger** project in IntelliJ.
2. Build the Lambda package:

     ```bash
     ./mvnw clean package
     ```

3. Go to the AWS Lambda Console.
4. Create a new function or use an existing one.
5. Upload the packaged `.jar` file from the `target/` directory.
6. Set the handler and runtime environment (e.g., Java 11).
7. Attach required IAM roles with access to S3 and SQS.

### 3️⃣ Spring Boot Setup

1. Open the **S3ObjectValidation** project in IntelliJ.
2. Update the `application.properties` file with the required configurations.
3. Run the Spring Boot application and test the API endpoints.