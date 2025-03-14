package com.ignit.internship.dto.cakrawala;

import java.util.List;

import com.ignit.internship.model.cakrawala.Info;

public class InfoResponse {

    private String description;

    private String salaryRange;

    private String criteria;

    private List<String> skills;

    private List<String> relatedStudies;

    private List<String> careerOpportunities;

    private List<String> responsibilites;

    private String question;

    private List<String> options;

    private Integer answer;

    private String tag;

    private List<Long> imageIds;

    public InfoResponse(
        String description, 
        String salaryRange, 
        String criteria, 
        List<String> skills,
        List<String> relatedStudies, 
        List<String> careerOpportunities, 
        List<String> responsibilites,
        String question,
        List<String> options, 
        Integer answer,
        String tag,
        List<Long> imageIds
    ) {
        this.description = description;
        this.salaryRange = salaryRange;
        this.criteria = criteria;
        this.skills = skills;
        this.relatedStudies = relatedStudies;
        this.careerOpportunities = careerOpportunities;
        this.responsibilites = responsibilites;
        this.options = options;
        this.answer = answer;
        this.tag = tag;
        this.imageIds = imageIds;
    }

    public InfoResponse(Info info) {
        this(
            info.getDescription(),
            info.getSalaryRange(),
            info.getCriteria(),
            info.getSkills(),
            info.getRelatedStudies(),
            info.getCareerOpportunities(),
            info.getResponsibilites(),
            info.getQuestion(),
            info.getOptions(),
            info.getAnswer(),
            info.getTag().getName(),
            info.getImageIds()
        );
    }

    public String getDescription() {
        return description;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public String getCriteria() {
        return criteria;
    }

    public List<String> getSkills() {
        return skills;
    }

    public List<String> getRelatedStudies() {
        return relatedStudies;
    }

    public List<String> getCareerOpportunities() {
        return careerOpportunities;
    }

    public List<String> getResponsibilites() {
        return responsibilites;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public Integer getAnswer() {
        return answer;
    }

    public String getTag() {
        return tag;
    }

    public List<Long> getImageIds() {
        return imageIds;
    }
}
