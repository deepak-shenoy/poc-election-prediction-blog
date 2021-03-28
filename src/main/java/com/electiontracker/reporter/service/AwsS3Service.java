package com.electiontracker.reporter.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.electiontracker.reporter.entities.ForecastReport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AwsS3Service {

    public static final String ELECTION_DATA_BUCKET_NAME = "election2020reporter";
    public static final String AUDIT_BUCKET_NAME = "election2020auditlog";
    private static final Regions CLIENT_REGION = Regions.US_EAST_1;

    private AmazonS3 s3Client;

    private @Autowired
    ObjectMapper objectMapper;

    @PostConstruct
    private void initialization() {
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(AwsS3Service.CLIENT_REGION)
                .build();
    }

    public boolean saveDataToS3Object(String bucketName, String stringObjKeyName, String content) {  // throws IOException
        try {
            // Upload a text string as a new object.
            s3Client.putObject(bucketName, stringObjKeyName, content);
            log().info("Successfully save file to s3 bucket: " + bucketName + " for: " + stringObjKeyName);

        } catch (AmazonServiceException e) {
            log().error("The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.", e);
            e.printStackTrace();
            return false;
        } catch (SdkClientException e) {
            log().error("Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<ForecastReport> getSavedListOfForecastReports() {
        List<String> fileFileObjects = getObjectListFromS3Bucket();
        List<ForecastReport> forecastReportList = new ArrayList<>();
        if (fileFileObjects == null) {
            return null;
        }
        for (String fileObject : fileFileObjects) {
            try {
                final S3Object s3Object = s3Client.getObject(AwsS3Service.ELECTION_DATA_BUCKET_NAME, fileObject);
                final InputStreamReader streamReader = new InputStreamReader(s3Object.getObjectContent(), StandardCharsets.UTF_8);
                final BufferedReader reader = new BufferedReader(streamReader);
                final List<String> fileLines = reader.lines().collect(Collectors.toList());
                String fileObjectContents = String.join("", fileLines);
                ForecastReport forecastReport = objectMapper.readValue(fileObjectContents, ForecastReport.class);
                forecastReport.setObjectKeyName(fileObject);
                forecastReportList.add(forecastReport);
                log().info("Successfully read object: " + fileObject +" from the s3 bucket.");
            } catch (JsonProcessingException e) {
                log().error("Could not convert the file contents into Json format for filename: " + fileObject, e);
                e.printStackTrace();
                return null;
            } catch (AmazonServiceException e) {
                log().error("The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.", e);
                e.printStackTrace();
                return null;
            } catch (SdkClientException e) {
                log().error("Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.", e);
                e.printStackTrace();
                return null;
            }
        }
        return forecastReportList;
    }

    public ForecastReport getSavedForecastReport(String objectKeyName) {
        ForecastReport forecastReport = new ForecastReport();
        try {
            final S3Object s3Object = s3Client.getObject(AwsS3Service.ELECTION_DATA_BUCKET_NAME, objectKeyName);
            final InputStreamReader streamReader = new InputStreamReader(s3Object.getObjectContent(), StandardCharsets.UTF_8);
            final BufferedReader reader = new BufferedReader(streamReader);
            final List<String> fileLines = reader.lines().collect(Collectors.toList());
            String fileObjectContents = String.join("", fileLines);
            forecastReport = objectMapper.readValue(fileObjectContents, ForecastReport.class);
            forecastReport.setObjectKeyName(objectKeyName);
            log().info("Successfully read object: " + objectKeyName +" from the s3 bucket.");
        } catch (JsonProcessingException e) {
            log().error("Could not convert the file contents into Json format for filename: " + objectKeyName, e);
            e.printStackTrace();
            return null;
        } catch (AmazonServiceException e) {
            log().error("The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.", e);
            e.printStackTrace();
            return null;
        } catch (SdkClientException e) {
            log().error("Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.", e);
            e.printStackTrace();
            return null;
        }
        return forecastReport;
    }


    public List<String> getObjectListFromS3Bucket() {
        List<String> objectsInS3Bucket = new ArrayList<>();

        try {
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request().withBucketName(AwsS3Service.ELECTION_DATA_BUCKET_NAME);
            objectsInS3Bucket = s3Client
                    .listObjectsV2(listObjectsV2Request)
                    .getObjectSummaries()
                    .stream().map(file -> file.getKey())
                    .collect(Collectors.toList());
            log().info("Successfully read objects from the s3 bucket.");
        } catch (AmazonServiceException e) {
            log().error("The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.", e);
            e.printStackTrace();
            return null;
        } catch (SdkClientException e) {
            log().error("Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.", e);
            e.printStackTrace();
            return null;
        }
        return objectsInS3Bucket;
    }

    protected Logger log() {
        return log;
    }

}
