package com.diasjoao.bolanatv.model;

import java.util.List;

public class Section {

    private String sectionTitle;
    private List<Game> sectionItems;

    public Section(String sectionTitle, List<Game> sectionItems) {
        this.sectionTitle = sectionTitle;
        this.sectionItems = sectionItems;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public List<Game> getSectionItems() {
        return sectionItems;
    }

    public void setSectionItems(List<Game> sectionItems) {
        this.sectionItems = sectionItems;
    }
}
