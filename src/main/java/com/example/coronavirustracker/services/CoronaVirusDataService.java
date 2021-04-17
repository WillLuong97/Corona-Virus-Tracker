package com.example.coronavirustracker.services;

import com.example.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service //Tell Spring that this file is a service
public class CoronaVirusDataService {
    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_US.csv";

    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    //this function will make an http call to the csv file url.
    @PostConstruct //Execute the below code once the application start
    @Scheduled(cron = "* * 1 * * *")    //run a method on a regular basis, this is telling spring to run the below method every second, run this code once everyday
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();
        //making the http client request to the API link
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
        //handling response
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        //print out the httpResponse
//        System.out.println(httpResponse.body());
        //Create an instance of the string reader to read through the entire response body
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        //This will read the csv by the header:
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

        for (CSVRecord record : records) {
            //create an instance of the location stats and populate its attribute with new data
            LocationStats locationStats = new LocationStats();
            locationStats.setState(record.get("Province_State"));
            locationStats.setCountry(record.get("Country_Region"));
            locationStats.setLatestTotalCases(Integer.parseInt(record.get(record.size() - 1)));
            int latestDay = Integer.parseInt(record.get(record.size()-1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));

            locationStats.setLatestTotalCases(latestDay);
            locationStats.setDiffFromPreviousDate(latestDay - prevDayCases);
            newStats.add(locationStats);
        }
        this.allStats = newStats;
    }
}
