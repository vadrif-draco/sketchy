package asu.foe.sketchy.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OCRService {

	public static String send(File file, String fileName) throws IOException {

		@JsonIgnoreProperties(ignoreUnknown = true)
		record ParsedResult(String ParsedText) {}

		@JsonIgnoreProperties(ignoreUnknown = true)
		record MyResponse(ArrayList<ParsedResult> ParsedResults) {}

		RequestBody reqBody = new MultipartBody.Builder()
					.setType(MultipartBody.FORM)
					.addFormDataPart("file", fileName, RequestBody.create(file, MediaType.parse("")))
					.addFormDataPart("language", "eng")
					.addFormDataPart("filetype", "png")
					.addFormDataPart("detectOrientation", "true")
					.addFormDataPart("OCREngine", "2")
					.build();

		Request req = new Request.Builder()
					.url("https://api.ocr.space/parse/image")
					.post(reqBody)
					.header("apiKey", "d043c9198f88957") // Nardine's API Key
					.build();

		OkHttpClient client = new OkHttpClient();

		try {
			Response response = client.newCall(req).execute();
			System.out.println(response);
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
			MyResponse myResponse = new ObjectMapper().readValue(response.body().string(), MyResponse.class);
			return myResponse.ParsedResults.get(0).ParsedText;
		} catch (IOException e) {
			e.printStackTrace();
			return "error";
		}
	}

}
