package codes.mona.measurementsdownloader.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import codes.mona.measurementsdownloader.model.UiLogger;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

/** Adapter class for AWS S3 SDK. If the Cloud changes then only this class has to be altered. */
public class S3Adapter {

	private final S3Client s3;
	private final String bucket;

	public S3Adapter(Region region, String bucket) {
		s3 = S3Client.builder().region(region).build();
		this.bucket = bucket;
	}

	public List<File> downloadFiles(String prefix, File downloadFolder) {
		ListObjectsV2Request listObjectsReq = ListObjectsV2Request.builder().bucket(bucket).prefix(prefix).build();
		ListObjectsV2Iterable listRes = s3.listObjectsV2Paginator(listObjectsReq);
		List<File> result = new ArrayList<>();

		for (S3Object obj : listRes.contents()) {

			if (!obj.key().endsWith("/")) {

				UiLogger.getInstance().log("Downloading: " + obj.key());
				File file = new File(downloadFolder + obj.key().substring(obj.key().lastIndexOf('/')));
				s3.getObject(GetObjectRequest.builder().bucket(bucket).key(obj.key()).build(),
						ResponseTransformer.toFile(file));
				result.add(file);
			}
		}

		return result;
	}

}
