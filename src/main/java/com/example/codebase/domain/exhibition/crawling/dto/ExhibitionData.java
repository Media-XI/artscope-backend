package com.example.codebase.domain.exhibition.crawling.dto;

import com.example.codebase.domain.exhibition.entity.EventSchedule;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.location.entity.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ExhibitionData {
    private Exhibition exhibition;
    private Location location;
    private List<EventSchedule> eventSchedules;
}
