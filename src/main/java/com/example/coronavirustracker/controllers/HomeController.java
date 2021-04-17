package com.example.coronavirustracker.controllers;

import com.example.coronavirustracker.models.LocationStats;
import com.example.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
/* This file will return the HTML template to display the information we received*/

@Controller
public class HomeController {
    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model){
        List<LocationStats> allStats = coronaVirusDataService.getAllStats();
        //counting all total cases from the data stream and then sum them all up
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        model.addAttribute("locationStats", allStats); //Passing the model data into the HTML format.
        model.addAttribute("totalReportedCases", totalReportedCases); //Passing the model data into the HTML format.
        return "home";
    }
}
